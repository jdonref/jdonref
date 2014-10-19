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
public class SynonymWithPayloadsFilterTests extends QueryTests
{
    public SynonymWithPayloadsFilterTests()
    {
        settingsFileName = "./test/resources/index/SynonymWithPayloadsFilter-settings.json";
        INDEX_NAME = "test";
        DOC_TYPE_NAME = "test";
    }
    
    @Override
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"synonymwithpayloadsfilter","adresse"};
        
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
        
        publicIndex(brb,"synonymwithpayloadsfilter","1",XContentFactory.jsonBuilder().startObject()
                .field("fullName","passage|1 square|1 vlge|1 immeubles|1")
                .endObject());
        publicIndex(brb,"synonymwithpayloadsfilter","2",XContentFactory.jsonBuilder().startObject()
                .field("fullName","boulevard|1 CC|1 ld|1")
                .endObject());
        publicIndex(brb,"synonymwithpayloadsfilter","10",XContentFactory.jsonBuilder().startObject()
                .field("fullName","59|1 boulevard|1 de|1 la|1 france|1 02000|1 hopital|1 france|1")
                .endObject());
        publicIndex(brb,"adresse","11",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",59)
                .field("type_de_voie","BOULEVARD")
                .field("article","DE LA")
                .field("libelle","FRANCE")
                .field("ligne4","59 BOULEVARD DE LA FRANCE")
                .field("ligne6","02000 HOPITAL")
                .field("ligne7","FRANCE")
                .field("commune","HOPITAL")
                .field("code_departement","02")
                .field("code_postal","02000")
                .field("code_insee","02001")
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
        
        TermVectorResponse response1 = client().prepareTermVector(INDEX_NAME, "synonymwithpayloadsfilter", "1").get();
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
        
        TermVectorResponse response2 = client().prepareTermVector(INDEX_NAME, "synonymwithpayloadsfilter", "2").get();
        TermsEnum terms2 = response2.getFields().terms("fullName").iterator(null);
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

        TermVectorResponse response3 = client().prepareTermVector(INDEX_NAME, "synonymwithpayloadsfilter", "10").get();
        TermsEnum terms3 = response3.getFields().terms("fullName").iterator(null);
        while((terms3.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms3.docsAndPositions(null, null);
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
        
        TermVectorResponse response4 = client().prepareTermVector(INDEX_NAME, "adresse", "11").get();
        TermsEnum terms4 = response4.getFields().terms("fullName").iterator(null);
        while((terms4.next())!=null)
        {
            DocsAndPositionsEnum docsEnum = terms4.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                }
            }
        }
    }
    
    @Override
    QueryBuilder getQueryBuilder(String voie) {
        return null;
    }
}