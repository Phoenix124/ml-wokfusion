package ml.ml.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.NamedEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a feature if data is covered with particular Ner.
 *
 * @param <T>
 */
public class IsCoveredWithParticularNerFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final String nerType;
    private List<NamedEntity> filteredNamedEntities;

    public IsCoveredWithParticularNerFeatureExtractor(String nerType) {
        this.nerType = nerType;
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        Collection<NamedEntity> namedEntities = document.findAll(NamedEntity.class);
        filteredNamedEntities = namedEntities
                .stream()
                .filter(n -> n.getType().equals(nerType))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        for (NamedEntity entity : filteredNamedEntities) {
            if (entity.getBegin() <= element.getBegin() && entity.getEnd() >= element.getEnd()) {
               return Collections.singleton(new Feature(nerType + "_feature", 1.0));
            }
        }
        return Collections.emptySet();
    }
}
