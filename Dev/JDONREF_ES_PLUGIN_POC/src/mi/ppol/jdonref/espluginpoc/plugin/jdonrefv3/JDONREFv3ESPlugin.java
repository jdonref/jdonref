package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3;

/**
 *
 * @author Julien
 */
import java.util.Collection;
import mi.ppol.jdonref.espluginpoc.index.analysis.JDONREFv3EdgeNGramWithPayloadsFilterFactory;
import org.elasticsearch.plugins.AbstractPlugin;

import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryParser;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.indices.query.IndicesQueriesModule;

import static org.elasticsearch.common.collect.Lists.newArrayList;

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
        return "JDONREFv3 query and type support";
    }
    
    @Override
    public Collection<Class<? extends Module>> indexModules() {
        Collection<Class<? extends Module>> modules = newArrayList();
        modules.add(JDONREFv3ESModule.class);
        return modules;
    }
    
    public void onModule(AnalysisModule module)
    {
        module.addTokenFilter("jdonrefv3es_edgengram",JDONREFv3EdgeNGramWithPayloadsFilterFactory.class);
    }
    
    public void onModule(IndicesQueriesModule module)
    {
        module.addQuery(new JDONREFv3QueryParser());
    }
}