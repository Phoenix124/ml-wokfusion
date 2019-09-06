package ml.ml.processing;

import com.workfusion.nlp.uima.util.DateNormalizer;
import com.workfusion.vds.sdk.api.nlp.model.Field;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;
import ml.ml.config.Fields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Try to normalized specific custom date format to MM/dd/yyyy first.
 * Then clear out all forbidden symbols.
 * Then use OOTB date normalizer.
 * Finally remove all dates that starts with '00' in year.
 */
public class DatePostProcessing implements Processor<IeDocument> {


    private final String fieldName;
    private DateNormalizer dateNormalizer = new DateNormalizer(Fields.DATE_FORMAT);
    private String[] forbiddenSymlbols = new String[]{"*"};

    public DatePostProcessing(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void process(IeDocument document) {
        Optional<Field> fieldOptional = document.findField(this.fieldName);
        if (fieldOptional.isPresent()) {

            Field field = fieldOptional.get();
            String value = field.getValue();
            value = value.replaceAll("0ct","October");

            String customPattern = "(JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER|" +
                    "January|February|March|April|May|June|July|August|September|October|November|December)" +
                    " [0-9]{1,2}(.|\\,) ?[0-9]{4}";
            Pattern pattern = Pattern.compile(customPattern);
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                value = CustomProcess(value);
            }

            for (String forbiddenSymbol : forbiddenSymlbols) {
                while (value.contains(forbiddenSymbol)) {
                    value = value.replace(forbiddenSymbol, "");
                }
            }

            value = dateNormalizer.apply(value);

            if (value != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(Fields.DATE_FORMAT);
                try {
                    Date parsedDate = sdf.parse(value);

                    SimpleDateFormat sdfYear = new SimpleDateFormat("YYYY");
                    if (sdfYear.format(parsedDate).startsWith("00")) {
                        document.remove(field);
                        return;
                    }

                    field.setValue(value);
                } catch (ParseException e) {
                    document.remove(field);
                }
            }
        }
    }

    private String CustomProcess(String value) {
        return value.replaceAll("JANUARY|January", "01")
                .replaceAll("FEBRUARY|February", "02")
                .replaceAll("MARCH|March", "03")
                .replaceAll("APRIL|April", "04")
                .replaceAll("MAY|May", "05")
                .replaceAll("JUNE|June", "06")
                .replaceAll("JULY|July", "07")
                .replaceAll("AUGUST|August", "08")
                .replaceAll("SEPTEMBER|September", "09")
                .replaceAll("OCTOBER|October", "10")
                .replaceAll("NOVEMBER|November", "11")
                .replaceAll("DECEMBER|December", "12")
                .replaceAll("(, |\\. | |,|\\.)", "/");
    }
}
