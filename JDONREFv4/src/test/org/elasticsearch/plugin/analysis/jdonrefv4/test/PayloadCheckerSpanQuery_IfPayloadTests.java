package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.search.spans.checkers.AndPayloadChecker;
import org.apache.lucene.search.spans.checkers.GroupedPayloadChecker;
import org.apache.lucene.search.spans.checkers.FieldChecker;
import org.apache.lucene.search.spans.checkers.IfPayloadChecker;
import org.apache.lucene.search.spans.checkers.OnePayloadChecker;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author Julien
 */
public class PayloadCheckerSpanQuery_IfPayloadTests extends QueryTests
{
    public PayloadCheckerSpanQuery_IfPayloadTests()
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
                .field("ligne4","AA BB CC")
                .field("ligne6","DD EE")
                .field("fullName","AA|3001 BB|3001 CC|3001 DD|2002 EE|2002 FF|1003")
                .endObject());
        publicIndex(brb,"anothertype","2",XContentFactory.jsonBuilder().startObject()
                .field("ligne4","AA BB EE")
                .field("ligne6","DD EE")
                .field("fullName","AA|3001 BB|3001 EE|3001 DD|2002 EE|2002 FF|1003")
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
        
        // NB: no search analyzer !
        searchExactAdresse("aa dd cc","AA BB EE","DD EE",0); // no match
        searchExactAdresse("aa bb cc ee","AA BB CC","DD EE",1); // match 1
        searchExactAdresse("aa bb ee","AA BB CC","DD EE",2); // match 1 & 2
    }

    @Override
    QueryBuilder getQueryBuilder(String voie)
    {
        String[] tokens = voie.split(" ");
        
        PayloadCheckerSpanFilterBuilder qb = new PayloadCheckerSpanFilterBuilder();
        for(int i=0;i<tokens.length;i++)
        {
            MultiPayloadSpanTermFilterBuilder sqb = new MultiPayloadSpanTermFilterBuilder("fullName",tokens[i]);
            qb.clause(sqb);
        }
        qb.termCountPayloadFactor(1000);
        
        IntegerEncoder encoder = new IntegerEncoder();
        BytesRef bytes1 = encoder.encode("1".toCharArray());
        
        FieldChecker condition = new FieldChecker("payloadversustypespanquery");
        GroupedPayloadChecker then = new GroupedPayloadChecker();
        
        IfPayloadChecker checker = new IfPayloadChecker(condition, then);
        qb.checker(checker);
        
        return new FilteredQueryBuilder(new MatchAllQueryBuilder(),qb);
    }
}