package ml.ml.processing;

import java.util.Collection;

import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.ProcessingException;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;

/**
 * Fix values case if needed.
 */
public class ToUpperOrLowerCasePostProcessor implements Processor<IeDocument> {

    private final String fieldName;
    private boolean isUpper;

    public ToUpperOrLowerCasePostProcessor(String fieldName, boolean isUpper) {
        this.isUpper = isUpper;
        this.fieldName = fieldName;
    }

    @Override
    public void process(IeDocument document) throws ProcessingException {
        Collection<Field> fields = document.findFields(fieldName);
        for (Field field : fields) {
            String value = field.getValue();
            field.setValue(isUpper ? value.toUpperCase() : value.toLowerCase());
        }
    }
}