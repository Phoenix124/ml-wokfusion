package ml.ml.fe.table;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Cell;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create a feature if data is in a cell of a table.
 *
 * @param <T>
 */
public class IsCoveredWithCellFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "covered_with_cell";
    private List<String> cellsText;
    private Map<Token, Collection<Cell>> tokensCoveredWithCells;

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        tokensCoveredWithCells = document.findAllCovering(Token.class, Cell.class);
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {

        if (tokensCoveredWithCells.get(element) != null) {
            return Collections.singleton(new Feature(FEATURE_NAME, 1.0));
        }
        return Collections.emptySet();
    }
}
