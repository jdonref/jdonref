package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.GroupedPayloadSpanQueryBuilder;
import org.elasticsearch.index.query.MultiPayloadSpanTermQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author Julien
 */
public class GroupedPayloadSpanQueryTests extends QueryTests
{
    public GroupedPayloadSpanQueryTests()
    {
        settingsFileName = "./test/resources/index/GroupedPayloadSpanQuery-settings.json";
        INDEX_NAME = "groupedpayloadspanquery";
        DOC_TYPE_NAME = "test";
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
    void index() throws IOException, InterruptedException, ExecutionException
    {
        BulkRequestBuilder brb = client().prepareBulk();
        
        publicIndex(brb,"voie","1",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","RUE DU PARIS GAGNANT")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("type","voie")
                .field("toto","rue|4 du|2")
                .endObject());
        publicIndex(brb,"voie","2",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","RUE DU GAGNANT DE DOUAI")
                .field("ligne6","75007 SAINT PARIS")
                .field("ligne7","FRANCE")
                .field("commune","SAINT PARIS")
                .field("code_departement","75")
                .field("code_postal","75007")
                .field("code_insee","75107")
                .field("type","voie")
                .endObject());
        publicIndex(brb,"voie","3",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","AA BB CC")
                .field("ligne6","75007 AA BB DD")
                .field("ligne7","FRANCE")
                .field("commune","AA BB DD")
                .field("code_departement","75")
                .field("code_postal","75007")
                .field("code_insee","75107")
                .field("type","voie")
                .endObject());
        publicIndex(brb,"voie","4",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne4","AA BB DD")
                .field("ligne6","75007 CC")
                .field("ligne7","FRANCE")
                .field("commune","CC")
                .field("code_departement","75")
                .field("code_postal","75007")
                .field("code_insee","75107")
                .field("type","voie")
                .endObject());
        
        BulkResponse br = brb.execute().actionGet();
        if (br.hasFailures()) System.out.println(br.buildFailureMessage());
        Assert.assertFalse(br.hasFailures());
        
        refresh();
    }
    
    @Test
    public void testSearch() throws IOException, InterruptedException, ExecutionException
    {
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
        
        // NB: no search analyzer !
        searchExactAdresse("rue pari gagnant","RUE DU PARIS GAGNANT","59500 DOUAI",1); // match 1 not 2
        searchExactAdresse("aa bb cc dd","AA BB CC","75007 AA BB DD",1); // match 3 not 4
        searchExactAdresse("aa bb cc aa","AA BB CC","75007 AA BB DD",1); // match 3 not 4
        searchExactAdresse("aa bb dd cc","AA BB DD","75007 CC",2); // match 3 and 4
    }

    @Override
    QueryBuilder getQueryBuilder(String voie) {
        
        String[] tokens = voie.split(" ");
        
        QueryBuilder qb = new GroupedPayloadSpanQueryBuilder();
        for(int i=0;i<tokens.length;i++)
        {
            MultiPayloadSpanTermQueryBuilder sqb = new MultiPayloadSpanTermQueryBuilder("fullName",tokens[i]);
            ((GroupedPayloadSpanQueryBuilder)qb).clause(sqb);
        }
        
        return qb;
    }
}