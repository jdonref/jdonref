package org.elasticsearch.plugin.analysis.jdonrefv4;

import org.elasticsearch.index.mapper.jdonrefv4.RegisterJDONREFv4AdresseType;
import org.elasticsearch.common.inject.AbstractModule;

/**
 *
 * @author Julien
 */
public class JDONREFv4Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(RegisterJDONREFv4AdresseType.class).asEagerSingleton();
    }
}
