package ml.ml.fe.table;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.cache.CacheBuilder;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Cell;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.NamedEntity;
import com.workfusion.vds.sdk.api.nlp.model.Table;
import com.workfusion.vds.sdk.api.nlp.model.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Create a feature for every value in a column${symbol_escape}row where appropriate column${symbol_escape}row header contains any of keyword list provided.
 *
 * @param <T>
 */
public class MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror<T extends Element> implements FeatureExtractor<T> {

    private String featureName;
    private Set<String> headerKeywords;
    private Map<Integer, Cell> headersMapping = new TreeMap<>();
    private boolean isUsedForColumn;
    private Map<Token, Collection<Cell>> tokensCoveredWithCells;
    private Map<Token, Collection<Table>> tokensCoveredWithTables;

    public MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractror(String featureName,
            Set<String> columnNames, boolean isUsedForColumn) {
        if (isUsedForColumn) {
            this.featureName = featureName + "_match_table_column_header";
        } else {
            this.featureName = featureName + "_match_table_row_header";
        }
        this.isUsedForColumn = isUsedForColumn;
        headerKeywords = columnNames;
    }

    @OnInit
    public void OnInit(CacheBuilder cacheBuilder) {
        cacheBuilder.covering(Token.class, Cell.class);
        cacheBuilder.covering(Token.class, Table.class);
        cacheBuilder.covering(Cell.class, Table.class);
    }

    /**
     * Code is running on document load one time per document.
     *
     * @param document
     */
    @OnDocumentStart
    public void OnDocumentStart(Document document) {
        Collection<Table> coveringTable = document.findAll(Table.class);
        for (Table table : coveringTable) {
            List<Cell> coveredCells = document.findCovered(Cell.class, table);

            for (Cell coveredCell : coveredCells) {
                String cellText = coveredCell.getText().toLowerCase();
                for (String columnName : headerKeywords) {
                    if (cellText.contains(columnName)) {
                        headersMapping.put(table.hashCode(), coveredCell);
                    }
                }
            }
        }
        tokensCoveredWithCells = document.findAllCovering(Token.class, Cell.class);
        tokensCoveredWithTables = document.findAllCovering(Token.class, Table.class);
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {

        Collection<Cell> coveringCell = tokensCoveredWithCells.get(element);
        Collection<Table> coveringTable = tokensCoveredWithTables.get(element);
        if (coveringCell != null && coveringTable != null && headersMapping.size() > 0) {
            Integer key = coveringTable.iterator().next().hashCode();
            for (Cell cell : coveringCell) {
                if (headersMapping.containsKey(key)) {
                    Cell headerCell = headersMapping.get(key);
                    if (isUsedForColumn) {
                        if (headerCell.getColumnIndex() == cell.getColumnIndex() && headerCell.getRowIndex() < cell.getRowIndex()) {
                           return Collections.singleton(new Feature(featureName, 1.0));
                        }
                    } else {
                        if (headerCell.getRowIndex() == cell.getRowIndex() && headerCell.getColumnIndex() < cell.getColumnIndex()) {
                            return Collections.singleton(new Feature(featureName, 1.0));
                        }
                    }
                }
            }
        }
        return Collections.emptySet();
    }
}
