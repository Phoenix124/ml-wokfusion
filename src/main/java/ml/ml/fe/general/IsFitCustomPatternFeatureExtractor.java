package ml.ml.fe.general;

import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create a feature if data fits provided pattern.
 *
 * @param <T>
 */
public class IsFitCustomPatternFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private String featureName;
    private Pattern pattern;

    public IsFitCustomPatternFeatureExtractor(String feature_name, String pattern) {
        this.pattern = Pattern.compile(pattern);
        featureName = feature_name;
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String text = element.getText().trim();
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Collections.singleton(new Feature(featureName + "_match_pattern", 1.0));
        }
        return Collections.emptySet();
    }
}
