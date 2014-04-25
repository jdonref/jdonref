package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3;

/**
 *
 * @author Julien
 */

import org.elasticsearch.plugins.AbstractPlugin;

import mi.ppol.jdonref.espluginpoc.index.analysis.JDONREFv3ComboSynonymFilterFactory;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryParser;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.indices.query.IndicesQueriesModule;

/**
 * 
 */
public class JDONREFv3ESPlugin extends AbstractPlugin
{
    @Override
    public String name() {
        return "elasticsearch-jdonrefv3es";
    }

    @Override
    public String description() {
        return "JDONREFv3 query support";
    }
    
    public void onModule(IndicesQueriesModule module)
    {
        module.addQuery(new JDONREFv3QueryParser());
    }
    /*
     * Not fully operational
    public void onModule(AnalysisModule module)
    {
        module.addTokenFilter("combosynonym", JDONREFv3ComboSynonymFilterFactory.class);
    }*/
}