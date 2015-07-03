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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvector.TermVectorResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
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
                //.field("fullName9","24|11 BD|2 HOPITAL|2 75005|3 PARIS|5")
                //.field("fullName9","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 PARIS|5")
                .field("fullName9","1|11 RUE|2 AUGUSTE|2 PERRET|2 95140|3 GARGES|5 LES|5 GONESSE|5")
                .field("fullName10","24 BD GENERAL CHARLES GAULLE 75005 PARIS")
                .field("fullName11","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .field("fullName12","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .field("fullName13","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .field("fullName14","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .field("fullName15","24 BD GENERAL CHARLES GAULLE 75005 75 PARIS")
                .field("fullName16","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .field("fullName17","RUE DE LA FRANCE PARIS")
                .endObject());
        
        publicIndex(brb,"unsplitfilter","2",XContentFactory.jsonBuilder().startObject()
                .field("fullName14","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .field("fullName16","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .endObject());
        
        publicIndex(brb,"unsplitfilter","3",XContentFactory.jsonBuilder().startObject()
                .field("fullName14","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
                .field("fullName16","24|11 BD|2 GENERAL|2 CHARLES|2 GAULLE|2 75005|3 75|3 PARIS|5")
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
            //System.out.println(terms5.term().utf8ToString());
                count5++;
        }
        assert(count5==11);
        
        TermsEnum terms6 = response5.getFields().terms("fullName2").iterator(null);
        int count6 = 0;
        while((terms6.next())!=null)
        {
            //if (terms5.term().utf8ToString().equals(toFind5))
            //System.out.println(terms6.term().utf8ToString());
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
                //System.out.println(terms12.term().utf8ToString());
                count12++;
        }
        assert(count12==2);
        
        TermsEnum terms13 = response5.getFields().terms("fullName9").iterator(null);
        int count13 = 0;
        while((terms13.next())!=null)
        {
                //System.out.println(terms13.term().utf8ToString());
                count13++;
        }
        //System.out.println("Found "+count13);
        assert(count13==633);
        
        TermsEnum terms14 = response5.getFields().terms("fullName10").iterator(null);
        int count14 = 0;
        while((terms14.next())!=null)
        {
                //System.out.println(terms14.term().utf8ToString());
                count14++;
        }
        //System.out.println("Found "+count14);
        assert(count14==1835);
        
        TermsEnum terms15 = response5.getFields().terms("fullName11").iterator(null);
        int count15 = 0;
        while((terms15.next())!=null)
        {
//                System.out.println(terms15.term().utf8ToString());
                count15++;
        }
        //System.out.println("Found "+count15);
        assert(count15==1000);
        
        TermsEnum terms16 = response5.getFields().terms("fullName12").iterator(null);
        int count16 = 0;
        while((terms16.next())!=null)
        {
//                System.out.println(terms16.term().utf8ToString());
                count16++;
        }
        System.out.println("Found "+count16);
        assert(count16==989);
        
        TermsEnum terms17 = response5.getFields().terms("fullName13").iterator(null);
        int count17 = 0;
        while((terms17.next())!=null)
        {
            count17++;
            
            DocsAndPositionsEnum docsEnum = terms17.docsAndPositions(null, null);
            while(docsEnum.nextDoc()!=DocsAndPositionsEnum.NO_MORE_DOCS) // no need for nextPosition for these samples
            {
                int freq = docsEnum.freq();
                int count = 0;
                while(count++<freq)
                {
                    //System.out.println(terms17.term().utf8ToString());
                    docsEnum.nextPosition();
                    BytesRef payload = docsEnum.getPayload();
                    if (payload!=null)
                    {
                        int payloadValue = PayloadHelper.decodeInt(payload.bytes,payload.offset);
                        
                       // System.out.println(terms17.term().utf8ToString()+" : "+payloadValue);
            
                        assert(payloadValue==1011 || payloadValue==4002 || payloadValue==2003 || payloadValue==1005 || 
                               payloadValue==11 || payloadValue==2 || payloadValue==3 || payloadValue==5 ||
                               (payloadValue>=0 && payloadValue<=200));
                    }
                    /*else
                        System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
                }
                /*if (freq==0)
                    System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
            }
        }
        //System.out.println("Found "+count17);
        assert(count17==989);
        
        TermsEnum terms18 = response5.getFields().terms("fullName14").iterator(null);
        int count18 = 0;
        while((terms18.next())!=null)
        {
            count18++;
            
            DocsAndPositionsEnum docsEnum = terms18.docsAndPositions(null, null);
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
                        
                        //System.out.println(terms18.term().utf8ToString()+" : "+payloadValue);
            
                        assert(payloadValue==1011 || payloadValue==4002 || payloadValue==2003 || payloadValue==1005 || 
                               payloadValue==11 || payloadValue==2 || payloadValue==3 || payloadValue==5 ||
                               (payloadValue>=0 && payloadValue<=200));
                    }
                    /*else
                        System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
                }
                /*if (freq==0)
                    System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
            }
        }
        //System.out.println("Found "+count18);
        assert(count18==441);
        
        TermsEnum terms19 = response5.getFields().terms("fullName15").iterator(null);
        int count19 = 0;
        while((terms19.next())!=null)
        {
            count19++;
            
            DocsAndPositionsEnum docsEnum = terms19.docsAndPositions(null, null);
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
                        
//                        System.out.println(terms19.term().utf8ToString()+" : "+payloadValue);
            
                        assert((payloadValue>=5 && payloadValue<=8));
                    }
                    /*else
                        System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
                }
                /*if (freq==0)
                    System.out.println(terms1.term().utf8ToString()+" "+"No payload");*/
            }
        }
        //System.out.println("Found "+count19);
        //assert(count19==441);
        
        TermsEnum terms20 = response5.getFields().terms("fullName16").iterator(null);
        int count20 = 0;
        while((terms20.next())!=null)
        {
                //System.out.println(terms20.term().utf8ToString());
                count20++;
        }
        //System.out.println("Found "+count20);
        //assert(count20==420);
        
        TermQueryBuilder qb20 = new TermQueryBuilder("fullName16", "24bdgeneral");
        SearchResponse search20 = client().prepareSearch().setQuery(qb20).execute().actionGet();
        //System.out.println("Found "+search20.getHits().totalHits());
        assert(search20.getHits().totalHits()==2); // 3 hits without filter
        
        TermQueryBuilder qb21 = new TermQueryBuilder("fullName14", "24bdgeneral");
        SearchResponse search21 = client().prepareSearch().setQuery(qb21).execute().actionGet();
        System.out.println("Found "+search21.getHits().totalHits());
        assert(search21.getHits().totalHits()==3); // 3 hits without filter
        
        TermVectorResponse response6 = client().prepareTermVector(INDEX_NAME, "unsplitfilter", "3").get();
        TermsEnum terms22 = response6.getFields().terms("fullName16").iterator(null);
        int count22 = 0;
        while((terms22.next())!=null)
        {
                //System.out.println(terms22.term().utf8ToString());
                count22++;
        }
        //System.out.println("Found "+count22);
        assert(count22<count20);
        
        TermsEnum terms21 = response5.getFields().terms("fullName17").iterator(null);
        int count21 = 0;
        while((terms21.next())!=null)
        {
                System.out.println(terms21.term().utf8ToString());
                count21++;
        }
        System.out.println("Found "+count21);
        //assert(count20==420);
    }
    
    @Override
    QueryBuilder getQueryBuilder(String voie) {
        return null;
    }

    
}