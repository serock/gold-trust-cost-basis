// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.TableSortField;
import com.sun.star.util.MalformedNumberFormatException;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;
import text.Constants;

public class GrossProceedsSheetBuilder extends SheetBuilder {

    public GrossProceedsSheetBuilder() {
        super();
    }

    @Override
    public void build() throws IllegalArgumentException, com.sun.star.uno.Exception {
        final XSpreadsheet grossProceedsSheet = SpreadsheetDocumentHelper.addSheet(document(), "gld-gross-proceeds");
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnPropertiesCollection = createColumnPropertiesCollection();
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnPropertiesCollection);
        sheetHelper().setSortFields(createSortFields());
        sheetHelper().updateSheet(grossProceedsSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), grossProceedsSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private void addDateColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int dateFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "MM/DD/YYYY");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(dateFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private static void addNumberFormatColumnProperty(final SortedMap<String, Object> columnProperties, final Integer indexKey) {
        columnProperties.put("NumberFormat", indexKey);
    }

    private void addOuncesColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) throws MalformedNumberFormatException {
        final int ouncesFormatIndexKey = SpreadsheetDocumentHelper.addNumberFormatCode(document(), "#,##0.00000000;[RED]-#,##0.00000000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(ouncesFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addOuncesSoldColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int ouncesFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "#,##0.00000000;[RED]-#,##0.00000000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(ouncesFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addProceedsColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) throws MalformedNumberFormatException {
        final int proceedsFormatIndexKey = SpreadsheetDocumentHelper.addNumberFormatCode(document(), "[$$-409]#,##0.00000000;[RED]-[$$-409]#,##0.00000000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(proceedsFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }

    private List<SortedMap<String, Object>> createColumnPropertiesCollection() throws MalformedNumberFormatException {
        final List<SortedMap<String, Object>> columnPropertiesCollection = new ArrayList<>(4);
        addDateColumnProperties(columnPropertiesCollection);
        addOuncesColumnProperties(columnPropertiesCollection);
        addOuncesSoldColumnProperties(columnPropertiesCollection);
        addProceedsColumnProperties(columnPropertiesCollection);
        return columnPropertiesCollection;
    }

    private static TableSortField[] createSortFields() {
        TableSortField[] sortFields = new TableSortField[1];
        sortFields[0] = new TableSortField();
        sortFields[0].Field = Constants.GP_FIELD_DATE;
        sortFields[0].IsAscending = true;
        return sortFields;
    }
}
