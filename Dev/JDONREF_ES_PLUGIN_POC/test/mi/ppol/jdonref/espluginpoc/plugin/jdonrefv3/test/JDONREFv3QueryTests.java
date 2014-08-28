package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryBuilder;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.JDONREFv3Query;
import org.apache.lucene.search.JDONREFv3Scorer;
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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

/**
 *
 * @author Julien
 */
@ClusterScope(scope=Scope.SUITE, numNodes=1)
public class JDONREFv3QueryTests// extends ElasticsearchIntegrationTest
{
    final static String INDEX_NAME = "jdonrefTest";
    final static String DOC_TYPE_NAME = "test";
    
    public void publicIndex(BulkRequestBuilder brb,String type,String id, XContentBuilder data)
    {
        //System.out.println("Start indexing "+type+" "+id);
        //super.index(INDEX_NAME, type, id, data);
        
        IndexRequestBuilder irb = client().prepareIndex(INDEX_NAME,type,id);
        irb.setSource(data);
        irb.setRefresh(false);
        
        brb.add(irb);
        
        //System.out.println("End indexing");
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
            
            String settings = readFile("./test/resources/index/jdonrefv3es-settings_Query.json");
            client().admin().indices().prepareCreate(INDEX_NAME).setSettings(settings).execute().actionGet();
            client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
            
            importMapping();
            stopRefresh();
            index();
            // startRefresh();
    }
    /*
    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
           .put("plugin.types", AnalysisPhoneticPlugin.class.getName())
           .put(super.nodeSettings(nodeOrdinal)).build();
    }*/
    
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
    
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"pays","departement","commune","voie","troncon","adresse","poizon"};
        
        for(int i=0;i<mappings.length;i++)
        {
            String test = mappings[i];
            String mapping = readFile("./src/resources/mapping/mapping-"+test+".json");
            PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(INDEX_NAME).type(test).source(mapping)).actionGet();
        }
    }
    
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
    
    void searchExactAdresse(String voie,String assertion)
    {
        searchExactAdresse( voie, assertion,0,-1.0f);
    }
    
    void searchExactAdresse(String voie,String assertion,int indice,float note_minimum)
    {
        System.out.println("---------------------");
        System.out.println("Test Number "+testNumber++);
        System.out.println("Searching "+voie);
        QueryBuilder qb = (QueryBuilder) new JDONREFv3QueryBuilder(voie);
        ((JDONREFv3QueryBuilder)qb).mode(JDONREFv3QueryParser.STRING);
        //QueryBuilder qb = new QueryStringQueryBuilder(voie);
        long start = Calendar.getInstance().getTimeInMillis();
        SearchResponse search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Took "+(end-start)+" ms");
        SearchHit[] hits = search.getHits().getHits();
        
        if (hits.length==0)
            System.out.println("No results");
        Assert.assertTrue(hits.length>0);
        
        boolean match = false;
        boolean hitPrinted = false;
        boolean explanationPrinted = false;
        int positionMatch = -1;
            
        System.out.println(hits.length+" hit(s). Best is "+hits[0].getScore());
        for(int i=0;(i<hits.length)&&!match;i++) // on n'affiche l'explication qu'en cas d'erreur et pour le premier et le résultat attendu.
        {
            hitPrinted = false;
            explanationPrinted = false;
            SearchHit hit = hits[i];
            Explanation ex = hits[i].explanation();
            match = assertion.equals(hit.getSource().get("fullName"));
            if (ex!=null && Math.abs(hits[i].getScore()-ex.getValue())>0.05)
            {
                printExplanation(ex);
                Assert.assertTrue(Math.abs(hits[i].getScore()-ex.getValue())<=0.05); // 0.05 tolerance : no more time to dev.
            }
            if (match) positionMatch = i;
        }
        if (positionMatch>indice || positionMatch==-1)
        {
            System.out.println("hit "+0+" "+hits[0].getSourceAsString());
            System.out.println("score : "+hits[0].getScore());
            printExplanation(hits[0].explanation());
        }
        Assert.assertTrue(positionMatch<=indice);
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
    
    void index() throws IOException, InterruptedException, ExecutionException
    {
        BulkRequestBuilder brb = client().prepareBulk();
        
        publicIndex(brb,"pays","FR",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","FRANCE")
                .field("type","pays")
                .endObject());
        publicIndex(brb,"pays","DE",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","DE")
                .field("ligne7","ALLEMAGNE")
                .field("fullName","ALLEMAGNE")
                .field("type","pays")
                .endObject());
        publicIndex(brb,"commune","75056",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","75000")
                .field("code_insee","75056")
                .field("commune","PARIS")
                .field("ligne6","PARIS")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","PARIS FRANCE")
                .field("code_departement","75")
                .field("type","commune")
                .endObject());
        publicIndex(brb,"commune","59500",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("commune","DOUAI")
                .field("ligne6","DOUAI")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","59500 DOUAI FRANCE")
                .field("code_departement","59")
                .field("type","commune")
                .endObject());
        publicIndex(brb,"voie","1",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","voie")
                .endObject());
        publicIndex(brb,"voie","2",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName","RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","voie")
                .endObject());
        publicIndex(brb,"voie","3",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","RUE DE LA FRANCE")
                .field("ligne6","75013 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","RUE DE LA FRANCE 75013 PARIS FRANCE")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75113")
                .field("type","voie")
                .endObject());
        publicIndex(brb,"adresse","1",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","24 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","24 BOULEVARD DE L HOPITAL 75013 PARIS FRANCE")
                .field("numero",24)
                .field("code_departement","75")
                .field("code_postal","75013")
                .field("code_insee","75013")
                .field("type","adresse")
                .endObject());
        publicIndex(brb,"adresse","2",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","24 RUE DE LA FRANCE")
                .field("ligne6","75013 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","24 RUE DE LA FRANCE 75013 PARIS FRANCE")
                .field("numero",24)
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75113")
                .field("type","adresse")
                .endObject());
        publicIndex(brb,"adresse","3",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","75 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","75 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("numero",75)
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","adresse")
                .endObject());
        
        for(int i=130;i<135;i++) // inclus donc le numéro 130
        {
            publicIndex(brb,"adresse","4"+i,XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4",i+" RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName",i+" RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero",i)
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        }
        
        publicIndex(brb,"adresse","5",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","131 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName","131 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero",131)
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        
        publicIndex(brb,"adresse","6",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","59 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName","59 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero",59)
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        publicIndex(brb,"adresse","7",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","75 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName","75 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero",75)
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        publicIndex(brb,"adresse","8",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","130 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","130 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("numero",130)
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","adresse")
                .endObject());
        publicIndex(brb,"adresse","9",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","59 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","59 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("numero",59)
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","adresse")
                .endObject());
        publicIndex(brb,"adresse","10",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","59 BOULEVARD DE LA FRANCE")
                .field("ligne6","02000 HOPITAL")
                .field("ligne7","FRANCE")
                .field("commune","HOPITAL")
                .field("fullName","59 BOULEVARD DE LA FRANCE 02000 HOPITAL FRANCE")
                .field("numero",59)
                .field("code_departement","02")
                .field("code_postal","02000")
                .field("code_insee","02000")
                .field("type","adresse")
                .endObject());
        publicIndex(brb,"poizon","4",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB1")
                .field("poizon_service",1)
                .field("ligne1","KEBAB LA P'TITE FRITE")
                .field("ligne4","130 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName","KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero",130)
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","poizon")
                .endObject());
        publicIndex(brb,"poizon","5",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB1")
                .field("poizon_service",1)
                .field("ligne1","KEBAB DU COIN")
                .field("ligne4","131 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName","KEBAB DU COIN 131 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero",131)
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","poizon")
                .endObject());
        publicIndex(brb,"poizon","6",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB2")
                .field("poizon_service",1)
                .field("ligne1","KEBAB LA GROSSE FRITE")
                .field("ligne4","59 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("fullName","KEBAB LA GROSSE FRITE 59 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero",59)
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","poizon")
                .endObject());
        publicIndex(brb,"poizon","7",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB3")
                .field("poizon_service",1)
                .field("ligne1","KEBAB DU COIN")
                .field("ligne4","75 RUE REMY DUHEM")
                .field("ligne6","75015 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","KEBAB DU COIN 75 RUE REMY DUHEM 75015 PARIS FRANCE")
                .field("numero",75)
                .field("code_departement","75")
                .field("code_postal","75015")
                .field("code_insee","75115")
                .field("type","poizon")
                .endObject());
        publicIndex(brb,"departement","75",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne6","75")
                .field("ligne7","FRANCE")
                .field("fullName","75 FRANCE")
                .field("code_departement","75")
                .field("type","departement")
                .endObject());
        publicIndex(brb,"departement","59",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne6","59")
                .field("ligne7","FRANCE")
                .field("fullName","59 FRANCE")
                .field("code_departement","59")
                .field("type","departement")
                .endObject());
        
        for(int i=0;i<10;i++)
        {
            String randomCode = "RANDOM"+i;
            String randomLigne7 = "RANDOM RANDOM "+i+""+i;
            publicIndex(brb,"pays",randomCode,XContentFactory.jsonBuilder().startObject()
                  .field("code_pays",randomCode)
                  .field("ligne7",randomLigne7)
                  .endObject());
        }
        
        BulkResponse br = brb.execute().actionGet();
        if (br.hasFailures()) System.out.println(br.buildFailureMessage());
        Assert.assertFalse(br.hasFailures());
        
        refresh();
    }
    
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
    
    @Test
    public void testSearch() throws IOException, InterruptedException, ExecutionException
    {
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());

        // 183 tests au total
        
        JDONREFv3Query.DEBUGREFERENCE = "130 RUE REMY DUHEM 59500 DOUAI FRANCE";
        JDONREFv3Scorer.DEBUGREFERENCE = "130 RUE REMY DUHEM 59500 DOUAI FRANCE";
        
        searchExactAdresse("130 RUE REMY DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("130 RUE REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 RUE REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        refresh();
        searchExactAdresse("130 RUE REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("130 RUE REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,135.0f);
        searchExactAdresse("130 DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f); // .. 
        searchExactAdresse("130 DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        
        // 59505 non pris en charge. dirty results
        /*refresh();
        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,180.0f);
        searchExactAdresse("130 RUE REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 RUE REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 RUE DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        refresh();
        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,180.0f);
        searchExactAdresse("130 RUE REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 RUE REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 RUE DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);*/
        
        refresh();
        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("130 RUE REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 RUE REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 RUE DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
        searchExactAdresse("130 RUE REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("130 RUE REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 RUE REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 RUE DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("130 DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("130 REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("130 REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        
        JDONREFv3Query.DEBUGREFERENCE = "RUE REMY DUHEM 59500 DOUAI FRANCE";
        JDONREFv3Scorer.DEBUGREFERENCE = "RUE REMY DUHEM 59500 DOUAI FRANCE";
        refresh();
        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("RUE REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
        searchExactAdresse("RUE REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        refresh();
        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("RUE REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("RUE DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("RUE REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
        searchExactAdresse("RUE REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("RUE DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f); 
        refresh();
        searchExactAdresse("RUE REMY DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("RUE REMY 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("REMY DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("REMY DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("RUE REMY DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
        searchExactAdresse("RUE REMY 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("REMY DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
        searchExactAdresse("RUE REMY 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("RUE DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("REMY DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("REMY 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        
        /*refresh();
        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,180.0f);
        searchExactAdresse("RUE REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("RUE DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("RUE REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("RUE REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("RUE DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
        searchExactAdresse("REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
        searchExactAdresse("RUE REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("RUE DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
        searchExactAdresse("RUE REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
        searchExactAdresse("RUE REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
        searchExactAdresse("RUE DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
        searchExactAdresse("REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
        searchExactAdresse("DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
        searchExactAdresse("DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
        searchExactAdresse("REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,30.0f);
        searchExactAdresse("REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);*/
        
        JDONREFv3Query.DEBUGREFERENCE = "59500 DOUAI FRANCE";
        JDONREFv3Scorer.DEBUGREFERENCE = "59500 DOUAI FRANCE";
        refresh();
        searchExactAdresse("59500 DOUAI FRANCE","59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("59500 FRANCE","59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("59500 DOUAI","59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("59500","59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("DOUAI FRANCE","59500 DOUAI FRANCE",0,199.99f);
        searchExactAdresse("DOUAI","59500 DOUAI FRANCE",0,199.99f);
        refresh();
        searchExactAdresse("59505 DOUAI FRANCE","59500 DOUAI FRANCE",0,50.0f); // réduction du score : 59505 n'est pas pris en charge
        //searchExactAdresse("59505 FRANCE","59500 DOUAI FRANCE"); // 59505 n'est pas encore pris en charge
        searchExactAdresse("59505 DOUAI","59500 DOUAI FRANCE",0,20.0f); // réduction du score : 59505 n'est pas pris en charge
        //searchExactAdresse("59505","59500 DOUAI FRANCE"); // 59505 n'est pas encore pris en charge
        
        searchExactAdresse("59 59500 DOUAI","59500 DOUAI FRANCE",0,200.0f);
        
        refresh();
        searchExactAdresse("59 DOUAI FRANCE","59500 DOUAI FRANCE",0,180.0f);
        searchExactAdresse("59 DOUAI","59500 DOUAI FRANCE",0,170.0f); // mark
        //searchExactAdresse("59 FRANCE","59 FRANCE"); // DOUAI n'est pas le meilleur résultat dans ce cas !

        JDONREFv3Query.DEBUGREFERENCE = "59 FRANCE";
        JDONREFv3Scorer.DEBUGREFERENCE = "59 FRANCE";
        refresh();
        searchExactAdresse("59 FRANCE","59 FRANCE",0,199.99f);
        searchExactAdresse("59","59 FRANCE",0,199.99f);
        //searchExactAdresse("59 FR","59 FRANCE",0,199.99f); // not supported for now
        //searchExactAdresse("59 FR FRANCE","59 FRANCE",0,199.99f);
        
        JDONREFv3Query.DEBUGREFERENCE = "FRANCE";
        JDONREFv3Scorer.DEBUGREFERENCE = "FRANCE";
        refresh();
        searchExactAdresse("FRANCE","FRANCE",0,199.99f);
        searchExactAdresse("FR FRANCE","FRANCE",0,199.99f);
        
        JDONREFv3Query.DEBUGREFERENCE = "KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE";
        JDONREFv3Scorer.DEBUGREFERENCE = "KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE";
        refresh();
        searchExactAdresse("KEBAB FRITE DOUAI","KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE",1,100.0f);
        searchExactAdresse("KEBAB DOUAI","KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE",2,50.0f);
        searchExactAdresse("KEBAB RUE REMY DUHEM","KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE",2,50.0f);
    }
}