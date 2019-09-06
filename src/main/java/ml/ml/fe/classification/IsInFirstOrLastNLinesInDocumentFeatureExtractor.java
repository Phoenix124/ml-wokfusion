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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a feature if specific data was found in first${symbol_escape}last N rows of the document.
 *
 * @param <T>
 */
public class IsInFirstOrLastNLinesInDocumentFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private int numberOfLines;
    private final String featureName;
    private final boolean isFirstLines;
    private List<String> linesText;

    public IsInFirstOrLastNLinesInDocumentFeatureExtractor(int numberOfLines, boolean isFirstLines) {
        this.isFirstLines = isFirstLines;
        this.numberOfLines = numberOfLines;

        if (isFirstLines) {
            featureName = "in_first_" + this.numberOfLines + "_lines";
        } else {
            featureName = "in_last_" + this.numberOfLines + "_lines";
        }
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        linesText = document.findAll(Line.class)
                .stream()
                .map(Element::getText)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String elementText = element.getText();

        if (isFirstLines) {
            int min = Math.min(numberOfLines, linesText.size());
            for (int i = 0; i < min; i++) {
                if (linesText.get(i).contains(elementText)) {
                    return Collections.singleton(new Feature(featureName, 1.0));
                }
            }
        } else {
            int linesSize = linesText.size();
            if (linesSize < numberOfLines) {
                numberOfLines = linesSize;
            }
            while (numberOfLines > 0) {
                if (linesText.get(linesSize - 1).contains(elementText)) {
                    return Collections.singleton(new Feature(featureName, 1.0));
                }
                linesSize--;
                numberOfLines--;
            }
        }
        return Collections.emptySet();
    }

    @OnDocumentComplete
    public void documentComplete() {
        linesText.clear();
    }
}
