package ml.ml.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Create a feature if data preceded with currency sign.
 *
 * @param <T>
 */
public class IsPrecededWithCurrencySignFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "contains_currency_sign";
    private List<String> currencies = new ArrayList<>();
    private String content;

    /**
     * Code is running one time while feature extractor initialization.
     */
    @OnInit
    public void onInit() {
        currencies.add("${symbol_dollar}");
        currencies.add("€");
        currencies.add("£");
    }

    @OnDocumentStart
    public void onDocumentStart(Document document){
        content = document.getText();
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {

        if (element.getBegin() > 2) {
            String textBeforeElement = content.substring(element.getBegin() - 2, element.getBegin());
            for (String currency : currencies) {
                if (textBeforeElement.contains(currency)) {
                    return Collections.singleton(new Feature(FEATURE_NAME, 1.0));
                }
            }
        }
        return Collections.emptySet();
    }
}
