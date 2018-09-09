package org.fao.geonet.api.registries.vocabularies;

import org.fao.geonet.kernel.search.keyword.KeywordRelation;

import java.beans.PropertyEditorSupport;

/**
 * Custom Spring PropertyEditor for {@link org.fao.geonet.kernel.search.keyword.KeywordRelation}.
 *
 * @author Jose García
 */
public class KeywordRelationConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        setValue(KeywordRelation.fromValue(text));
    }
}
