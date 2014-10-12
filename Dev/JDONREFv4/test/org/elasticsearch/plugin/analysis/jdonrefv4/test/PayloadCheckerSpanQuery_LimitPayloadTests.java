package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.search.spans.checkers.LimitChecker;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MultiPayloadSpanTermQueryBuilder;
import org.elasticsearch.index.query.PayloadCheckerSpanQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author Julien
 */
public class PayloadCheckerSpanQuery_LimitPayloadTests extends QueryTests
{
    public PayloadCheckerSpanQuery_LimitPayloadTests()
    {
        settingsFileName = "./test/resources/index/PayloadVersusTypeSpanQuery-settings.json";
        INDEX_NAME = "test";
        DOC_TYPE_NAME = "test";
    }
    
    @Override
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"payloadversustypespanquery"};
        
        for(int i=0;i<mappings.length;i++)
        {
            String test = mappings[i];
            String mapping = readFile("./test/resources/mapping/mapping-"+test+".json");
            PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(INDEX_NAME).type(test).source(mapping)).actionGet();
        }
    }
    
    @Override
    void index() throws IOException, InterruptedException, ExecutionException
    {
        BulkRequestBuilder brb = client().prepareBulk();
        
        for(int i=0;i<100;i++)
            publicIndex(brb,"payloadversustypespanquery",Integer.toString(i),XContentFactory.jsonBuilder().startObject()
                .field("ligne4","AA BB CC")
                .field("ligne6","DD EE")
                .field("fullName","AA|3001 BB|3001 CC|3001 DD|2002 EE|2002 FF|1003")
                .endObject());
        
        BulkResponse br = brb.execute().actionGet();
        if (br.hasFailures()) System.out.println(br.buildFailureMessage());
        Assert.assertFalse(br.hasFailures());
    }
    
    @Test
    public void testSearch() throws IOException, InterruptedException, ExecutionException
    {
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
        
        noexplaintest = true;
        
        // NB: no search analyzer !
        searchExactAdresse("aa","AA BB CC","DD EE",5); // only 5 matches (5 shards)
    }
    
    @Override
    QueryBuilder getQueryBuilder(String voie)
    {
        String[] tokens = voie.split(" ");
        
        PayloadCheckerSpanQueryBuilder qb = new PayloadCheckerSpanQueryBuilder();
        for(int i=0;i<tokens.length;i++)
        {
            MultiPayloadSpanTermQueryBuilder sqb = new MultiPayloadSpanTermQueryBuilder("fullName",tokens[i]);
            qb.clause(sqb);
        }
        qb.termCountPayloadFactor(1000);
        
        LimitChecker checker = new LimitChecker(1);
        qb.checker(checker);
        
        return qb;
    }
}