package mi.ppol.jdonref.lucenepoc.plugin.jdonrefv3.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.test.ElasticsearchIntegrationTest;

import org.junit.Test;


/**
 *
 * @author Julien
 */
public class JDONREFv3MappingTests extends JDONREFv3IntegrationTests
{
    public void test_index_type(String test) throws FileNotFoundException, IOException, InterruptedException, ExecutionException
    {
        typeExists(test);
        
        String mapping = readFile("./src/resources/mapping/mapping-"+test+".json");
        System.out.println(mapping);
        PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(INDEX_NAME).type(test).source(mapping)).actionGet();
        publicRefresh();
        typeExists(test);
        
        IndexResponse idxResponse = client().prepareIndex(INDEX_NAME,test,test).setSource("codepays",1).execute().actionGet();
        typeExists(test);
        
        GetMappingsResponse mappingResponse = client().admin().indices().prepareGetMappings(INDEX_NAME).execute().get();
        showResponse(mappingResponse);
    }
    
    public void typeExists(String type)
    {
        TypesExistsResponse response = client().admin().indices().prepareTypesExists().setTypes(type).execute().actionGet();
        System.out.println(type+" existe ?"+response.isExists());
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
    
    @Test
    public void test_index_type_adresse() throws Exception
    {
        String test = "adresse";
        test_index_type(test);
    }
    
    @Test
    public void test_index_type_voie() throws Exception
    {
        String test = "voie";
        test_index_type(test);
    }
    
    @Test
    public void test_index_type_troncon() throws Exception
    {
        String test = "troncon";
        test_index_type(test);
    }
    
    @Test
    public void test_index_type_commune() throws Exception
    {
        String test = "commune";
        test_index_type(test);
    }
    
    @Test
    public void test_index_type_departement() throws Exception
    {
        String test = "departement";
        test_index_type(test);
    }
    
    @Test
    public void test_index_type_pays() throws Exception
    {
        String test = "pays";
        test_index_type(test);
    }
}
