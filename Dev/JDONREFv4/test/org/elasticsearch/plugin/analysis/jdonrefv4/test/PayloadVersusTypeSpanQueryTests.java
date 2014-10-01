package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.search.spans.TermVectorMultiPayloadSpanTermQuery;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.lucene.Lucene;
import org.elasticsearch.common.xcontent.XContentFactory;
//import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
//import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope;
import org.elasticsearch.index.query.GroupedPayloadSpanQueryBuilder;
import org.elasticsearch.index.query.MultiPayloadSpanTermQueryBuilder;
import org.elasticsearch.index.query.PayloadVersusTypeSpanQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SpanQueryBuilder;
import org.elasticsearch.index.query.SpanTermQueryBuilder;
import org.elasticsearch.index.query.TermVectorMultiPayloadSpanTermQueryBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author Julien
 */
public class PayloadVersusTypeSpanQueryTests extends QueryTests
{
    public PayloadVersusTypeSpanQueryTests()
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
        
        publicIndex(brb,"payloadversustypespanquery","1",XContentFactory.jsonBuilder().startObject()
                .field("ligne4","AA BB")
                .field("ligne6","CC")
                .field("fullName","AA|1002 BB|1002 CC|2001 DD|3001")
                .endObject());
        publicIndex(brb,"payloadversustypespanquery","2",XContentFactory.jsonBuilder().startObject()
                .field("ligne4","AA")
                .field("ligne6","CC")
                .field("fullName","AA|1001 CC|2001 DD|3001")
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
        searchExactAdresse("aa bb","AA BB","CC",1); // match 1 only
        searchExactAdresse("aa","AA","CC",1); // match 2 only
        searchExactAdresse("cc","AA","CC",0); // no match
    }

    @Override
    QueryBuilder getQueryBuilder(String voie) {
        
        String[] tokens = voie.split(" ");
        
        PayloadVersusTypeSpanQueryBuilder qb = new PayloadVersusTypeSpanQueryBuilder();
        for(int i=0;i<tokens.length;i++)
        {
            TermVectorMultiPayloadSpanTermQueryBuilder sqb = new TermVectorMultiPayloadSpanTermQueryBuilder("fullName",tokens[i]);
            qb.clause(sqb);
        }
        
        IntegerEncoder encoder = new IntegerEncoder();
        BytesRef bytes = encoder.encode("1".toCharArray());
        qb.requiredPayloads("payloadversustypespanquery", new BytesRef[]{bytes});
        qb.termCountPayloadFactor(1000);
        
        return qb;
    }
}