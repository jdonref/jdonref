package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryBuilder;
import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

/**
 *
 * @author Julien
 */
@ClusterScope(scope=Scope.SUITE, numNodes=1)
public class JDONREFv3SynonymeTests extends ElasticsearchIntegrationTest
{
    final static String INDEX_NAME = "test";
    final static String DOC_TYPE_NAME = "test";
    
    public void publicRefresh()
    {
        super.refresh();
    }
    
    public void publicIndex(String type,String id, XContentBuilder data)
    {
        super.index(INDEX_NAME, type, id, data);
    }
    
    public Settings indexSettings() {
        return settingsBuilder()
                .put("index.number_of_replicas", 0)
                .put("index.number_of_shards", 1)
                .put("index.image.use_thread_pool", this.randomBoolean())
            .build();
    }
    
    @Before
    public void createEmptyIndex() throws Exception {
        logger.info("creating index [{}]", INDEX_NAME);
        wipeIndices(INDEX_NAME);
        createIndex(INDEX_NAME);
        ensureGreen();
    }
    
    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
           .loadFromSource("./test/resources/index/TestSynonyme-settings.yml")
           .put(super.nodeSettings(nodeOrdinal)).build();
    }
    
    public String readFile(String file) throws FileNotFoundException, IOException
    {
        BufferedReader reader = (new BufferedReader(new FileReader(new File(file))));
        String line = reader.readLine();
        String res = "";
        while(line!=null)
        {
            res += line+System.getProperty("line.separator");
            line = reader.readLine();
        }
        reader.close();
        return res;
    }
    
    void importMapping() throws FileNotFoundException, IOException
    {
        String[] mappings = new String[]{"pays","departement","commune","voie","troncon","adresse"};
        
        for(int i=0;i<mappings.length;i++)
        {
            String test = mappings[i];
            String mapping = readFile("./src/resources/mapping/mapping-"+test+".json");
            PutMappingResponse pmr = client().admin().indices().putMapping(new PutMappingRequest(INDEX_NAME).type(test).source(mapping)).actionGet();
        }
        publicRefresh();
    }
    
    void percolate(String voie) throws IOException
    {
        System.out.println("Percolate : "+voie);
        
        //Build a document to check against the percolator
        XContentBuilder docBuilder = XContentFactory.jsonBuilder().startObject();
        docBuilder.field("doc").startObject(); //This is needed to designate the document
        docBuilder.field("content", voie);
        docBuilder.endObject(); //End of the doc field
        docBuilder.endObject(); //End of the JSON root object

        //Percolate
        PercolateResponse response = client().preparePercolate()
                .setIndices(INDEX_NAME)
                .setDocumentType("doc")
                .setSource(docBuilder).execute().actionGet();

        if (response.getCount()==0)
            System.out.println("Nothing");
        
        //Iterate over the results
        for (PercolateResponse.Match match : response) {
            //Handle the result which is the name of
            //the query in the percolator
            Map<String,HighlightField> map = match.getHighlightFields();
            Collection<HighlightField> fields = map.values();
            Iterator<HighlightField> iterator = fields.iterator();
            while(iterator.hasNext())
            {
                Text[] fragments = iterator.next().getFragments();
                for(int j=0;j<fragments.length;j++)
                    System.out.println(fragments[j].toString());
            }
        }
    }
    
    void searchExactAdresse(String voie,String assertion)
    {
        System.out.println("Searching "+voie);
        QueryBuilder qb = (QueryBuilder) new JDONREFv3QueryBuilder(voie);
        //QueryBuilder qb = (QueryBuilder)QueryBuilders.termQuery("ligne7",pays);
        //QueryBuilder qb = (QueryBuilder)QueryBuilders.matchQuery("ligne7",pays);
        SearchResponse search = client().prepareSearch().setQuery(qb).execute().actionGet();
        SearchHit[] hits = search.getHits().getHits();
        
        if (hits.length==0)
            System.out.println("No results");
        assertTrue(hits.length>0);
        
        System.out.println(hits.length+" hit(s)");
        for(int i=0;i<hits.length;i++)
        {
            SearchHit hit = hits[i];
            System.out.println("hit "+i+" "+hit.getSourceAsString());
            System.out.println("score : "+hit.getScore());
            Explanation ex = hits[i].explanation();
            if (ex!=null)
            {
                System.out.println("explanation :"+ex.getDescription());
                for(int j=0;j<ex.getDetails().length;j++)
                    System.out.println(ex.getDetails()[j].getDescription());
            }
        }
        assertEquals(assertion,hits[0].getSource().get("fullName"));
    }
    
    void indexPercolator(String champ,String id,String value) throws IOException
    {
        QueryBuilder qb = QueryBuilders.termQuery(champ,value);
        
        client().prepareIndex(INDEX_NAME,".percolator",id)
                .setSource(XContentFactory.jsonBuilder()
                   .startObject()
                      .field("query",qb)
                   .endObject())
                .setRefresh(true)
                .execute().actionGet();
    }
    
    void indexCommune(XContentBuilder Xcommune,String id,String commune) throws IOException
    {
        indexPercolator("commune",id,commune);
        
        publicIndex("commune",commune,Xcommune);
    }
    
    void indexPays(XContentBuilder Xpays,String id,String pays) throws IOException
    {
        indexPercolator("pays",id,pays);
        
        publicIndex("pays",id,Xpays);
    }
    
    void index() throws IOException, InterruptedException, ExecutionException
    {
//        BulkRequest bulk = new BulkRequest();
//        bulk.readFrom(new InputStreamStreamInput(new FileInputStream("./test/resources/bulk/requests.bulk")));
//        this.client().bulk(bulk);
        
        indexCommune(XContentFactory.jsonBuilder().startObject()
                .field("codepostal","75000")
                .field("codeinsee","75056")
                .field("commune","PARIS")
                .field("ligne6","PARIS")
                .field("codepays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","PARIS FRANCE")
                .field("fullName_without_numbers","PARIS FRANCE")
                .field("numero","0")
                .field("codedepartement","75")
                .endObject(),"75056","PARIS");
        indexPays(XContentFactory.jsonBuilder().startObject()
                .field("codepays","FR")
                .field("ligne7","FRANCE")
                .field("fullName","FRANCE")
                .field("fullName_without_numbers","FRANCE")
                .field("numero","0")
                .endObject(),"FR","FRANCE");
        indexPays(XContentFactory.jsonBuilder().startObject()
                .field("codepays","DE")
                .field("ligne7","ALLEMAGNE")
                .field("fullName","ALLEMAGNE")
                .field("fullName_without_numbers","ALLEMAGNE")
                .field("numero","0")
                .endObject(),"DE","ALLEMAGNE");
        publicIndex("voie","1",XContentFactory.jsonBuilder().startObject()
                .field("codepays","FR")
                .field("ligne4","BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("fullName","BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("fullName_without_numbers","BOULEVARD DE L HOPITAL PARIS FRANCE")
                .field("numero","0")
                .field("codedepartement","75")
                .field("codepostal","75005")
                .field("codeinsee","75105")
                .endObject());
        publicIndex("adresse","1",XContentFactory.jsonBuilder().startObject()
                .field("codepays","FR")
                .field("ligne4","24 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("fullName","24 BOULEVARD DE L HOPITAL 75013 PARIS FRANCE")
                .field("fullName_without_numbers","BOULEVARD DE L HOPITAL PARIS FRANCE")
                .field("numero","24")
                .field("codedepartement","75")
                .field("codepostal","75013")
                .field("codeinsee","75013")
                .endObject());
        publicIndex("adresse","2",XContentFactory.jsonBuilder().startObject()
                .field("codepays","FR")
                .field("ligne4","24 RUE DE LA FRANCE")
                .field("ligne6","75013 PARIS")
                .field("ligne7","FRANCE")
                .field("fullName","24 RUE DE LA FRANCE 75013 PARIS FRANCE")
                .field("fullName_without_numbers","RUE DE LA FRANCE PARIS FRANCE")
                .field("numero","24")
                .field("codedepartement","75")
                .field("codepostal","75005")
                .field("codeinsee","75113")
                .endObject());
        publicIndex("adresse","3",XContentFactory.jsonBuilder().startObject()
                .field("codepays","FR")
                .field("ligne4","75 BOULEVARD DE L HOPITAL")
                .field("ligne6","75005 PARIS")
                .field("ligne7","FRANCE")
                .field("fullName","75 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE")
                .field("fullName_without_numbers","BOULEVARD DE L HOPITAL PARIS FRANCE")
                .field("numero","75")
                .field("codedepartement","75")
                .field("codepostal","75005")
                .field("codeinsee","75105")
                .endObject());
        publicIndex("departement","75",XContentFactory.jsonBuilder().startObject()
                .field("ligne6","75")
                .field("ligne7","FRANCE")
                .field("fullName","75 FRANCE")
                .field("fullName_without_numbers","FRANCE")
                .field("numero","0")
                .field("codedepartement","75")
                .endObject());
        
        for(int i=0;i<10;i++)
        {
            String randomCode = randomRealisticUnicodeOfLength(2);
            String randomLigne7 = randomRealisticUnicodeOfLength(10);
            publicIndex("pays",randomCode,XContentFactory.jsonBuilder().startObject()
                  .field("codepays",randomCode)
                  .field("ligne7",randomLigne7)
                  .endObject());
        }
    }
    
    @Test
    public void testSearch() throws IOException, InterruptedException, ExecutionException
    {    
        importMapping();
        index();
        
        ensureGreen();
        Thread.sleep(10000); // wait for indexation !
        
        GetResponse response = client().prepareGet(INDEX_NAME,"pays","FR").execute().get();
        System.out.println(response.getSourceAsString());
        // need assert
        
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
        
        searchExactAdresse("24 BD HOPITAL","24 BOULEVARD DE L HOPITAL 75013 PARIS FRANCE");
        searchExactAdresse("24 BOULEVARD HOPITAL","24 BOULEVARD DE L HOPITAL 75013 PARIS FRANCE");
        
        searchExactAdresse("BOULEVARD HOPITAL","BOULEVARD DE L HOPITAL 75005 PARIS FRANCE");
        searchExactAdresse("BD HOPITAL","BOULEVARD DE L HOPITAL 75005 PARIS FRANCE");
        
        searchExactAdresse("75 BD HOPITAL PARIS","75 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE");
        searchExactAdresse("75 BOULEVARD HOPITAL PARIS","75 BOULEVARD DE L HOPITAL 75005 PARIS FRANCE");
        
        searchExactAdresse("BD HOPITAL 75 PARIS","BOULEVARD DE L HOPITAL 75005 PARIS FRANCE");
        searchExactAdresse("BOULEVARD HOPITAL 75 PARIS","BOULEVARD DE L HOPITAL 75005 PARIS FRANCE");
    }
    
    @Test
    public void testPercolate() throws IOException, InterruptedException, ExecutionException
    {
        importMapping();
        index();
        
        ensureGreen();
        Thread.sleep(10000); // wait for indexation !
        
        GetResponse response = client().prepareGet(INDEX_NAME,"pays","FR").execute().get();
        System.out.println(response.getSourceAsString());
        // need assert
        
        IndicesStatusResponse indResponse = client().admin().indices().prepareStatus().execute().actionGet();
        System.out.println(INDEX_NAME+" num docs : "+indResponse.getIndex(INDEX_NAME).getDocs().getNumDocs());
        
        //percolate("BD HOPITAL PARIS"); // percolator does not handle tokenfilters !!!
    }
}