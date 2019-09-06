package ml.ml.annotator;

import com.workfusion.vds.sdk.api.nlp.annotator.Annotator;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.NamedEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example of custom NER annotator used for specific goal of CEO lookup in the documents.
 * Best practice is to annotate as much as possible in the document and the use simple feature extractors for code speed up.
 */
public class CeoNerAnnotator implements Annotator<Document> {

    private static final String CEO_PATTERN = "(?si)${symbol_escape}${symbol_escape}b((Chief${symbol_escape}${symbol_escape}s+.{0,10}Executive${symbol_escape}${symbol_escape}s+.{0,10}Officer)|(C.{0,2}E.{0,2}O))${symbol_escape}${symbol_escape}b";
    private static final String NER_TYPE = "CEO";

    @Override
    public void process(Document document) {
        Pattern pattern = Pattern.compile(CEO_PATTERN);
        Matcher matcher = pattern.matcher(document.getText());
        while (matcher.find()) {
            document.add(NamedEntity.descriptor()
                    .setType(NER_TYPE)
                    .setBegin(matcher.start())
                    .setEnd(matcher.end()));
        }
    }
}
