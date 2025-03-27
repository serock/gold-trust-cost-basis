// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.TableSortField;
import com.sun.star.uno.Exception;
import com.sun.star.util.MalformedNumberFormatException;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;
import text.Constants;

public class GoldOuncesSheetBuilder extends SheetBuilder {

    @Override
    public void build() throws Exception {
        final XSpreadsheet goldOuncesSheet = SpreadsheetDocumentHelper.addSheet(document(), "gld-gold-ounces");
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnPropertiesCollection = createColumnPropertiesCollection();
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnPropertiesCollection);
        sheetHelper().setSortFields(createSortFields());
        sheetHelper().updateSheet(goldOuncesSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), goldOuncesSheet);
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

    private List<SortedMap<String, Object>> createColumnPropertiesCollection() throws MalformedNumberFormatException {
        final List<SortedMap<String, Object>> columnPropertiesCollection = new ArrayList<>(4);
        addDateColumnProperties(columnPropertiesCollection);
        addOuncesColumnProperties(columnPropertiesCollection);
        return columnPropertiesCollection;
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }

    private static TableSortField[] createSortFields() {
        TableSortField[] sortFields = new TableSortField[1];
        sortFields[0] = new TableSortField();
        sortFields[0].Field = Constants.GP_FIELD_DATE;
        sortFields[0].IsAscending = true;
        return sortFields;
    }
}
