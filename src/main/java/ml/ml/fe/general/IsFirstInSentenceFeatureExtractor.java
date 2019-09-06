package ml.ml.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Sentence;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Create a feature if data is located in the beginning of the sentence.
 *
 * @param <T>
 */
public class IsFirstInSentenceFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "first_in_sentence_feature";
    private Set<Integer> first;

    /**
     * Code is running on document load one time per document.
     *
     * @param document
     */
    @OnDocumentStart
    public void documentStart(Document document) {
        first = document.findAll(Sentence.class)
                .stream()
                .map(Element::getBegin)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {

        if (first.contains(element.getBegin())) {
            return Collections.singleton(new Feature(FEATURE_NAME,1.0));
        }
        return Collections.emptySet();
    }

    /**
     * Code is running on document unload one time per document.
     */
    @OnDocumentComplete
    public void documentComplete() {
        first.clear();
    }

}
