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
        final XSpreadsheet grossProceedsSheet = SpreadsheetDocumentHelper.getSheet(document(), 0);
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnProperties = createColumnProperties();
        sheetHelper().setSheetName("gld-gross-proceeds");
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnProperties);
        sheetHelper().setSortFields(createSortFields());
        sheetHelper().updateSheet(grossProceedsSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private void addDateColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getDateNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private static void addGoldOuncesColumnProperties(final List<SortedMap<String, Object>> columnProperties, final int indexKey) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", Integer.valueOf(indexKey));
        columnProperties.add(columnPropertiesItem);
    }

    private static void addGoldOuncesSoldColumnProperties(final List<SortedMap<String, Object>> columnProperties, final int indexKey) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", Integer.valueOf(indexKey));
        columnProperties.add(columnPropertiesItem);
    }

    private static void addProceedsColumnProperties(final List<SortedMap<String, Object>> columnProperties, final int indexKey) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", Integer.valueOf(indexKey));
        columnProperties.add(columnPropertiesItem);
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }

    private List<SortedMap<String, Object>> createColumnProperties() throws MalformedNumberFormatException {
        final int ouncesFormatIndexKey = SpreadsheetDocumentHelper.addNumberFormatCode(document(), "#,##0.00000000;[RED]-#,##0.00000000");
        final int proceedsFormatIndexKey = SpreadsheetDocumentHelper.addNumberFormatCode(document(), "[$$-409]#,##0.00000000;[RED]-[$$-409]#,##0.00000000");
        final List<SortedMap<String, Object>> columnProperties = new ArrayList<>(4);
        addDateColumnProperties(columnProperties);
        addGoldOuncesColumnProperties(columnProperties, ouncesFormatIndexKey);
        addGoldOuncesSoldColumnProperties(columnProperties, ouncesFormatIndexKey);
        addProceedsColumnProperties(columnProperties, proceedsFormatIndexKey);
        return columnProperties;
    }

    private static TableSortField[] createSortFields() {
        TableSortField[] sortFields = new TableSortField[1];
        sortFields[0] = new TableSortField();
        sortFields[0].Field = Constants.GP_FIELD_DATE;
        sortFields[0].IsAscending = true;
        return sortFields;
    }
}
