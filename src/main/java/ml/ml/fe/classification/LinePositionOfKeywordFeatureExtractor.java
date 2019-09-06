package ml.ml.fe.classification;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a feature if specific data was found in a line of the document.
 * Value of the feature depends on the line number of the document.
 *
 * @param <T>
 */
public class LinePositionOfKeywordFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final Collection<String> keywords;
    private static final String FEATURE_NAME = "line_position";
    private List<String> linesText;
    private Collection<Feature> features;
    private HashMap<Integer, Feature> featuresMap;

    public LinePositionOfKeywordFeatureExtractor(Collection<String> keywords) {
        this.keywords = keywords
                .stream()
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toList());
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        linesText = document.findAll(Line.class)
                .stream()
                .map(s -> s.getText().trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        for (String keyword : keywords) {
            for (int i = 0; i < linesText.size(); i++) {
                int firstIndex = linesText.get(i).indexOf(keyword);

                if (firstIndex >= 0) {
                    featuresMap.put(firstIndex, new Feature(FEATURE_NAME + "_" + keyword,
                            ((double) i + 1) / linesText.size()));
                }
            }
        }
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        int begin = element.getBegin();
        if (featuresMap.keySet().contains(begin)) {
            return Collections.singleton(featuresMap.get(begin));
        }
        return Collections.emptySet();
    }

    @OnDocumentComplete
    public void documentComplete() {
        linesText.clear();
        features.clear();
    }
}
