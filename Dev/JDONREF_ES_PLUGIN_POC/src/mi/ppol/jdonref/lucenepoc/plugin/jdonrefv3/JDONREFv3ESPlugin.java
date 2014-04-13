package mi.ppol.jdonref.lucenepoc.plugin.jdonrefv3;

/**
 *
 * @author Julien
 */

import java.util.ArrayList;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.Collection;

/**
 * 
 */
public class JDONREFv3ESPlugin extends AbstractPlugin
{
    @Override
    public String name() {
        return "analysis-jdonrefv3es";
    }

    @Override
    public String description() {
        return "JDONREFv3 analysis support";
    }
}