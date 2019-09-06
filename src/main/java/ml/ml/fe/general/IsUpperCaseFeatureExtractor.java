
package ml.ml.fe.general;

import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Create a feature if is all upper${symbol_escape}lowercase.
 *
 * @param <T>
 */
public class IsUpperCaseFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME_UPPER = "is_upper_case_feature";

    @Override
    public Collection<Feature> extract(Document document, T element) {

        String elementText = element.getText();
        if (elementText.toUpperCase().equals(elementText)) {
           return Collections.singleton(new Feature(FEATURE_NAME_UPPER, 1.0));
        }
        return Collections.emptySet();
    }
}
