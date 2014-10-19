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
import org.elasticsearch.index.query.jdonrefv4.JDONREFv4QueryBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 * @author Julien
 */
public class JDONREFv4QueryTests extends QueryTests
{
    public JDONREFv4QueryTests()
    {
        settingsFileName = "./test/resources/index/jdonrefv4-settings_Query.json";
        INDEX_NAME = "test";
        DOC_TYPE_NAME = "test";
    }
    
    @Override
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
    
    @Override
    void index() throws IOException, InterruptedException, ExecutionException
    {
        BulkRequestBuilder brb = client().prepareBulk();
        
        publicIndex(brb,"pays","FR",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .endObject());
        publicIndex(brb,"pays","DE",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","DE")
                .field("ligne7","ALLEMAGNE")
                .endObject());
        publicIndex(brb,"commune","75056",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","75000")
                .field("code_insee","75056")
                .field("commune","PARIS")
                .field("ligne6","PARIS")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("code_departement","75")
                .endObject());
        publicIndex(brb,"commune","59500",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("commune","DOUAI")
                .field("ligne6","DOUAI")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("code_departement","59")
                .endObject());
        publicIndex(brb,"voie","1",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("type_de_voie","BOULEVARD")
                .field("article","DE L")
                .field("libelle","HOPITAL")
                .field("ligne4","BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .endObject());
        publicIndex(brb,"voie","2",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("type_de_voie","RUE")
                //.field("article","DE LA")
                .field("libelle","REMY DUHEM")
                .field("ligne4","RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        publicIndex(brb,"voie","3",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("type_de_voie","RUE")
                .field("article","DE LA")
                .field("libelle","FRANCE")
                .field("ligne4","RUE DE LA FRANCE")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .endObject());
        publicIndex(brb,"adresse","1",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",24)
                .field("type_de_voie","BOULEVARD")
                .field("article","DE L")
                .field("libelle","HOPITAL")
                .field("ligne4","24 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("code_departement","75")
                .field("code_postal","75013")
                .field("code_insee","75013")
                .endObject());
        publicIndex(brb,"adresse","2",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",24)
                .field("type_de_voie","RUE")
                .field("article","DE LA")
                .field("libelle","FRANCE")
                .field("ligne4","24 RUE DE LA FRANCE")
                .field("ligne6","75013 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("numero",24)
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75113")
                .endObject());
        publicIndex(brb,"adresse","3",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",75)
                .field("type_de_voie","BOULEVARD")
                .field("article","DE L")
                .field("libelle","HOPITAL")
                .field("ligne4","75 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("numero",75)
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .endObject());
        
        for(int i=130;i<500;i++) // inclus donc le numéro 130
        {
            publicIndex(brb,"adresse","4"+i,XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",i)
                .field("type_de_voie","RUE")
                //.field("article","")
                .field("libelle","REMY DUHEM")
                .field("ligne4",i+" RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        }
        
        publicIndex(brb,"adresse","5",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",131)
                .field("type_de_voie","RUE")
                //.field("article","")
                .field("libelle","REMY DUHEM")
                .field("ligne4","131 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        
        publicIndex(brb,"adresse","6",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",59)
                .field("type_de_voie","RUE")
                //.field("article","")
                .field("libelle","REMY DUHEM")
                .field("ligne4","59 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        publicIndex(brb,"adresse","7",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",75)
                .field("type_de_voie","RUE")
                .field("libelle","REMY DUHEM")
                .field("ligne4","75 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        publicIndex(brb,"adresse","7",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",75)
                .field("type_de_voie","RUE")
                .field("libelle","REMY DUHEM")
                .field("ligne4","75 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        publicIndex(brb,"adresse","8",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",130)
                .field("type_de_voie","BOULEVARD")
                .field("article","DE L")
                .field("libelle","HOPITAL")
                .field("ligne4","130 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .endObject());
        publicIndex(brb,"adresse","9",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("numero",59)
                .field("type_de_voie","BOULEVARD")
                .field("article","DE L")
                .field("libelle","HOPITAL")
                .field("ligne4","59 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("code_departement","75")
                .field("code_postal","75005")
                .field("code_insee","75105")
                .endObject());
        publicIndex(brb,"adresse","10",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,"poizon","4",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB1")
                .field("poizon_service",1)
                .field("numero",130)
                .field("type_de_voie","RUE")
                //.field("article","")
                .field("libelle","REMY DUHEM")
                .field("ligne1","KEBAB LA P'TITE FRITE")
                .field("ligne4","130 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        publicIndex(brb,"poizon","5",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB1")
                .field("poizon_service",1)
                .field("ligne1","KEBAB DU COIN")
                .field("numero",131)
                .field("type_de_voie","RUE")
                //.field("article","")
                .field("libelle","REMY DUHEM")
                .field("ligne4","131 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        publicIndex(brb,"poizon","6",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB2")
                .field("poizon_service",1)
                .field("numero",59)
                .field("type_de_voie","RUE")
                //.field("article","")
                .field("libelle","REMY DUHEM")
                .field("ligne1","KEBAB LA GROSSE FRITE")
                .field("ligne4","59 RUE REMY DUHEM")
                .field("ligne6","59500 DOUAI")
                .field("ligne7","FRANCE")
                .field("commune","DOUAI")
                .field("code_departement","59")
                .field("code_postal","59500")
                .field("code_insee","59500")
                .endObject());
        publicIndex(brb,"poizon","7",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_id","KEBAB3")
                .field("poizon_service",1)
                .field("numero",75)
                .field("type_de_voie","RUE")
                //.field("article","")
                .field("libelle","REMY DUHEM")
                .field("ligne1","KEBAB DU COIN")
                .field("ligne4","75 RUE REMY DUHEM")
                .field("ligne6","75015 PARIS")
                .field("ligne7","FRANCE")
                .field("commune","PARIS")
                .field("code_departement","75")
                .field("code_postal","75015")
                .field("code_insee","75115")
                .endObject());
        publicIndex(brb,"departement","75",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne6","75")
                .field("ligne7","FRANCE")
                .field("code_departement","75")
                .endObject());
        publicIndex(brb,"departement","59",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne6","59")
                .field("ligne7","FRANCE")
                .field("code_departement","59")
                .endObject());
        
        for(int i=0;i<10;i++)
        {
            String randomCode = "RANDOM"+i;
            String randomLigne7 = "RANDOM RANDOM "+i+""+i;
            publicIndex(brb,"pays",randomCode,XContentFactory.jsonBuilder().startObject()
                  .field("code_pays",randomCode)
                  .field("ligne7",randomLigne7)
                  .endObject());
        }
        
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
        
        ////////////
        /// VOIE
        // voie avec uniquement commune
        searchExactAdresse("RUE DE LA FRANCE PARIS","RUE DE LA FRANCE","75005 PARIS"); // match
        
        // voie avec uniquement le code département
        searchExactAdresse("RUE DE LA FRANCE 75","RUE DE LA FRANCE","75005 PARIS"); // match
        
        // voie partielle avec uniquement le code département
        searchExactAdresse("FRANCE 75","RUE DE LA FRANCE","75005 PARIS"); // match
        
        // voie partielle avec uniquement le code postal
        searchExactAdresse("FRANCE 75005","RUE DE LA FRANCE","75005 PARIS"); // match
        
        ////////////
        /// ADRESSES
        
        // adresse avec uniquement commune
        searchExactAdresse("59 BOULEVARD DE LA FRANCE HOPITAL","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
        
        // adresse avec uniquement code département
        searchExactAdresse("59 BOULEVARD DE LA FRANCE 02","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
        
        // adresse avec uniquement code postal
        searchExactAdresse("59 BOULEVARD DE LA FRANCE 02000","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
        
        // adresse avec uniquement code insee
        searchExactAdresse("59 BOULEVARD DE LA FRANCE 02001","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
        
        // adresse sans son numéro d'adresse => fail
        searchExactAdresse("BOULEVARD DE LA FRANCE 02001","59 BOULEVARD DE LA FRANCE","02000 HOPITAL",0); // no match
        
        // adresse avec code postal intercalé, mais FRANCE correspond au pays
        searchExactAdresse("59 BOULEVARD DE LA 02001 FRANCE","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
        
        // adresse avec code postal intercalé => fail
        searchExactAdresse("59 02001 BOULEVARD DE LA FRANCE","59 BOULEVARD DE LA FRANCE","02000 HOPITAL",0); // match
    }

    @Override
    QueryBuilder getQueryBuilder(String voie)
    {
        JDONREFv4QueryBuilder qb = new JDONREFv4QueryBuilder(voie);
        
        return qb;
    }

//    @Test
//    public void testSearch() throws IOException, InterruptedException, ExecutionException
//    {
//        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
//        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
//        
//        // 183 tests au total
//        
//        GetResponse resp = client().prepareGet().setFields("fullName").setId("4130").setFetchSource(true).setIndex(INDEX_NAME).setType("adresse").execute().actionGet();
//        System.out.println("Doc 1 source:" +resp.getSourceAsString());
//        System.out.println("Doc 1 fullName:" +resp.getField("fullName").getValue());
//        
//        searchExactAdresse("rue remy duhem 59500 douai france","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,0);
//        searchExactAdresse("130 RUE REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 RUE REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 DUHEM 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 DUHEM 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY 59500 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY 59500 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        refresh();
//        searchExactAdresse("130 RUE REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("130 RUE REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 DUHEM 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,135.0f);
//        searchExactAdresse("130 DUHEM DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f); // .. 
//        searchExactAdresse("130 DUHEM 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 REMY 59500 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 REMY DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 REMY 59500","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        
//        // 59505 non pris en charge. dirty results
//        /*refresh();
//        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,180.0f);
//        searchExactAdresse("130 RUE REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 RUE REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 RUE DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 REMY DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 DUHEM 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 DUHEM 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 REMY 59505 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY 59505 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        refresh();
//        searchExactAdresse("130 RUE REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,180.0f);
//        searchExactAdresse("130 RUE REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 RUE REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 RUE DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 REMY DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 DUHEM 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 DUHEM 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 REMY 59505 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY 59505","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);*/
//        
//        refresh();
//        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("130 RUE REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 RUE REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 RUE DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 DUHEM 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 DUHEM DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 DUHEM 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 REMY 59 DOUAI FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY 59 FRANCE","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 RUE REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
//        searchExactAdresse("130 RUE REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 REMY DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("130 RUE REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 RUE REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 RUE DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 DUHEM 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("130 DUHEM 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("130 REMY 59 DOUAI","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("130 REMY 59","130 RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        
//        refresh();
//        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("RUE REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("REMY DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("REMY DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
//        searchExactAdresse("RUE REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("REMY DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("DUHEM 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("DUHEM DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("DUHEM 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY 59500 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY 59500 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        refresh();
//        searchExactAdresse("RUE REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("RUE REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("REMY DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("RUE DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("REMY DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("RUE REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
//        searchExactAdresse("RUE REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("RUE DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("REMY DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("DUHEM 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("DUHEM DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("DUHEM 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("REMY 59500 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("REMY 59500","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f); 
//        refresh();
//        searchExactAdresse("RUE REMY DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("RUE REMY 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("REMY DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("REMY DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("DUHEM 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("DUHEM 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY 59 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY 59 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("RUE REMY DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,200.0f);
//        searchExactAdresse("RUE REMY 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("REMY DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,160.0f);
//        searchExactAdresse("RUE REMY 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("RUE DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("REMY DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("DUHEM 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("DUHEM 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("REMY 59 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY 59","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        
//        /*refresh();
//        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,180.0f);
//        searchExactAdresse("RUE REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("RUE DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("REMY DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("RUE REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("RUE REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("RUE DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("REMY DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("DUHEM 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("DUHEM 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
//        searchExactAdresse("REMY 59505 DOUAI FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("REMY 59505 FRANCE","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
//        searchExactAdresse("RUE REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,140.0f);
//        searchExactAdresse("RUE REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("RUE DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("REMY DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,120.0f);
//        searchExactAdresse("RUE REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,100.0f);
//        searchExactAdresse("RUE REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
//        searchExactAdresse("RUE DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
//        searchExactAdresse("REMY DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
//        searchExactAdresse("DUHEM 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
//        searchExactAdresse("DUHEM 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);
//        searchExactAdresse("REMY 59505 DOUAI","RUE REMY DUHEM 59500 DOUAI FRANCE",0,30.0f);
//        searchExactAdresse("REMY 59505","RUE REMY DUHEM 59500 DOUAI FRANCE",0,60.0f);*/
//        
//        refresh();
//        searchExactAdresse("59500 DOUAI FRANCE","59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("59500 FRANCE","59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("59500 DOUAI","59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("59500","59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("DOUAI FRANCE","59500 DOUAI FRANCE",0,199.99f);
//        searchExactAdresse("DOUAI","59500 DOUAI FRANCE",0,199.99f);
//        refresh();
//        searchExactAdresse("59505 DOUAI FRANCE","59500 DOUAI FRANCE",0,50.0f); // réduction du score : 59505 n'est pas pris en charge
//        //searchExactAdresse("59505 FRANCE","59500 DOUAI FRANCE"); // 59505 n'est pas encore pris en charge
//        searchExactAdresse("59505 DOUAI","59500 DOUAI FRANCE",0,20.0f); // réduction du score : 59505 n'est pas pris en charge
//        //searchExactAdresse("59505","59500 DOUAI FRANCE"); // 59505 n'est pas encore pris en charge
//        
//        searchExactAdresse("59 59500 DOUAI","59500 DOUAI FRANCE",0,200.0f);
//        
//        refresh();
//        searchExactAdresse("59 DOUAI FRANCE","59500 DOUAI FRANCE",0,180.0f);
//        searchExactAdresse("59 DOUAI","59500 DOUAI FRANCE",0,170.0f); // mark
//        //searchExactAdresse("59 FRANCE","59 FRANCE"); // DOUAI n'est pas le meilleur résultat dans ce cas !
//
//        refresh();
//        searchExactAdresse("59 FRANCE","59 FRANCE",0,199.99f);
//        searchExactAdresse("59","59 FRANCE",0,199.99f);
//        //searchExactAdresse("59 FR","59 FRANCE",0,199.99f); // not supported for now
//        //searchExactAdresse("59 FR FRANCE","59 FRANCE",0,199.99f);
//        
//        refresh();
//        searchExactAdresse("FRANCE","FRANCE",0,199.99f);
//        searchExactAdresse("FR FRANCE","FRANCE",0,199.99f);
//        
//        refresh();
//        searchExactAdresse("KEBAB FRITE DOUAI","KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE",1,100.0f);
//        searchExactAdresse("KEBAB DOUAI","KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE",2,50.0f);
//        searchExactAdresse("KEBAB RUE REMY DUHEM","KEBAB LA P'TITE FRITE 130 RUE REMY DUHEM 59500 DOUAI FRANCE",2,50.0f);
//    }
}