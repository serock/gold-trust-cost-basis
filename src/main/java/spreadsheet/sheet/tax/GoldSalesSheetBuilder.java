// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.star.awt.FontWeight;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.uno.Exception;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;

public class GoldSalesSheetBuilder extends SheetBuilder {

    @Override
    public void build() throws Exception {
        final XSpreadsheet goldSalesSheet = SpreadsheetDocumentHelper.addSheet(document(), "gold-sales");
        sheetHelper().setSheetFormulas(createFormulas());
        final SortedMap<String, Object> headerProperties = createHeaderProperties();
        final List<SortedMap<String, Object>> columnPropertiesCollection = createColumnPropertiesCollection();
        sheetHelper().setHeaderProperties(headerProperties);
        sheetHelper().setColumnProperties(columnPropertiesCollection);
        sheetHelper().updateSheet(goldSalesSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), goldSalesSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private static String[][] createFormulas() {
        return new String[][] {{"Tax Lot ID", "Date of Gold Sale", "Gold Ounces Sold", "Gold Ounces Owned", "Cost of Gold Sold", "Adjusted Cost Basis"}};
    }

    private SortedMap<String, Object> createHeaderProperties() {
        final SortedMap<String, Object> headerProperties = new TreeMap<>();
        headerProperties.put("CharWeight", Float.valueOf(FontWeight.BOLD));
        headerProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        return headerProperties;
    }

    private List<SortedMap<String, Object>> createColumnPropertiesCollection() {
        final List<SortedMap<String, Object>> columnPropertiesCollection = new ArrayList<>(6);
        addTaxLotIdColumnProperties(columnPropertiesCollection);
        addDateColumnProperties(columnPropertiesCollection);
        addGoldOuncesSoldColumnProperties(columnPropertiesCollection);
        addGoldOuncesOwnedColumnProperties(columnPropertiesCollection);
        addCostOfGoldSoldColumnProperties(columnPropertiesCollection);
        addAdjustedCostBasisColumnProperties(columnPropertiesCollection);
        return columnPropertiesCollection;
    }

    private static void addNumberFormatColumnProperty(final SortedMap<String, Object> columnProperties, final Integer indexKey) {
        columnProperties.put("NumberFormat", indexKey);
    }

    private void addTaxLotIdColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addDateColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int dateFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "MM/DD/YYYY");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(dateFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addGoldOuncesSoldColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int ouncesFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "#,##0.00000000;[RED]-#,##0.00000000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(ouncesFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addGoldOuncesOwnedColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int ouncesFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "#,##0.00000000;[RED]-#,##0.00000000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(ouncesFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addCostOfGoldSoldColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addAdjustedCostBasisColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }
}
