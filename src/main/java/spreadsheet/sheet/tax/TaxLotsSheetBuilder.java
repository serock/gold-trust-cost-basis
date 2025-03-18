// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.uno.Exception;
import com.sun.star.util.MalformedNumberFormatException;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;

public class TaxLotsSheetBuilder extends SheetBuilder {

    @Override
    public void build() throws Exception {
        final XSpreadsheet taxLotsSheet = SpreadsheetDocumentHelper.getSheet(document(), 0);
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnProperties = createColumnProperties();
        sheetHelper().setSheetName("tax-lots");
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnProperties);
        sheetHelper().updateSheet(taxLotsSheet);
        SpreadsheetDocumentHelper.setActiveSheet(document(), taxLotsSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private void addCostColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addDateAcquiredColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getDateNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addDateSoldColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getDateNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private void addProceedsColumnProperties(final List<SortedMap<String, Object>> columnProperties) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnProperties.add(columnPropertiesItem);
    }

    private static void addSharesColumnProperties(final List<SortedMap<String, Object>> columnProperties, final int indexKey) {
        final SortedMap<String, Object> columnPropertiesItem = new TreeMap<>();
        columnPropertiesItem.put("NumberFormat", Integer.valueOf(indexKey));
        columnProperties.add(columnPropertiesItem);
    }

    private List<SortedMap<String, Object>> createColumnProperties() throws MalformedNumberFormatException {
        final int sharesFormatIndexKey = SpreadsheetDocumentHelper.addNumberFormatCode(document(), "#,##0.0000;[RED]-#,##0.0000");
        final List<SortedMap<String, Object>> columnProperties = new ArrayList<>(4);
        addSharesColumnProperties(columnProperties, sharesFormatIndexKey);
        addDateAcquiredColumnProperties(columnProperties);
        addDateSoldColumnProperties(columnProperties);
        addProceedsColumnProperties(columnProperties);
        addCostColumnProperties(columnProperties);
        return columnProperties;
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }
}
