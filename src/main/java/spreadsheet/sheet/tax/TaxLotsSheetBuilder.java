// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.util.MalformedNumberFormatException;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;

public class TaxLotsSheetBuilder extends SheetBuilder {

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet taxLotsSheet = SpreadsheetDocumentHelper.addSheet(document(), "tax-lots");
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnPropertiesCollection = createColumnPropertiesCollection();
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnPropertiesCollection);
        sheetHelper().updateSheet(taxLotsSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), taxLotsSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private void addCostColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addDateAcquiredColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int dateFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "MM/DD/YYYY");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(dateFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addDateSoldColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int dateFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "MM/DD/YYYY");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(dateFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addProceedsColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private static void addNumberFormatColumnProperty(final SortedMap<String, Object> columnProperties, final Integer indexKey) {
        columnProperties.put("NumberFormat", indexKey);
    }

    private void addSharesColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) throws MalformedNumberFormatException {
        final int sharesFormatIndexKey = SpreadsheetDocumentHelper.addNumberFormatCode(document(), "#,##0.000;[RED]-#,##0.000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(sharesFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addTaxLotIDColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private List<SortedMap<String, Object>> createColumnPropertiesCollection() throws MalformedNumberFormatException {
        final List<SortedMap<String, Object>> columnPropertiesCollection = new ArrayList<>(4);
        addTaxLotIDColumnProperties(columnPropertiesCollection);
        addSharesColumnProperties(columnPropertiesCollection);
        addDateAcquiredColumnProperties(columnPropertiesCollection);
        addDateSoldColumnProperties(columnPropertiesCollection);
        addProceedsColumnProperties(columnPropertiesCollection);
        addCostColumnProperties(columnPropertiesCollection);
        return columnPropertiesCollection;
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }
}
