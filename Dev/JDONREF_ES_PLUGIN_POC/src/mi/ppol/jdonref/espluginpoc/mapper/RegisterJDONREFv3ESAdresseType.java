package mi.ppol.jdonref.espluginpoc.mapper;

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
public class RegisterJDONREFv3ESAdresseType extends AbstractIndexComponent
{
    @Inject
    public RegisterJDONREFv3ESAdresseType(Index index, @IndexSettings Settings indexSettings,MapperService mapperService) {
        super(index, indexSettings);
        mapperService.documentMapperParser().putTypeParser("adresse", new JDONREFv3AdresseTypeMapper.TypeParser());
    }
}