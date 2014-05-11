package mi.ppol.jdonref.espluginpoc.plugin.jdonrefv3;

import mi.ppol.jdonref.espluginpoc.mapper.RegisterJDONREFv3ESAdresseType;
import org.elasticsearch.common.inject.AbstractModule;

/**
 *
 * @author Julien
 */
public class JDONREFv3ESModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RegisterJDONREFv3ESAdresseType.class).asEagerSingleton();
    }

}
