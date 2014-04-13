package mi.ppol.jdonref.lucenepoc.plugin.jdonrefv3.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import mi.ppol.jdonref.lucenepoc.index.query.JDONREFv3QueryBuilder;
import mi.ppol.jdonref.lucenepoc.plugin.jdonrefv3.JDONREFv3ESPlugin;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
public class JDONREFv3AnalyzerTests extends ElasticsearchIntegrationTest
{
    final static String INDEX_NAME = "test";
    final static String DOC_TYPE_NAME = "test";
    
    public void publicRefresh()
    {
        super.refresh();
    }
    
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
        createIndex(INDEX_NAME);
        ensureGreen();
    }
    
    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
           .put("plugin.types", JDONREFv3ESPlugin.class.getName())
           .put(super.nodeSettings(nodeOrdinal)).build();
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
    
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"pays","departement","commune","voie","troncon","adresse"};
        
        for(int i=0;i<mappings.length;i++)
        {
            String test = mappings[i];
            String mapping = readFile("./src/resources/mapping/mapping-"+test+".json");
            PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(INDEX_NAME).type(test).source(mapping)).actionGet();
        }
        publicRefresh();
    }

    void searchPays(String pays)
    {
        System.out.println("Searching "+pays);
        QueryBuilder qb = (QueryBuilder) new JDONREFv3QueryBuilder(pays);
        //QueryBuilder qb = (QueryBuilder)QueryBuilders.termQuery("ligne7",pays);
        //QueryBuilder qb = (QueryBuilder)QueryBuilders.matchQuery("ligne7",pays);
        SearchResponse search = client().prepareSearch().setQuery(qb).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        
        for(int i=0;i<hits.length;i++)
        {
            SearchHit hit = hits[i];
            System.out.println("hit "+i+" "+hit.getSourceAsString());
        }
        if (hits.length==0)
            System.out.println("No results");
    }
    
    void test_pays() throws IOException, InterruptedException, ExecutionException
    {    
        importMapping();
        
        publicIndex("pays","FR",XContentFactory.jsonBuilder().startObject()
                .field("codepays","FR")
                .field("ligne7","FRANCE")
                .endObject());
        publicIndex("pays","DE",XContentFactory.jsonBuilder().startObject()
                .field("codepays","DE")
                .field("ligne7","ALLEMAGNE")
                .endObject());
        
        for(int i=0;i<10;i++)
        {
            String randomCode = randomRealisticUnicodeOfLength(2);
            String randomLigne7 = randomRealisticUnicodeOfLength(10);
            publicIndex("pays",randomCode,XContentFactory.jsonBuilder().startObject()
                  .field("codepays",randomCode)
                  .field("ligne7",randomLigne7)
                  .endObject());
        }
        
        ensureGreen();
        Thread.sleep(10000); // wait for indexation !
        
        GetResponse response = client().prepareGet(INDEX_NAME,"pays","FR").execute().get();
        System.out.println(response.getSourceAsString());
        // need assert
        
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
        
        searchPays("FRANCE");
        searchPays("ALLEMAGNE");
    }
    
    
    @Test
    public void test_analyzer_pays() throws Exception
    {
        test_pays();
    }
}