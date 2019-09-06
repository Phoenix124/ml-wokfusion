package ml.ml.fe.classification;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Create a feature if specific data was found the the document.
 * Value of the feature depends on the absolute position of data in the document text.
 *
 * @param <T>
 */
public class PositionOfKeywordsFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private Collection<String> keywords;
    private static final String FEATURE_NAME = "document_position";
    private String content;
    private HashMap<String, Feature> featuresMap;

    public PositionOfKeywordsFeatureExtractor(Collection<String> keywords) {
        this.keywords = keywords
                .stream()
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        content = document.getText().toLowerCase();
        featuresMap = new HashMap<>();
        for (String keyword : keywords) {
            int firstIndex = content.indexOf(keyword);
            if (firstIndex >= 0) {
                featuresMap.put(keyword, new Feature(FEATURE_NAME + "_" + keyword,
                        ((double) firstIndex) / content.length()));
            }
        }
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String elementText = element.getText();
        if(featuresMap.keySet().contains(elementText)){
            return Collections.singleton(featuresMap.get(elementText));
        }
        return Collections.emptySet();
    }
}
