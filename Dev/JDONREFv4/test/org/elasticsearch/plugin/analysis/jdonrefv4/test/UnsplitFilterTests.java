package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.lucene.index.TermsEnum;
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
public class UnsplitFilterTests extends QueryTests
{
    public UnsplitFilterTests()
    {
        settingsFileName = "./test/resources/index/UnsplitFilter-settings.json";
        INDEX_NAME = "test";
        DOC_TYPE_NAME = "test";
    }
    
    @Override
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"unsplitfilter"};
        
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
        
        publicIndex(brb,"unsplitfilter","1",XContentFactory.jsonBuilder().startObject()
                .field("fullName","AA BB CC DD")
                .field("fullName2","AA BB CC DD")
                .field("fullName3","AA BB CC DD")
                .field("fullName4","AA BB CC DD")
                .field("fullName5","AA BB CC DD")
                .field("fullName6","AA BB CC DD")
                .field("fullName7","24 BD HOPITAL 75005 PARIS")
                .field("fullName8","24 BD HOPITAL 75005 PARIS")
                //.field("fullName8","10 boulevard pasteur par")
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
        TermVectorResponse response5 = client().prepareTermVector(INDEX_NAME, "unsplitfilter", "1").get();
        TermsEnum terms5 = response5.getFields().terms("fullName").iterator(null);
        int count5 = 0;
        while((terms5.next())!=null)
        {
            //if (terms5.term().utf8ToString().equals(toFind5))
                count5++;
        }
        assert(count5==11);
        
        TermsEnum terms6 = response5.getFields().terms("fullName2").iterator(null);
        int count6 = 0;
        while((terms6.next())!=null)
        {
            //if (terms5.term().utf8ToString().equals(toFind5))
                count6++;
        }
        assert(count6==15);
        
        TermsEnum terms7 = response5.getFields().terms("fullName3").iterator(null);
        int count7 = 0;
        while((terms7.next())!=null)
        {
            //if (terms5.term().utf8ToString().equals(toFind5))
                count7++;
        }
        assert(count7==5);
        
        TermsEnum terms8 = response5.getFields().terms("fullName4").iterator(null);
        int count8 = 0;
        while((terms8.next())!=null)
        {
            //if (terms5.term().utf8ToString().equals(toFind5))
                count8++;
        }
        assert(count8==9);
        
        TermsEnum terms9 = response5.getFields().terms("fullName5").iterator(null);
        int count9 = 0;
        while((terms9.next())!=null)
        {
            //System.out.println(terms9.term().utf8ToString());
            count9++;
        }
        assert(count9==21);
        
        TermsEnum terms10 = response5.getFields().terms("fullName6").iterator(null);
        int count10 = 0;
        while((terms10.next())!=null)
        {
            //System.out.println(terms10.term().utf8ToString());
            //if (terms5.term().utf8ToString().equals(toFind5))
                count10++;
        }
        assert(count10==29);
        
        TermsEnum terms11 = response5.getFields().terms("fullName7").iterator(null);
        int count11 = 0;
        while((terms11.next())!=null)
        {
            //System.out.println(terms11.term().utf8ToString());
                count11++;
        }
        assert(count11==18);
        
        TermsEnum terms12 = response5.getFields().terms("fullName8").iterator(null);
        int count12 = 0;
        while((terms12.next())!=null)
        {
                System.out.println(terms12.term().utf8ToString());
                count12++;
        }
        assert(count12==2);
    }
    
    @Override
    QueryBuilder getQueryBuilder(String voie) {
        return null;
    }
}