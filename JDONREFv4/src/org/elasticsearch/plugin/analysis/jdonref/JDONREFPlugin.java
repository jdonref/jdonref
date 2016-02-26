package org.elasticsearch.plugin.analysis.jdonref;

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
import static org.elasticsearch.common.collect.Lists.newArrayList;

/**
 * 
 */
public class JDONREFPlugin extends AbstractPlugin
{
    @Override
    public String name() {
        return "elasticsearch-jdonref";
    }

    @Override
    public String description() {
        return "free web service for validation and geocoding of French postal address";
    }
    
    @Override
    public Collection<Class<? extends Module>> indexModules() {
        Collection<Class<? extends Module>> modules = newArrayList();
        modules.add(JDONREFModule.class);
        return modules;
    }
    
    public void onModule(AnalysisModule module)
    {
        module.addTokenFilter("payloadedgengram",EdgeNGramWithPayloadsFilterFactory.class);
        module.addTokenFilter("payloadsynonym",SynonymWithPayloadsTokenFilterFactory.class);
        module.addTokenFilter("unsplit",UnsplitFilterFactory.class);
        module.addTokenFilter("tokencountpayloads",TokenCountPayloadsFilterFactory.class);
        module.addTokenFilter("integertypeaspayload",IntegerTypeAsPayloadFilterFactory.class);
        module.addTokenFilter("count_token_as_value",CountTokenAsValueFilterFactory.class);
    }
    
    public void onModule(IndicesQueriesModule module)
    {
        module.addQuery(JDONREFv4QueryParser.class);
        module.addFilter(MultiPayloadSpanTermFilterParser.class);
        module.addFilter(PayloadCheckerSpanFilterParser.class);
    }
}