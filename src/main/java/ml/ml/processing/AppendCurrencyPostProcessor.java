package ml.ml.processing;

import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;

import java.util.Collection;

/**
 * Appends to a value provided value: e.g. 'USD'.
 */
public class AppendCurrencyPostProcessor implements Processor<IeDocument> {

    private final String fieldName;
    private final String currency;

    public AppendCurrencyPostProcessor(String fieldName, String currency) {
        this.fieldName = fieldName;
        this.currency = currency;
    }

    @Override
    public void process(IeDocument document) {
        Collection<Field> fields = document.findFields(this.fieldName);
        for (Field f : fields) {
            String value = f.getValue();
            StringBuilder sb = new StringBuilder();
            f.setValue(sb.append(value).append(" ").append(this.currency).toString());
        }
    }
}
