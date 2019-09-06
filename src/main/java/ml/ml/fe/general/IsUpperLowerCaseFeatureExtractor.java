package ml.ml.fe.general;

import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Create a feature if is all upper${symbol_escape}lowercase.
 *
 * @param <T>
 */
public class IsUpperLowerCaseFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final static String FEATURE_NAME_UPPER = "is_upper_case_feature";
    private final static String FEATURE_NAME_LOWER = "is_lower_case_feature";
    private final boolean IS_UPPER_CASE;

    public IsUpperLowerCaseFeatureExtractor(boolean isUpperCase) {
        IS_UPPER_CASE = isUpperCase;
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        ArrayList<Feature> featureList = new ArrayList<>();
        String text = element.getText();
        if (text.toUpperCase().equals(text)) {
            featureList.add(new Feature(IS_UPPER_CASE ? FEATURE_NAME_UPPER : FEATURE_NAME_LOWER, 1.0));
        }

        return featureList;
    }
}
