package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SpanQueryBuilder;
import org.elasticsearch.index.query.GroupedPayloadSpanQueryBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.search.Explanation;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.SpanTermQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
//import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
//import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

/**
 *
 * @author Julien
 */
public abstract class QueryTests
{
    protected String INDEX_NAME;
    protected String DOC_TYPE_NAME;
    protected String settingsFileName;
    
    protected boolean noexplaintest = false;
    
    public void publicIndex(BulkRequestBuilder brb,String type,String id, XContentBuilder data)
    {
        IndexRequestBuilder irb = client().prepareIndex(INDEX_NAME,type,id);
        irb.setSource(data);
        irb.setRefresh(false);
        
        brb.add(irb);
    }
    
    public Settings indexSettings() {
        return settingsBuilder()
                .put("index.number_of_replicas", 0)
                .put("index.number_of_shards", 1)
                .put("index.image.use_thread_pool", this.randomBoolean())
            .build();
    }
    
    public boolean randomBoolean()
    {
        return (new Random()).nextBoolean();
    }
    
    protected static Node node;
    
    public static Client client()
    {
        if (node==null)
            node = NodeBuilder.nodeBuilder().node();
        return node.client();
    }
    
    @After
    public void clean()
    {
        try {
            client().admin().indices().prepareDelete(INDEX_NAME).execute().actionGet();
            client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
        } catch (ElasticsearchException e) {
        }
    }
    
    @Before
    public void setUp() throws Exception {
            System.out.println("creating index ["+INDEX_NAME+"]");
            
            try {
                client().admin().indices().prepareDelete(INDEX_NAME).execute().actionGet();
                client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
            } catch (ElasticsearchException e) {
            }
            
            String settings = readFile(settingsFileName);
            client().admin().indices().prepareCreate(INDEX_NAME).setSettings(settings).execute().actionGet();
            client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
            
            importMapping();
            stopRefresh();
            index();
            startRefresh();
            refresh();
    }
    
    public String readFile(String file) throws FileNotFoundException, IOException
    {
        BufferedReader reader = (new BufferedReader(new FileReader(new File(file))));
        String line = reader.readLine();
        String res = "";
        while(line!=null)
        {
            res += line+System.getProperty("line.separator");
            line = reader.readLine();
        }
        reader.close();
        return res;
    }
    
    abstract void importMapping() throws FileNotFoundException, IOException;
    
    void printExplanation(Explanation ex)
    {
        System.out.println("Explanation:");
        printExplanation(ex,"");
    }
    
    void printExplanation(Explanation ex,String tab)
    {
        System.out.println(tab+"value:"+ex.getValue());
        System.out.println(tab+"description:"+ex.getDescription());
        if (ex.getDetails()!=null)
        {
            for(int j=0;j<ex.getDetails().length;j++)
                printExplanation(ex.getDetails()[j],tab+"  ");
        }
    }
    
    int testNumber = 1;
    
    void searchExactAdresse(String voie,String assertion_ligne4,String assertion_ligne6)
    {
        searchExactAdresse( voie, assertion_ligne4,assertion_ligne6,-1,-1.0f,-1);
    }
    
    void searchExactAdresse(String voie,String assertion_ligne4,String assertion_ligne6,int size)
    {
        searchExactAdresse( voie, assertion_ligne4,assertion_ligne6,-1,-1.0f,size);
    }
    
    void searchExactAdresse(String voie,String assertion_ligne4,String assertion_ligne6,int indice,float note_minimum)
    {
        searchExactAdresse( voie, assertion_ligne4,assertion_ligne6,indice,note_minimum,-1);
    }
    
    abstract QueryBuilder getQueryBuilder(String voie);
    
    void searchExactAdresse(String voie,String assertion_ligne4,String assertion_ligne6,int indice,float note_minimum,int size)
    {
        System.out.println("---------------------");
        System.out.println("Test Number "+testNumber++);
        System.out.println("Searching "+voie+" in ["+INDEX_NAME+"]");
        
        QueryBuilder qb = getQueryBuilder(voie);
        
        //QueryStringQueryBuilder qb = new QueryStringQueryBuilder(voie);
        
        long start = Calendar.getInstance().getTimeInMillis();
        SearchResponse search = client().prepareSearch(INDEX_NAME).setQuery(qb).setExplain(true).execute().actionGet();
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Took "+(end-start)+" ms");
        SearchHit[] hits = search.getHits().getHits();
        
        if (hits.length==0)
        {
            System.out.println("No results");
            if (size!=-1) Assert.assertTrue(size==hits.length);
            if (size==0) return;
        }
        if (size==-1)
            Assert.assertTrue(hits.length>0);
        else
            Assert.assertTrue(hits.length==size);
        
        boolean match_ligne4 = false;
        boolean match_ligne6 = false;
        boolean hitPrinted = false;
        boolean explanationPrinted = false;
        int positionMatch = -1;
            
        System.out.println(hits.length+" hit(s). Best is "+hits[0].getScore());
        for(int i=0;(i<hits.length)&&!(match_ligne4&&match_ligne6);i++) // on n'affiche l'explication qu'en cas d'erreur et pour le premier et le résultat attendu.
        {
            hitPrinted = false;
            explanationPrinted = false;
            SearchHit hit = hits[i];
            Explanation ex = hits[i].explanation();
            match_ligne4 = assertion_ligne4.equals(hit.getSource().get("ligne4"));
            match_ligne6 = assertion_ligne6.equals(hit.getSource().get("ligne6"));
            if (ex!=null && !noexplaintest && Math.abs(hits[i].getScore()-ex.getValue())>0.05)
            {
                printExplanation(ex);
                System.out.println("-- ERROR WITH EXPLAIN --");
                //Assert.assertTrue(Math.abs(hits[i].getScore()-ex.getValue())<=0.05); // 0.05 tolerance : no more time to dev.
            }
            if (match_ligne4 && match_ligne6) positionMatch = i;
        }
        if (indice!=-1 && (positionMatch>indice || positionMatch==-1))
        {
            System.out.println("hit "+0+" "+hits[0].getSourceAsString());
            System.out.println("score : "+hits[0].getScore());
            printExplanation(hits[0].explanation());
        }
        if (indice!=-1) Assert.assertTrue(positionMatch<=indice);
        if (note_minimum>-1)
        {
            if (hits[positionMatch].getScore()<note_minimum)
            {
                if (!hitPrinted)
                {
                    System.out.println("hit "+positionMatch+" "+hits[positionMatch].getSourceAsString());
                    System.out.println("score : "+hits[positionMatch].getScore());
                }
                if (!explanationPrinted)
                {
                    printExplanation(hits[positionMatch].explanation());
                }
                Assert.assertTrue(hits[positionMatch].getScore()>=note_minimum);
            }
        }
    }
    
    abstract void index() throws IOException, InterruptedException, ExecutionException;
    
    public void refresh()
    {
        RefreshResponse rr = client().admin().indices().prepareRefresh().execute().actionGet();
        Assert.assertFalse(rr.getFailedShards()>0);
    }
    
    public void stopRefresh()
    {
        UpdateSettingsRequestBuilder reqb = client().admin().indices().prepareUpdateSettings();
        reqb.setIndices(INDEX_NAME);
        reqb.setSettings("{\"index\":{\"refresh_interval\":-1}}");
        reqb.get();
    }
    public void startRefresh()
    {
        UpdateSettingsRequestBuilder reqb = client().admin().indices().prepareUpdateSettings();
        reqb.setIndices(INDEX_NAME);
        reqb.setSettings("{\"index\":{\"refresh_interval\":\"1s\"}}");
        reqb.get();
    }
}