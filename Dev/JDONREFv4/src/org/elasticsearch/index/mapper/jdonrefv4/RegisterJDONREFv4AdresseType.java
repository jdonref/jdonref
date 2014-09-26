package org.elasticsearch.index.mapper.jdonrefv4;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.AbstractIndexComponent;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.settings.IndexSettings;

/**
 *
 * @author Julien
 */
public class RegisterJDONREFv4AdresseType extends AbstractIndexComponent
{
    @Inject
    public RegisterJDONREFv4AdresseType(Index index, @IndexSettings Settings indexSettings,MapperService mapperService) {
        super(index, indexSettings);
        mapperService.documentMapperParser().putTypeParser("adresse", new JDONREFv4AdresseTypeMapper.TypeParser());
    }
}