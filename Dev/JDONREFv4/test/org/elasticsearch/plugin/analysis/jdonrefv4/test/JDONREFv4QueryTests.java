package org.elasticsearch.plugin.analysis.jdonrefv4.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.lucene.search.jdonrefv4.JDONREFv4Query;
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
    String PAYS_INDEX = "pays";
    String DPT_INDEX = "departement";
    String COMMUNE_INDEX = "commune";
    String VOIE_INDEX = "voie";
    String TRONCON_INDEX = "troncon";
    String ADR_INDEX = "adresse";
    String POIZON_INDEX = "poizon";
    
    public JDONREFv4QueryTests()
    {
        settingsFileName = "./test/resources/index/jdonrefv4-settings_Query.json";
        INDEX_NAME = "test";
        DOC_TYPE_NAME = "test";
    }
    
    @Override
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] indices = new String[]{PAYS_INDEX,DPT_INDEX,COMMUNE_INDEX,VOIE_INDEX,TRONCON_INDEX,ADR_INDEX,POIZON_INDEX};
        String[] mappings = new String[]{"pays","departement","commune","voie","troncon","adresse","poizon"};
        
        for(int i=0;i<mappings.length;i++)
        {
            String test = mappings[i];
            String mapping = readFile("./test/resources/mapping/mapping-"+test+".json");
            PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(indices[i]).type(test).source(mapping)).actionGet();
        }
    }
    
    @Override
    public void deleteIndex()
    {
        client().admin().indices().prepareDelete(PAYS_INDEX).execute().actionGet();
        client().admin().indices().prepareDelete(DPT_INDEX).execute().actionGet();
        client().admin().indices().prepareDelete(COMMUNE_INDEX).execute().actionGet();
        client().admin().indices().prepareDelete(VOIE_INDEX).execute().actionGet();
        client().admin().indices().prepareDelete(TRONCON_INDEX).execute().actionGet();
        client().admin().indices().prepareDelete(ADR_INDEX).execute().actionGet();
        client().admin().indices().prepareDelete(POIZON_INDEX).execute().actionGet();
    }
    
    @Override
    public void createIndex() throws FileNotFoundException, IOException
    {
        String settings = readFile(settingsFileName);
        client().admin().indices().prepareCreate(PAYS_INDEX).setSettings(settings).execute().actionGet();
        client().admin().indices().prepareCreate(DPT_INDEX).setSettings(settings).execute().actionGet();
        client().admin().indices().prepareCreate(COMMUNE_INDEX).setSettings(settings).execute().actionGet();
        client().admin().indices().prepareCreate(VOIE_INDEX).setSettings(settings).execute().actionGet();
        client().admin().indices().prepareCreate(TRONCON_INDEX).setSettings(settings).execute().actionGet();
        client().admin().indices().prepareCreate(ADR_INDEX).setSettings(settings).execute().actionGet();
        client().admin().indices().prepareCreate(POIZON_INDEX).setSettings(settings).execute().actionGet();
    }
    
    @Override
    void index() throws IOException, InterruptedException, ExecutionException
    {
        BulkRequestBuilder brb = client().prepareBulk();
        
        publicIndex(brb,PAYS_INDEX,"pays","FR",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .endObject());
        publicIndex(brb,PAYS_INDEX,"pays","DE",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","DE")
                .field("ligne7","ALLEMAGNE")
                .endObject());
        publicIndex(brb,COMMUNE_INDEX,"commune","75056",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","75000")
                .field("code_insee","75056")
                .field("commune","PARIS")
                .field("ligne6","PARIS")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("code_departement","75")
                .endObject());
        publicIndex(brb,COMMUNE_INDEX,"commune","59500",XContentFactory.jsonBuilder().startObject()
                .field("code_postal","59500")
                .field("code_insee","59500")
                .field("commune","DOUAI")
                .field("ligne6","DOUAI")
                .field("code_pays","FR")
                .field("ligne7","FRANCE")
                .field("code_departement","59")
                .endObject());
        publicIndex(brb,VOIE_INDEX,"voie","1",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,VOIE_INDEX,"voie","2",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,VOIE_INDEX,"voie","3",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","1",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","2",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","3",XContentFactory.jsonBuilder().startObject()
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
            publicIndex(brb,ADR_INDEX,"adresse","4"+i,XContentFactory.jsonBuilder().startObject()
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
        
        publicIndex(brb,ADR_INDEX,"adresse","5",XContentFactory.jsonBuilder().startObject()
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
        
        publicIndex(brb,ADR_INDEX,"adresse","6",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","7",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","7",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","8",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","9",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,ADR_INDEX,"adresse","10",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,POIZON_INDEX,"poizon","4",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,POIZON_INDEX,"poizon","5",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,POIZON_INDEX,"poizon","6",XContentFactory.jsonBuilder().startObject()
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
        publicIndex(brb,POIZON_INDEX,"poizon","7",XContentFactory.jsonBuilder().startObject()
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
         //"pin":{"centroide":[2.31373310272827,48.926365812723]}}]}
        publicIndex(brb,POIZON_INDEX,"poizon","139",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("poizon_service",105)
                .field("ligne1","MCI 92 GENNEVILLIERS ZONE INDUSTRIELLE")
                .field("ligne6","92230")
                .field("ligne7","FRANCE")
                .field("pays","FRANCE")
                .field("t0","2014-05-19 00:00:00")
                .field("t1","2064-05-19 00:00:00")
                .endObject());        
        publicIndex(brb,DPT_INDEX,"departement","75",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne6","75")
                .field("ligne7","FRANCE")
                .field("code_departement","75")
                .endObject());
        publicIndex(brb,DPT_INDEX,"departement","59",XContentFactory.jsonBuilder().startObject()
                .field("code_pays","FR")
                .field("ligne6","59")
                .field("ligne7","FRANCE")
                .field("code_departement","59")
                .endObject());
        
        for(int i=0;i<10;i++)
        {
            String randomCode = "RANDOM"+i;
            String randomLigne7 = "RANDOM RANDOM "+i+""+i;
            publicIndex(brb,PAYS_INDEX,"pays",randomCode,XContentFactory.jsonBuilder().startObject()
                  .field("code_pays",randomCode)
                  .field("ligne7",randomLigne7)
                  .endObject());
        }
        
        BulkResponse br = brb.execute().actionGet();
        if (br.hasFailures()) System.out.println(br.buildFailureMessage());
        Assert.assertFalse(br.hasFailures());
        
        refresh();
        client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute();
    }

    @Test
    public void testSearch() throws IOException, InterruptedException, ExecutionException
    {
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(PAYS_INDEX+" num docs : "+indResponse.getIndex(PAYS_INDEX).getDocs().getNumDocs());
        System.out.println(DPT_INDEX+" num docs : "+indResponse.getIndex(DPT_INDEX).getDocs().getNumDocs());
        System.out.println(COMMUNE_INDEX+" num docs : "+indResponse.getIndex(COMMUNE_INDEX).getDocs().getNumDocs());
//        //System.out.println(TRONCON_INDEX+" num docs : "+indResponse.getIndex(TRONCON_INDEX).getDocs().getNumDocs());
        System.out.println(VOIE_INDEX+" num docs : "+indResponse.getIndex(VOIE_INDEX).getDocs().getNumDocs());
        System.out.println(ADR_INDEX+" num docs : "+indResponse.getIndex(ADR_INDEX).getDocs().getNumDocs());
        System.out.println(POIZON_INDEX+" num docs : "+indResponse.getIndex(POIZON_INDEX).getDocs().getNumDocs());
        
        ////////////
        /// VOIE
        // voie avec uniquement commune
//        searchExactAdresse("RUE DE LA FRANCE PARIS","RUE DE LA FRANCE","75005 PARIS"); // match
//        
//        // voie avec uniquement le code département
//        searchExactAdresse("RUE DE LA FRANCE 75","RUE DE LA FRANCE","75005 PARIS"); // match
//        
//        // voie partielle avec uniquement le code département
//        searchExactAdresse("FRANCE 75","RUE DE LA FRANCE","75005 PARIS"); // match
//        
//        // voie partielle avec uniquement le code postal
//        searchExactAdresse("FRANCE 75005","RUE DE LA FRANCE","75005 PARIS"); // match
//        
//        ////////////
//        /// ADRESSES
//        
//        // adresse avec uniquement commune
//        searchExactAdresse("59 BOULEVARD DE LA FRANCE HOPITAL","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
//        
//        // adresse avec uniquement code département
//        searchExactAdresse("59 BOULEVARD DE LA FRANCE 02","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
//        
//        // adresse avec uniquement code postal
//        searchExactAdresse("59 BOULEVARD DE LA FRANCE 02000","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
//        
//        // adresse avec uniquement code insee
//        searchExactAdresse("59 BOULEVARD DE LA FRANCE 02001","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
//        
        // adresse sans son numéro d'adresse => fail
        searchExactAdresse("BOULEVARD DE LA FRANCE 02001","59 BOULEVARD DE LA FRANCE","02000 HOPITAL",1); // no match with adress, only with country
        
        // adresse avec code postal intercalé, mais FRANCE correspond au pays
        searchExactAdresse("59 BOULEVARD DE LA 02001 FRANCE","59 BOULEVARD DE LA FRANCE","02000 HOPITAL"); // match
        
        // adresse avec code postal intercalé => fail
        searchExactAdresse("59 02001 BOULEVARD DE LA FRANCE","59 BOULEVARD DE LA FRANCE","02000 HOPITAL",false); // no match with adress, only with country
    }

    @Override
    QueryBuilder getQueryBuilder(String voie)
    {
        JDONREFv4QueryBuilder qb = new JDONREFv4QueryBuilder(voie);
        qb.maxSize(100);
        qb.mode(JDONREFv4Query.BULK);
        
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