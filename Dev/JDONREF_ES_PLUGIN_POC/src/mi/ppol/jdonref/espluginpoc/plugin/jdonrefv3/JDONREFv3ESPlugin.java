package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3;

/**
 *
 * @author Julien
 */

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.Collection;
import mi.ppol.jdonref.espluginpoc.index.query.JDONREFv3QueryParser;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.index.query.IndexQueryParserModule;
import org.elasticsearch.index.query.QueryParser;
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
    
 /*   @Override
    public Collection<Class<? extends Module>> modules() {
        Collection<Class<? extends Module>> modules = Lists.newArrayList();
        modules.add(JDONREFv3ESModule.class);
        return modules;
    }*/
    
    public void onModule(IndicesQueriesModule module)
    {
        module.addQuery(new JDONREFv3QueryParser());
    }
}