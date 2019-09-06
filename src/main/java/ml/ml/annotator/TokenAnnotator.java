package ml.ml.annotator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.workfusion.vds.sdk.api.nlp.annotator.Annotator;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Line;
import com.workfusion.vds.sdk.api.nlp.model.Token;

public class TokenAnnotator implements Annotator<Document> {

    private final String regexp;

    public TokenAnnotator(String matcherRegex) {
        regexp = matcherRegex;
    }

    @Override
    public void process(Document document) {

        //if a word overlaps a line, substring the word to the beginning of the line
        //same as before if a word overlaps the end of a line

        ArrayList<Line> allLines = new ArrayList<Line>(document.findAll(Line.class));
        int lineIndex = 0;
        Line line = allLines.size() > 0 ? allLines.get(0) : null;

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(document.getText());
        while (matcher.find()) {
            boolean wordMatchedByLine = false;
            if (line != null) {
                while ((line.getEnd() < matcher.start()) && lineIndex < (allLines.size() - 1)) {
                    lineIndex++;
                    line = allLines.get(lineIndex);
                }
                //if a word overlaps the beginning of a line line, substring the word to the beginning of the line
                if (matcher.start() < line.getBegin() && (matcher.end() >= line.getBegin()) && (matcher.end() <= line.getEnd())) {
                    document.add(Token.descriptor()
                            .setBegin(matcher.start())
                            .setEnd(line.getBegin()));
                    document.add(Token.descriptor()
                            .setBegin(line.getBegin())
                            .setEnd(matcher.end()));
                    wordMatchedByLine = true;
                }
                //if a word overlaps the end of a line
                else if ((matcher.start() >= line.getBegin()) && (matcher.start() <= line.getEnd()) && matcher.end() > line.getEnd()) {
                    document.add(Token.descriptor()
                            .setBegin(matcher.start())
                            .setEnd(line.getEnd()));
                    document.add(Token.descriptor()
                            .setBegin(line.getEnd())
                            .setEnd(matcher.end()));
                    wordMatchedByLine = true;
                }
            }
            if (!wordMatchedByLine) {
                document.add(Token.descriptor()
                        .setBegin(matcher.start())
                        .setEnd(matcher.end()));
            }
        }
    }

}
