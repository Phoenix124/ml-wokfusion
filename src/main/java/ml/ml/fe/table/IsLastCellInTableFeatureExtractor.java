package ml.ml.fe.table;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Cell;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Table;
import com.workfusion.vds.sdk.api.nlp.model.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Create a feature if data is located in lower right cell of the table.
 *
 * @param <T>
 */

public class IsLastCellInTableFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final List<Cell> lastCells = new ArrayList<>();
    private Map<Token, Collection<Cell>> tokensCoveredWithCells;

    @OnDocumentStart
    public void OnDocumentStart(Document document) {
        Collection<Table> coveringTable = document.findAll(Table.class);
        for (Table table : coveringTable) {
            List<Cell> coveredCells = document.findCovered(Cell.class, table);
            Cell cell = null;
            for (Cell coveredCell : coveredCells) {
                if (cell == null) {
                    cell = coveredCell;
                } else {
                    int coveredCellColumnIndex = coveredCell.getColumnIndex();
                    int cellColumnIndex = cell.getColumnIndex();
                    if (coveredCellColumnIndex > cellColumnIndex) {
                        cell = coveredCell;
                    } else {
                        int coveredCellRowIndex = coveredCell.getRowIndex();
                        int cellRowIndex = cell.getRowIndex();
                        if (coveredCellColumnIndex == cellColumnIndex && coveredCellRowIndex > cellRowIndex) {
                            cell = coveredCell;
                        }
                    }
                }
            }
            lastCells.add(cell);
        }

        tokensCoveredWithCells = document.findAllCovering(Token.class, Cell.class);
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {

        Collection<Cell> covering = tokensCoveredWithCells.get(element);
        if (covering != null) {
            for (Cell cell : covering) {
                if (lastCells.contains(cell)) {
                    return Collections.singleton(new Feature("last_cell_in_table", 1.0));
                }
            }
        }
        return Collections.emptySet();
    }
}
