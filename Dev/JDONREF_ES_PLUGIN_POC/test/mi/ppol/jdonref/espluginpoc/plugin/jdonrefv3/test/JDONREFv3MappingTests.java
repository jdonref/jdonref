package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.test.ElasticsearchIntegrationTest;

import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

/**
 *
 * @author Julien
 */
@ClusterScope(scope=Scope.SUITE, numNodes=1)
public class JDONREFv3MappingTests extends ElasticsearchIntegrationTest
{
    final static String INDEX_NAME = "test";
    final static String DOC_TYPE_NAME = "test";
    
    @Override
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
                
        String settings = readFile("./test/resources/index/jdonrefv3es-settings_Mapping.json");
        client().admin().indices().prepareCreate(INDEX_NAME).setSettings(settings).execute().actionGet();
        //createIndex(INDEX_NAME);
    }
    
    public void test_index_type(String test) throws FileNotFoundException, IOException, InterruptedException, ExecutionException
    {
        typeExists(test,false);
        
        String mapping = readFile("./src/resources/mapping/mapping-"+test+".json");
        //System.out.println(mapping);
        PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(INDEX_NAME).type(test).source(mapping)).actionGet();
        super.refresh();
        typeExists(test,true);
        GetMappingsResponse mappingResponse = client().admin().indices().prepareGetMappings(INDEX_NAME).execute().get();
        //showResponse(mappingResponse);
    }
    
    public void typeExists(String type,boolean not)
    {
        TypesExistsResponse response = client().admin().indices().prepareTypesExists().setTypes(type).execute().actionGet();
        assert(response.isExists()==not);
    }
    
    public void showResponse(GetMappingsResponse mappingResponse) throws IOException
    {
        for (ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>> indexEntry : mappingResponse.mappings()) {
            System.out.println("index : "+indexEntry.key);
            for (ObjectObjectCursor<String, MappingMetaData> typeEntry : indexEntry.value) {
                System.out.println("type : "+typeEntry.key);
                showMap(typeEntry.value.getSourceAsMap());
            }
        }
    }
    
    public void showMap(Map<String,Object> map)
    {
        Set<String> keys = map.keySet();
        String[] stKeys = keys.toArray(new String[]{});
        for (int i = 0; i < stKeys.length; i++)
        {
            Object o = map.get(stKeys[i]);
            if (o instanceof Map)
            {
                System.out.println(stKeys[i] + ": {");
                showMap((Map<String, Object>)o);
                System.out.println("}");
            }
            else
            {
                System.out.println(stKeys[i] + ":" + o);
            }
        }
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
    
    @Test
    public void test_index_type_adresse() throws Exception
    {
        // Crée le mapping
        test_index_type("adresse");
        
        // Tente une indexation
        index(INDEX_NAME, "adresse", "1", XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("adresse")
                        .field("code_pays","FR")
                        .field("ligne4","24 BOULEVARD DE L HOPITAL")
                        .field("ligne6","75005 PARIS")
                        .field("ligne7","FRANCE")
                        .field("fullName","24 BOULEVARD DE L HOPITAL 75013 PARIS FRANCE")
                        .field("numero","24")
                        .field("type_de_voie","BOULEVARD")
                        .field("article","DE L")
                        .field("libelle","HOPITAL")
                        .field("commune","PARIS")
                        .field("pays","FRANCE")
                        .field("code_arrondissement","13")
                        .field("code_departement","75")
                        .field("code_postal","75013")
                        .field("code_insee","75113")
                    .endObject()
                .endObject());
        
        GetResponse get = client().prepareGet().setIndex(INDEX_NAME).setType("adresse").setFetchSource(true).setId("1").execute().actionGet();
        assertTrue(get.isExists());
        System.out.println(get.getSourceAsString());
        
        Thread.sleep(10000);
        
        QueryBuilder qb = (QueryBuilder)QueryBuilders.matchQuery("ligne4","BOULEVARD");
        SearchResponse search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        assert(hits.length==0);
        
        //qb = (QueryBuilder)QueryBuilders.matchQuery("fullName","BOULEVARD");
        qb = (QueryBuilder)QueryBuilders.queryString("BOULEVARD");
        search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        hits = search.getHits().getHits();
        assert(hits.length>0);
    }
    /*
    @Test
    public void test_index_type_voie() throws Exception
    {
        // Crée le mapping
        test_index_type("voie");
        
        // Tente une indexation
        index(INDEX_NAME, "voie", "1", XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("adresse")
                        .field("codepays","FR")
                        .field("ligne4","BOULEVARD DE L HOPITAL")
                        .field("ligne6","75005 PARIS")
                        .field("ligne7","FRANCE")
                        .field("fullName","BOULEVARD DE L HOPITAL 75013 PARIS FRANCE")
                        .field("codedepartement","75")
                        .field("codepostal","75013")
                        .field("codeinsee","75113")
                    .endObject()
                .endObject());
        
        GetResponse get = client().prepareGet().setIndex(INDEX_NAME).setType("voie").setFetchSource(true).setId("1").execute().actionGet();
        assertTrue(get.isExists());
        System.out.println(get.getSourceAsString());
        
        Thread.sleep(10000);
        
        QueryBuilder qb = (QueryBuilder)QueryBuilders.matchQuery("ligne4","BOULEVARD");
        SearchResponse search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        assert(hits.length==0);
        
        qb = (QueryBuilder)QueryBuilders.matchQuery("fullName","BOULEVARD");
        search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        hits = search.getHits().getHits();
        assert(hits.length>0);
    }
    
    @Test
    public void test_index_type_commune() throws Exception
    {
        // Crée le mapping
        test_index_type("commune");
        
        // Tente une indexation
        index(INDEX_NAME, "commune", "1", XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("adresse")
                        .field("codepays","FR")
                        .field("ligne6","75005 PARIS")
                        .field("ligne7","FRANCE")
                        .field("fullName","75013 PARIS FRANCE")
                        .field("codedepartement","75")
                        .field("codepostal","75013")
                        .field("codeinsee","75113")
                    .endObject()
                .endObject());
        
        GetResponse get = client().prepareGet().setIndex(INDEX_NAME).setType("commune").setFetchSource(true).setId("1").execute().actionGet();
        assertTrue(get.isExists());
        System.out.println(get.getSourceAsString());
        
        Thread.sleep(10000);
        
        QueryBuilder qb = (QueryBuilder)QueryBuilders.matchQuery("ligne6","PARIS");
        SearchResponse search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        assert(hits.length==0);
        
        qb = (QueryBuilder)QueryBuilders.matchQuery("fullName","PARIS");
        search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        hits = search.getHits().getHits();
        assert(hits.length>0);
    }
    
    @Test
    public void test_index_type_departement() throws Exception
    {
        // Crée le mapping
        test_index_type("departement");
        
        // Tente une indexation
        index(INDEX_NAME, "departement", "1", XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("adresse")
                        .field("codepays","FR")
                        .field("ligne6","75")
                        .field("ligne7","FRANCE")
                        .field("fullName","75 FRANCE")
                        .field("codedepartement","75")
                    .endObject()
                .endObject());
        
        GetResponse get = client().prepareGet().setIndex(INDEX_NAME).setType("departement").setFetchSource(true).setId("1").execute().actionGet();
        assertTrue(get.isExists());
        System.out.println(get.getSourceAsString());
        
        Thread.sleep(10000);
        
        QueryBuilder qb = (QueryBuilder)QueryBuilders.matchQuery("ligne6","75");
        SearchResponse search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        assert(hits.length==0);
        
        qb = (QueryBuilder)QueryBuilders.matchQuery("fullName","75");
        search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        hits = search.getHits().getHits();
        assert(hits.length>0);
    }
    
    @Test
    public void test_index_type_pays() throws Exception
    {
        // Crée le mapping
        test_index_type("pays");
        
        // Tente une indexation
        index(INDEX_NAME, "pays", "1", XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("adresse")
                        .field("codepays","FR")
                        .field("ligne7","FRANCE")
                        .field("fullName","FRANCE")
                    .endObject()
                .endObject());
        
        GetResponse get = client().prepareGet().setIndex(INDEX_NAME).setType("pays").setFetchSource(true).setId("1").execute().actionGet();
        assertTrue(get.isExists());
        System.out.println(get.getSourceAsString());
        
        Thread.sleep(10000);
        
        QueryBuilder qb = (QueryBuilder)QueryBuilders.matchQuery("ligne7","FRANCE");
        SearchResponse search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        assert(hits.length==0);
        
        qb = (QueryBuilder)QueryBuilders.matchQuery("fullName","FRANCE");
        search = client().prepareSearch().setQuery(qb).setExplain(true).execute().actionGet();
        hits = search.getHits().getHits();
        assert(hits.length>0);
    }
     * */
}
