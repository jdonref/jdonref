package org.elasticsearch.plugin.analysis.jdonrefv4;

/**
 *
 * @author Julien
 */
import org.elasticsearch.plugin.analysis.TokenCountPayloadsFilterFactory;
import org.elasticsearch.index.query.PayloadVersusTypeSpanQueryParser;
import org.elasticsearch.index.query.MultiPayloadSpanTermQueryParser;
import org.elasticsearch.index.query.GroupedPayloadSpanQueryParser;
import org.elasticsearch.plugin.analysis.UnsplitFilterFactory;
import org.elasticsearch.plugin.analysis.EdgeNGramWithPayloadsFilterFactory;
import java.util.Collection;
import org.elasticsearch.plugins.AbstractPlugin;

import org.elasticsearch.index.query.jdonrefv4.JDONREFv4QueryParser;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.indices.query.IndicesQueriesModule;

import static org.elasticsearch.common.collect.Lists.newArrayList;

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
        module.addTokenFilter("unsplit",UnsplitFilterFactory.class);
        module.addTokenFilter("tokencountpayloads",TokenCountPayloadsFilterFactory.class);
    }
    
    public void onModule(IndicesQueriesModule module)
    {
        module.addQuery(new JDONREFv4QueryParser());
        module.addQuery(new GroupedPayloadSpanQueryParser());
        module.addQuery(new MultiPayloadSpanTermQueryParser());
        module.addQuery(new PayloadVersusTypeSpanQueryParser());
    }
}