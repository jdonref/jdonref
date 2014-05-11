package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3.JDONREFv3ESPlugin;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest.ClusterScope;
import org.elasticsearch.test.ElasticsearchIntegrationTest.Scope;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

/**
 *
 * @author Julien
 */
@ClusterScope(scope=Scope.SUITE, numNodes=1)
public class JDONREFv3IntegrationTests extends ElasticsearchIntegrationTest
{
    final static String INDEX_NAME = "test";
    final static String DOC_TYPE_NAME = "test";
    
    public void publicRefresh()
    {
        super.refresh();
    }
    
/*    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
           .put("plugin.types", JDONREFv3ESPlugin.class.getName())
           .put(super.nodeSettings(nodeOrdinal)).build();
    }*/
    
    public void publicIndex(String type,String id, XContentBuilder data)
    {
        super.index(INDEX_NAME, type, id, data);
    }
    
    public Settings indexSettings() {
        return settingsBuilder()
                .put("index.number_of_replicas", 0)
                .put("index.number_of_shards", 5)
                .put("index.image.use_thread_pool", this.randomBoolean())
            .build();
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
}