package jdonref_lucene_poc.business;

import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import jdonref_lucene_poc.entity.Search;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Julien
 */
public class AdresseBusiness
{
    int hitsPerPage = 5;
    float limit = 1.0f;

    QueryParser qp = null;
    IndexSearcher searcher = null;
    
    public int getHitsPerPage() {
        return hitsPerPage;
    }

    public void setHitsPerPage(int hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }
    
    public AdresseBusiness(Directory index) throws IOException
    {
            Analyzer analyzer = new FrenchAnalyzer(Version.LUCENE_45);
            
            IndexReader reader = IndexReader.open(index);
            searcher = new IndexSearcher(reader);
            
            qp = new QueryParser(Version.LUCENE_45, "fullName", analyzer);
    }
    
    public String addTilde(String query)
    {
        String[] split = query.split(" ");
        
        String res = "";
        for(int i=0;i<split.length;i++)
        {
            if (i>0)
                res += " ";
            if (!split[i].matches("[0-9]*"))
                res += split[i]+"~";
            else
                res += split[i];
        }
        return res;
    }
    
    public void showHits(Search s,IndexSearcher searcher) throws IOException
    {
        ScoreDoc[] hits = s.res;
        System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                float score = hits[i].score;
                Document d = searcher.doc(docId);
                String[] fullNames = d.getValues("toString");
                System.out.print((i + 1)+ " ("+score+") :");
                for(int j=0;j<fullNames.length;j++)
                    System.out.println(fullNames[j]);
               // System.out.println(searcher.explain(s.q, hits[i].doc));
            }
    }
    
    public boolean isOk(ScoreDoc[] hits, float limit)
    {
        if (hits.length==0) return false;
        
        if (hits[0].score>=limit) return true;
        
        return false;
    }
    
    public Document[] valide(String querystr) throws IOException, ParseException
    {
        System.out.println("-------------");
        System.out.println("Cherche "+querystr);
        querystr = qp.escape(querystr); // warning : valideApprox ajoute ~ à la fin des mots. Les effets de bords n'ont pas été traités.
        
        Search s = valideExact(querystr);
        ScoreDoc[] hits = s.res;
        
        if (!isOk(hits, limit))
        {
            s = valideApprox(querystr);
            hits = s.res;
        }
        
        Document[] docs = new Document[hits.length];
        for(int i=0;i<hits.length;i++)
        {
            docs[i] = searcher.doc(hits[i].doc);
        }
        
        showHits(s, searcher);
        
        return docs;
    }
    
    public Search valideExact(String querystr) throws IOException, ParseException
    {
        Search s = new Search();
        
        s.collector = TopScoreDocCollector.create(hitsPerPage, true);
        querystr = getQueryStrExact(querystr);
        System.out.println("Cherche exactement "+querystr);
        long start = Calendar.getInstance().getTimeInMillis();
        
        s.q = qp.parse(querystr);
        System.out.println(s.q.toString());
        s.searcher = searcher;
        searcher.search(s.q, s.collector);
        
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println((end-start)+" millis");
        
        s.res = s.collector.topDocs().scoreDocs;
        
        return s;
    }
    
    public Search valideApprox(String querystr) throws IOException, ParseException
    {
        Search s = new Search();
        
        s.collector = TopScoreDocCollector.create(hitsPerPage, true);
        querystr = addTilde(querystr);
        querystr = getQueryStrExact(querystr);
        System.out.println("Cherche approximativement " + querystr);
        long start = Calendar.getInstance().getTimeInMillis();
        s.q = qp.parse(querystr);
        s.searcher = searcher;
        searcher.search(s.q, s.collector);
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println((end - start) + " millis");

        s.res = s.collector.topDocs().scoreDocs;

        return s;
    }
    
    public boolean isInt(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }
    
    public String getQueryStrExact(String find)
    {
        Hashtable<String,Boolean> hash = new Hashtable<String,Boolean>();
        String str = "";
        
        boolean isThereInt = false;
        
        String[] splitted = find.split(" ");
        for(int i=0;i<splitted.length;i++)
        {
            String stri = splitted[i];
            
            if (hash.get(stri)==null)
            {
                hash.put(stri,true);
            
                if (isInt(stri))
                {
                    isThereInt = true;
                    //str += "(";
                    if (stri.length()==5)
                        str += " code_insee:"+stri+"^2";
                    if (stri.length()==5)
                        str += " code_postal:"+stri+"^2";
                    str += " code_departement:"+stri;
                    if (i==0)
                        str += "^0.5";
                    if (i>0)
                        str += "^2";
                    str += " code_arrondissement:"+stri;
                    if (i==0)
                        str += "^0.5";
                    if (i>0)
                        str += "^2";
                    str += " numero:"+stri;
                    if (i==0)
                        str += "^2.5";
                    str += " numero:AUCUN";
                    //str += ")";
                }
                else
                {
                    str += " fullName:"+stri+"^10";
                }
            }
        }
        
        if (!isThereInt)
            str += " numero:AUCUN";
        
        return str;
    }
}