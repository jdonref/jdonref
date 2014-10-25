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
public class EdgeNGramWithPayloadsFilterTests extends QueryTests
{
    public EdgeNGramWithPayloadsFilterTests()
    {
        settingsFileName = "./test/resources/index/EdgeNGramWithPayloadsFilter-settings.json";
        INDEX_NAME = "test";
        DOC_TYPE_NAME = "test";
    }
    
    @Override
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"edgengramwithpayloadsfilter"};
        
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
        
        publicIndex(brb,"edgengramwithpayloadsfilter","1",XContentFactory.jsonBuilder().startObject()
                .field("fullName","AA|1 BB|1 CC|1 DD|1")
                .field("fullName2","passage|1 square|1 vlge|1 immeubles|1")
                .field("fullName3","24|1 BOULEVARD|2 HOPITAL|2 75013|3 PARIS|4")
                .field("fullName4","24|1 BOULEVARD|2 HOPITAL|2 75013|3 PARIS|4")
                .endObject());
        publicIndex(brb,"edgengramwithpayloadsfilter","2",XContentFactory.jsonBuilder().startObject()
                .field("fullName","AA|1 CC|1 DD|1")
                .field("fullName2","boulevard|1 CC|1 ld|1")
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
        
        TermVectorResponse response1 = client().prepareTermVector(INDEX_NAME, "edgengramwithpayloadsfilter", "1").get();
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
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                    assert(payloadValue==1);
                }
            }
        }
        terms1 = response1.getFields().terms("fullName2").iterator(null);
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
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                    assert(payloadValue==1);
                }
            }
        }
        
        TermVectorResponse response2 = client().prepareTermVector(INDEX_NAME, "edgengramwithpayloadsfilter", "2").get();
        TermsEnum terms2 = response2.getFields().terms("fullName2").iterator(null);
        while((terms2.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms2.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                    assert(payloadValue==1);
                }
            }
        }
        terms2 = response2.getFields().terms("fullName2").iterator(null);
        while((terms2.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms2.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                    assert(payloadValue==1);
                }
            }
        }
        
        // KeepNumber test
        TermsEnum terms5 = response1.getFields().terms("fullName3").iterator(null);
        int count5 = 0;
        while((terms5.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms5.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                    if (payloadValue == 1) count5++;
                }
            }
        }
        assert(count5==1); // assert 24 is not ngramed
        
        TermsEnum terms6 = response1.getFields().terms("fullName4").iterator(null);
        int count6 = 0;
        while((terms6.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms6.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                    if (payloadValue == 1) count6++;
                }
            }
        }
        assert(count6==1); // assert 24 is present
    }
    
    @Override
    QueryBuilder getQueryBuilder(String voie) {
        return null;
    }
}