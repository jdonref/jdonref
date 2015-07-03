package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.analysis.FrequentTermsUtil;
import org.apache.lucene.search.spans.checkers.NullPayloadChecker;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Julien
 */
public class PayloadCheckerSpanQuery_MostFrequentPayloadTests extends QueryTests
{
    public PayloadCheckerSpanQuery_MostFrequentPayloadTests()
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
        
        for(int i=0;i<20;i++) // increase AA and DD frequencies
            publicIndex(brb,"payloadversustypespanquery","a"+i,XContentFactory.jsonBuilder().startObject()
                .field("ligne4","AA")
                .field("ligne6","DD")
                .field("fullName","albert|1001 rose|1002")
                .field("mostFrequentTerms","albertrose")
                .endObject());
        
        for(int i=0;i<5000;i++) // increase AA and DD frequencies
            publicIndex(brb,"payloadversustypespanquery","b"+i,XContentFactory.jsonBuilder().startObject()
                .field("ligne4","AA")
                .field("ligne6","DD")
                .field("fullName","albert|1001 toutou|1003")
                .field("mostFrequentTerms","albert")
                .endObject());
        
        for(int i=0;i<5000;i++) // increase AA and DD frequencies
            publicIndex(brb,"payloadversustypespanquery","c"+i,XContentFactory.jsonBuilder().startObject()
                .field("ligne4","AA")
                .field("ligne6","DD")
                .field("fullName","rose|1001 toutou|1003")
                .field("mostFrequentTerms","rose")
                .endObject());
        
        BulkResponse br = brb.setRefresh(true).execute().actionGet();
        if (br.hasFailures()) System.out.println(br.buildFailureMessage());
        Assert.assertFalse(br.hasFailures());
    }
    
    @Test
    public void testSearch() throws IOException, InterruptedException, ExecutionException
    {
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
        
        // NB: no search analyzer !
        searchExactAdresse("rose albert","AA","DD"); // match 
        
        // over limit match
        System.out.println("TEST 2 : searching 'toutou rose' in ["+INDEX_NAME+"]");
        QueryBuilder qb = getQueryBuilder("toutou rose");
        long start = Calendar.getInstance().getTimeInMillis();
        SearchResponse search = client().prepareSearch(INDEX_NAME).setQuery(qb).setExplain(true).execute().actionGet();
        long end = Calendar.getInstance().getTimeInMillis();
        System.out.println("Took "+(end-start)+" ms");
        SearchHit[] hits = search.getHits().getHits();
        assert(hits[0].score()==1);
    }

    @Override
    QueryBuilder getQueryBuilder(String voie)
    {
        String[] tokens = voie.split(" ");
        
        FrequentTermsUtil.setFilePath("src/resources/analysis/word84.txt");
        
        PayloadCheckerSpanFilterBuilder qb = new PayloadCheckerSpanFilterBuilder();
        for(int i=0;i<tokens.length;i++)
        {
            MultiPayloadSpanTermFilterBuilder sqb = new MultiPayloadSpanTermFilterBuilder("fullName",tokens[i]);
            qb.clause(sqb);
        }
        NullPayloadChecker checker = new NullPayloadChecker();
        qb.checker(checker);
        //qb.limit(20); // sharding need to reduce that number a lot
        
        return new FilteredQueryBuilder(new MatchAllQueryBuilder(),qb);
    }
}