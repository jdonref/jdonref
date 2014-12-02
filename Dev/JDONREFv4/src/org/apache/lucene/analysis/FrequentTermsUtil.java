package org.apache.lucene.analysis;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.index.Term;
import org.elasticsearch.common.hppc.LongArrayList;
import org.elasticsearch.common.hppc.cursors.LongCursor;
import org.elasticsearch.common.util.CollectionUtils;

/**
 *
 * @author akchana
 */
public class FrequentTermsUtil {
    
    static final String defaultPath = "/usr/share/elasticsearch/plugins/jdonrefv4-0.2/word84.txt";
    
    static String filePath = defaultPath;

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        FrequentTermsUtil.filePath = filePath;
    }
    
     public static String readFile ( String theFileName ){
        InputStreamReader flog	= null;
	LineNumberReader llog	= null;
	String myLine		     = null;
        String myConcatLines     = "";
	try{ 
		flog = new InputStreamReader(new FileInputStream(theFileName) );
		llog = new LineNumberReader(flog);
		while ((myLine = llog.readLine()) != null) { 
                      // --- Ajout de la ligne au contenu 2069
                      myConcatLines += myLine;
                }
        }catch (Exception e){
               // --- Gestion erreur lecture du fichier (fichier non existant, illisible, etc.)
               System.err.println("Error : "+e.getMessage());
               return null;
        }
        return myConcatLines;
    }
    
    public static ConcurrentHashMap<String,Integer> hashMot(String data){
        
        ConcurrentHashMap<String,Integer> str = new ConcurrentHashMap<>();
        reverseMostFrequentHashTerms = new ConcurrentHashMap<>();

        String term = "\"term\":\"";
        String count = "\",\"count\"";
        int sTerm = term.length();
        int sCount = count.length();
        int dep = 0;
        int iTerm;
        int iCount;
        String contenu = data.replaceAll("\\s", "");
        int lastITerm = contenu.lastIndexOf(term);
        int index=0;
        
        while(dep<lastITerm){
            iTerm = contenu.indexOf(term, dep);
            iCount = contenu.indexOf(count, dep);
            //TODO si contenu.substring(iTerm+sTerm,iCount) est une chaine vide ne pas ajouter !!!!
            reverseMostFrequentHashTerms.put(index,contenu.substring(iTerm+sTerm,iCount));
            str.put(contenu.substring(iTerm+sTerm,iCount),index++);
            dep = iCount+sCount;
        }
        return str;
        
    }
    
    static String data = null;
    
    public static String getData()
    {
        if (data==null)
        {
            data = readFile(filePath);
        }
        return data;
    }
            
    static ConcurrentHashMap<String,Integer> mostFrequentHashTerms;
    static ConcurrentHashMap<Integer,String> reverseMostFrequentHashTerms;
    
    public static ConcurrentHashMap<String,Integer> getMostFrequentHashTerms()
    {
        if (mostFrequentHashTerms==null)
        {
            mostFrequentHashTerms = hashMot(getData());
        }
        return mostFrequentHashTerms;
    }
    
    public static ConcurrentHashMap<Integer,String> getReverseMostFrequentHashTerms()
    {
        if (reverseMostFrequentHashTerms==null)
        {
            mostFrequentHashTerms = hashMot(getData());
        }
        return reverseMostFrequentHashTerms;
    }
    
//    static final String[] mostFrequentTerms = new String[]{
//        /*"france",*/ "rue", "avenue", "av", "saint", "st","chemin", "route", "rte", "bis", "mont", "impasse", "imp", "jean", "boulevard", "bd", "grand", "chateau", "pl", "place", "arondi", "pont", "tour", "paul", "cour", "moulin", "fer", "champ", "ter", "general", "gal","george", "pari", "loui", "marti", "mare", "mars", "eglis", "bourg", "fontain", "petit", "pt","martin", "gaul", "marseil", "henri", "75056", "toulo", "marechal", "13055", "francoi", "vileneuv", "leclerc", "michel", "toulous",
//        "principal", "cote", "mauric", "joseph", "albert", "rose", "nation", "nal","national", "germain", "havr", "33063", "foret", "76351", "laure", "verdun", "robe", "lauren", "mine", "provenc", "claud", "colomb", "sabl", "lyon", "chatel", "laurent", "quai", "44109", "epi", "aube", "august", "guy", "parc", "roue", "jardin", "gambeta", "59350", "roy", "roi"
//    };
//    
//    static final String[] mostFrequentTermsTransformed = new String[]{
//        /*"france",*/ "rue", "av", "av", "st", "st","chemin", "rte", "rte", "bis", "mont", "imp", "imp","jean", "bd", "bd", "grand", "chateau", "pl", "pl", "arondi", "pont", "tour", "paul", "cour", "moulin", "fer", "champ", "ter", "gal", "gal","george", "pari", "loui", "marti", "mare", "mars", "eglis", "bourg", "fontain", "petit", "pt","martin", "gaul", "marseil", "henri", "75056", "toulo", "marechal", "13055", "francoi", "vileneuv", "leclerc", "michel", "toulous",
//        "principal", "cote", "mauric", "joseph", "albert", "rose", "nation", "nal","nal", "germain", "havr", "33063", "foret", "76351", "laure", "verdun", "robe", "lauren", "mine", "provenc", "claud", "colomb", "sabl", "lyon", "chatel", "laurent", "quai", "44109", "epi", "aube", "august", "guy", "parc", "roue", "jardin", "gambeta", "59350", "roy", "roi"
//    };
    
    public static boolean isInt(String chaine)
    {
        try
        {
            Integer.parseInt(chaine);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
    
    public static ArrayList<LongArrayList> genereMostFrequentTerms(String[] words,int count,int index,int currentFaults,int maxFaults, int currentTerms, int maxTerms)
    {
        if (index==words.length && count>0) return null;
        
        ArrayList<LongArrayList> res = new ArrayList<>();
        if (count==0)
        {
            res.add(new LongArrayList());
            return res;
        }
        
        String word = words[index].toLowerCase();
        boolean exact = word.length()<3 || isInt(word) || currentTerms>=maxTerms;
        int start = exact?word.length():Math.max(3,word.length()-Math.max(maxFaults-currentFaults,0));
        int end   = word.length();
        
        for(int j=start;j<=end;j++)
        {
            ArrayList<LongArrayList> res1 = genereMostFrequentTerms(words, count-1,index+1,currentFaults+end-j,maxFaults,j==end?currentTerms:(currentTerms+1),maxTerms);
            if (res1!=null)
            {
                String term = word.substring(0,j);
                if (getMostFrequentHashTerms().get(term)!=null)
                {
                    for(int i=0;i<res1.size();i++)
                    {
                        LongArrayList res1i = res1.get(i).clone();
                        res1i.add(getMostFrequentHashTerms().get(term));
                        res.add(res1i);
                    }
                }
            }
        }
        
        if (index+count<words.length)
        {
            ArrayList<LongArrayList> res2 = genereMostFrequentTerms(words, count,index+1,currentFaults,maxFaults,currentTerms,maxTerms);
            if (res2!=null)
                res.addAll(res2);
        }
        
        return res;
    }
    
    public static ArrayList<LongArrayList> genereMostFrequentTerms(String[] words,int minWords,int maxFaults, int maxTerms)
    {
        ArrayList<LongArrayList> res = new ArrayList<>();
        
        for(int i=minWords;i<=words.length;i++)
        {
            res.addAll(genereMostFrequentTerms(words,i,0,0,maxFaults, 0, maxTerms));
        }
        
        return res;
    }
    
    public static ArrayList<String> genereMostFrequentTerms(String chaine,int minWords,int maxFaults,int maxTerms)
    {
        String[] words = chaine.split(" ");
        
        ArrayList<LongArrayList> mostFrequentTerms = genereMostFrequentTerms(words, minWords, maxFaults, maxTerms);
        ArrayList<String> res = new ArrayList<>();
        
        for(LongArrayList mostFrequentTerm : mostFrequentTerms)
        {
            StringBuilder str = new StringBuilder();
            CollectionUtils.sort(mostFrequentTerm);
            for(LongCursor l : mostFrequentTerm)
            {
                int value = (int) l.value;
                String subchaine = getReverseMostFrequentHashTerms().get(value);
                str.append(subchaine);
            }
            res.add(str.toString());
        }
        
        return res;
    }
    
    public static String generateMostFrequentTerms(LongArrayList list)
    {
        String res = "";
        for(int i=0;i<list.size();i++)
        {
            res += getReverseMostFrequentHashTerms().get((int)list.get(i)-1);
        }
        return res;
    }
    
    public static LongArrayList getMostFrequentTerms(Term[] queryTerms)
    {
        LongArrayList terms = new LongArrayList();
        for(int i=0;i<queryTerms.length;i++)
        {
            String term = queryTerms[i].text();
            
            if (getMostFrequentHashTerms().get(term)!=null)
            {
                terms.add(getMostFrequentHashTerms().get(term)+1);
            }
        }
        CollectionUtils.sort(terms);
        return terms;
    }
    
    public static String cumul(ArrayList<String> al)
    {
        StringBuilder cumul = new StringBuilder();
        for(int i=0;i<al.size();i++)
        {
            if (i>0) cumul.append(" ");
            cumul.append(al.get(i));
        }
        return cumul.toString();
    }
    
    public static void main(String[] args)
    {
        setFilePath("src/resources/analysis/word84.txt");
        //ArrayList<String> al = genereMostFrequentTerms("40 BOULEVARD FRANCOIS ALBERT"+ " "+"86"+ " " +"86194"+ " "+ "POITIERS",2,1);
        //ArrayList<String> al = genereMostFrequentTerms("57 BOULEVARD DU MARECHAL LECLERC 01 01053 BOURG EN BRESSE",2,5,1);
        //ArrayList<String> al = genereMostFrequentTerms("6 AVENUE DU GENERAL DE GAULLE 95 95190 FONTENAY EN PARISIS",2,5,1);
        //ArrayList<String> al = genereMostFrequentTerms("54 BOULEVARD DE L HOPITAL 13 75013 PARIS 13 E ARRONDISSEMENT 13",5,5,1);
        ArrayList<String> al = genereMostFrequentTerms("10 RUE DU GENERAL DE GAULLE 62 62223 SAINT LAURENT BLANGY",5,5,1);
        for(int i=0;i<al.size();i++)
                System.out.println(" Ajoute "+al.get(i));
        System.out.println(al.size());
        
        String cumul = cumul(al);
        
        System.out.println(cumul);
    }
}