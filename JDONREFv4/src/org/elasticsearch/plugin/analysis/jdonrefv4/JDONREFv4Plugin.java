package org.elasticsearch.plugin.analysis.jdonrefv4;

/**
 *
 * @author Julien
 */
import java.util.Collection;
import static org.elasticsearch.common.collect.Lists.newArrayList;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.query.MultiPayloadSpanTermFilterParser;
import org.elasticsearch.index.query.PayloadCheckerSpanFilterParser;
import org.elasticsearch.index.query.jdonrefv4.JDONREFv4QueryParser;
import org.elasticsearch.indices.query.IndicesQueriesModule;
import org.elasticsearch.plugin.analysis.*;
import org.elasticsearch.plugins.AbstractPlugin;

/**
 * 
 */
public class JDONREFv4Plugin extends AbstractPlugin
{
    @Override
    public String name() {
        return "elasticsearch-jdonrefv4";
    }

    @Override
    public String description() {
        return "JDONREFv4 query and type support";
    }
    
    @Override
    public Collection<Class<? extends Module>> indexModules() {
        Collection<Class<? extends Module>> modules = newArrayList();
        modules.add(JDONREFv4Module.class);
        return modules;
    }
    
    public void onModule(AnalysisModule module)
    {
        module.addTokenFilter("payloadedgengram",EdgeNGramWithPayloadsFilterFactory.class);
        module.addTokenFilter("payloadsynonym",SynonymWithPayloadsTokenFilterFactory.class);
        module.addTokenFilter("unsplit",UnsplitFilterFactory.class);
        module.addTokenFilter("tokencountpayloads",TokenCountPayloadsFilterFactory.class);
        module.addTokenFilter("integertypeaspayload",IntegerTypeAsPayloadFilterFactory.class);
    }
    
    public void onModule(IndicesQueriesModule module)
    {
        module.addQuery(new JDONREFv4QueryParser());
        module.addFilter(new MultiPayloadSpanTermFilterParser());
        module.addFilter(new PayloadCheckerSpanFilterParser());
    }
}