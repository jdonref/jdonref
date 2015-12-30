package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.termvector.TermVectorResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author Julien
 */
public class TokenCountPayloadsFilterTests extends QueryTests
{
    public TokenCountPayloadsFilterTests()
    {
        settingsFileName = "./test/resources/index/TokenCountPayloadsFilter-settings.json";
        INDEX_NAME = "test";
        DOC_TYPE_NAME = "test";
    }
    
    @Override
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"tokencountpayloadsfilter"};
        
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
        
        publicIndex(brb,"tokencountpayloadsfilter","1",XContentFactory.jsonBuilder().startObject()
                .field("fullName","AA|1 BB|1 CC|2 DD|3")
                .endObject());
        publicIndex(brb,"tokencountpayloadsfilter","2",XContentFactory.jsonBuilder().startObject()
                .field("fullName","AA|1 CC|2 DD|3")
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
        
        TermVectorResponse response1 = client().prepareTermVector(INDEX_NAME, "tokencountpayloadsfilter", "1").get();
        TermsEnum terms1 = response1.getFields().terms("fullName").iterator(null);
        while((terms1.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms1.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    if (payload!=null)
                    {
                        int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                        //System.out.println(terms1.term().utf8ToString()+" "+payloadValue);
                        assert(payloadValue==2001 || payloadValue==1002 || payloadValue==1003 || 
                               payloadValue==1 || payloadValue==2 || payloadValue==3 || payloadValue==0);
                    }
                    /*else
                        System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
                }
                /*if (freq==0)
                    System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
            }
        }
        
        TermVectorResponse response2 = client().prepareTermVector(INDEX_NAME, "tokencountpayloadsfilter", "2").get();
        TermsEnum terms2 = response2.getFields().terms("fullName").iterator(null);
        while((terms2.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms1.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                    assert(payloadValue==1001 || payloadValue==1002 || payloadValue==1003 || 
                           payloadValue==1 || payloadValue==2 || payloadValue==3 || payloadValue==0);
                }
            }
        }
    }
    
    @Override
    QueryBuilder getQueryBuilder(String voie) {
        return null;
    }
}