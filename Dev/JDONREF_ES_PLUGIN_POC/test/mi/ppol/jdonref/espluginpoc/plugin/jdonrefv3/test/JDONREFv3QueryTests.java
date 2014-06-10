package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryBuilder;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryParser;
import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

/**
 *
 * @author Julien
 */
@ClusterScope(scope=Scope.SUITE, numNodes=1)
public class JDONREFv3QueryTests extends ElasticsearchIntegrationTest
{
    final static String INDEX_NAME = "jdonref";
    final static String DOC_TYPE_NAME = "test";
    
    public void publicIndex(String type,String id, XContentBuilder data)
    {
        super.index(INDEX_NAME, type, id, data);
    }
    
    public Settings indexSettings() {
        return settingsBuilder()
                .put("index.number_of_replicas", 0)
                .put("index.number_of_shards", 1)
                .put("index.image.use_thread_pool", this.randomBoolean())
            .build();
    }
    
    @Before
    public void createEmptyIndex() throws Exception {
            logger.info("creating index [{}]", INDEX_NAME);
            wipeIndices(INDEX_NAME);
            
            String settings = readFile("./test/resources/index/jdonrefv3es-settings_Types.json");
            client().admin().indices().prepareCreate(INDEX_NAME).setSettings(settings).execute().actionGet();
    }
    /*
    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
           .put("plugin.types", JDONREFv3ESPlugin.class.getName())
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
        String[] mappings = new String[]{"pays","departement","commune","voie","troncon","adresse"};
        
        for(int i=0;i<mappings.length;i++)
        {
            String test = mappings[i];
            String mapping = readFile("./src/resources/mapping/mapping-"+test+".json");
            PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(INDEX_NAME).type(test).source(mapping)).actionGet();
        }
        this.refresh();
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
        System.out.println("---------------------");
        System.out.println("Test Number "+testNumber++);
        System.out.println("Searching "+voie);
        QueryBuilder qb = (QueryBuilder) new JDONREFv3QueryBuilder(voie);
        ((JDONREFv3QueryBuilder)qb).mode(JDONREFv3QueryParser.STRING);
        //QueryBuilder qb = new QueryStringQueryBuilder(voie);
        SearchResponse search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        
        if (hits.length==0)
            System.out.println("No results");
        assertTrue(hits.length>0);
        
        boolean match = false;
        System.out.println(hits.length+" hit(s)");
        for(int i=0;(i<hits.length)&&!match;i++) // on n'affiche l'explication qu'en cas d'erreur et pour le premier et le résultat attendu.
        {
            SearchHit hit = hits[i];
            match = assertion.equals(hit.getSource().get("fullName"));
            if (!match || match && i>0)
            {
                System.out.println("hit "+i+" "+hit.getSourceAsString());
                System.out.println("score : "+hit.getScore());
                Explanation ex = hits[i].explanation();
                if (ex!=null && (match || i==0))
                {
                    printExplanation(ex);
                }
            }
        }
        assertEquals(assertion,hits[0].getSource().get("fullName"));
    }
    
    void index() throws IOException, InterruptedException, ExecutionException
    {
        publicIndex("pays","FR",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","FRANCE")
                .field("numero","0")
                .field("type","pays")
                .endObject());
        publicIndex("pays","DE",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","DE")
                .field("ligne7","ALLEMAGNE")
                .field("fullName","ALLEMAGNE")
                .field("numero","0")
                .field("type","pays")
                .endObject());
        publicIndex("commune","75056",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","75000")
                .field("code_insee","75056")
                .field("commune","PARIS")
                .field("ligne6","PARIS")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","PARIS FRANCE")
                .field("numero","0")
                .field("code_departement","75")
                .field("type","commune")
                .endObject());
        publicIndex("commune","59500",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("commune","DOUAI")
                .field("ligne6","DOUAI")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","59500 DOUAI FRANCE")
                .field("numero","0")
                .field("code_departement","59")
                .field("type","commune")
                .endObject());
        publicIndex("voie","1",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("numero","0")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","voie")
                .endObject());
        publicIndex("voie","2",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero","0")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","voie")
                .endObject());
        publicIndex("voie","3",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","RUE DE LA FRANCE")
                .field("ligne6","75013 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","RUE DE LA FRANCE 75013 PARIS FRANCE")
                .field("numero","0")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75113")
                .field("type","voie")
                .endObject());
        publicIndex("adresse","1",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","24 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","24 BOULEVARD DE L HOPITAL 75013 PARIS FRANCE")
                .field("numero","24")
                .field("code_departement","75")
                .field("code_postal","75013")
                .field("code_insee","75013")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","2",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","24 RUE DE LA FRANCE")
                .field("ligne6","75013 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","24 RUE DE LA FRANCE 75013 PARIS FRANCE")
                .field("numero","24")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75113")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","3",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","75 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","75 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("numero","75")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","4",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","130 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","130 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero","130")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","5",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","131 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","131 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero","131")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","6",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","59 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","59 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero","59")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","7",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","75 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","75 RUE REMY DUHEM 59500 DOUAI FRANCE")
                .field("numero","75")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","8",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","130 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","130 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("numero","130")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","9",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","59 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","59 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("numero","59")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .field("type","adresse")
                .endObject());
        publicIndex("adresse","10",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","59 BOULEVARD DE LA FRANCE")
                .field("ligne6","02000 HOPITAL")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("fullName","59 BOULEVARD DE LA FRANCE 02000 HOPITAL FRANCE")
                .field("numero","59")
                .field("code_departement","02")
                .field("code_postal","02000")
                .field("code_insee","02000")
                .field("type","adresse")
                .endObject());
        publicIndex("departement","75",XContentFactory.jsonBuilder().startObject()
                .field("ligne6","75")
                .field("ligne7","FRANCE")
                .field("fullName","75 FRANCE")
                .field("numero","0")
                .field("code_departement","75")
                .field("type","departement")
                .endObject());
        publicIndex("departement","59",XContentFactory.jsonBuilder().startObject()
                .field("ligne6","59")
                .field("ligne7","FRANCE")
                .field("fullName","59 FRANCE")
                .field("numero","0")
                .field("code_departement","59")
                .field("type","departement")
                .endObject());
        
        for(int i=0;i<10;i++)
        {
            String randomCode = randomRealisticUnicodeOfLength(2);
            String randomLigne7 = randomRealisticUnicodeOfLength(10);
            publicIndex("pays",randomCode,XContentFactory.jsonBuilder().startObject()
                  .field("code_pays",randomCode)
                  .field("ligne7",randomLigne7)
                  .endObject());
        }
    }
    
    @Test
    public void testSearch() throws IOException, InterruptedException, ExecutionException
    {
        importMapping();
        index();
            
        //Thread.sleep(5000);
        ensureYellow();
        Thread.sleep(30000); // wait for indexation !
        
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
        
        
        searchExactAdresse("130 RUE REMY DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
       searchExactAdresse("RUE REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("130 RUE REMY DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 RUE DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("130 REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
       searchExactAdresse("RUE REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        
        
        searchExactAdresse("59500 DOUAI FRANCE","59500 DOUAI FRANCE");
        searchExactAdresse("59500 FRANCE","59500 DOUAI FRANCE");
        searchExactAdresse("59500 DOUAI","59500 DOUAI FRANCE");
        searchExactAdresse("59500","59500 DOUAI FRANCE");
        searchExactAdresse("DOUAI FRANCE","59500 DOUAI FRANCE");
        searchExactAdresse("DOUAI","59500 DOUAI FRANCE");
        
        searchExactAdresse("59505 DOUAI FRANCE","59500 DOUAI FRANCE");
        searchExactAdresse("59505 FRANCE","59500 DOUAI FRANCE"); // 59505 n'est pas encore pris en charge
        searchExactAdresse("59505 DOUAI","59500 DOUAI FRANCE");
        searchExactAdresse("59505","59500 DOUAI FRANCE"); // 59505 n'est pas encore pris en charge

        searchExactAdresse("59 DOUAI FRANCE","59500 DOUAI FRANCE"); // anomalie à corriger : le code département est pris pour un numéro de voie
        searchExactAdresse("59 FRANCE","59500 DOUAI FRANCE"); // idem
        searchExactAdresse("59 DOUAI","59500 DOUAI FRANCE"); // idem

        searchExactAdresse("59 FRANCE","59 FRANCE"); // idem
        searchExactAdresse("59","59 FRANCE"); // idem
        
        searchExactAdresse("FRANCE","FRANCE");
        
        searchExactAdresse("RUE REMY DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE REMY 59","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("RUE DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE");
        searchExactAdresse("REMY 59","RUE REMY DUHEM 59500 DOUAI FRANCE");
        
        searchExactAdresse("59 BOULEVARD HOPITAL 75 PARIS","59 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE");
    }
}