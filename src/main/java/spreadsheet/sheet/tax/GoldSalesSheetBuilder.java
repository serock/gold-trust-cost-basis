// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import com.sun.star.awt.FontWeight;
import com.sun.star.beans.PropertyValue;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.ConditionOperator;
import com.sun.star.sheet.XSpreadsheet;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class GoldSalesSheetBuilder extends SheetBuilder {

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final String sheetName = "gold-sales";
        if (SpreadsheetDocumentHelper.hasSheet(document(), sheetName)) {
            return;
        }
        final XSpreadsheet goldSalesSheet = SpreadsheetDocumentHelper.addSheet(document(), sheetName);
        final String[][] sheetFormulas = createFormulas();
        sheetHelper().setSheetFormulas(sheetFormulas);
        sheetHelper().setConditionalFormats(createConditionalFormats(sheetFormulas));
        sheetHelper().setHeaderProperties(createHeaderProperties());
        sheetHelper().setColumnProperties(createColumnPropertiesCollection());
        sheetHelper().updateSheet(goldSalesSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), goldSalesSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 1);
    }

    private void addAdjustedCostBasisColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addCostOfGoldSoldColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("NumberFormat", SpreadsheetDocumentHelper.getCurrencyNumberFormat(document()));
        columnPropertiesCollection.add(columnProperties);
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

    private void addTaxLotIdColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        columnProperties.put("NumberFormat", SpreadsheetDocumentHelper.getTextFormat(document()));
        columnPropertiesCollection.add(columnProperties);
    }

    private Object[][] costBasisFactorData() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet costBasisFactorsSheet = SpreadsheetDocumentHelper.getSheet(document(), "cost-basis-factors");
        return SheetHelper.getData(costBasisFactorsSheet);
    }

    private static PropertyValue[] createBadCondition() {
        final List<PropertyValue> badCondition = new ArrayList<>(3);
        PropertyValue condition;
        condition = new PropertyValue();
        condition.Name = "Operator";
        condition.Value = ConditionOperator.EQUAL;
        badCondition.add(condition);
        condition = new PropertyValue();
        condition.Name = "Formula1";
        condition.Value = "";
        badCondition.add(condition);
        condition = new PropertyValue();
        condition.Name = "StyleName";
        condition.Value = "Bad";
        badCondition.add(condition);
        return badCondition.toArray(new PropertyValue[0]);
    }

    private static Map<String, List<PropertyValue[]>> createConditionalFormats(final String[][] formulas) {
        final Map<String, List<PropertyValue[]>> formats = new HashMap<>();
        final List<PropertyValue[]> conditions = createConditions();
        int i = 1;
        String cellRangeName;
        for (String[] row : formulas) {
            if ("".equals(row[Constants.GS_FIELD_ADJUSTED_COST_BASIS])) {
                cellRangeName = "D" + Integer.toString(i);
                formats.put(cellRangeName, conditions);
            }
            i++;
        }
        return formats;
    }

    private static List<PropertyValue[]> createConditions() {
        final List<PropertyValue[]> conditions = new ArrayList<>(2);
        conditions.add(createDefaultCondition());
        conditions.add(createBadCondition());
        return conditions;
    }

    private static PropertyValue[] createDefaultCondition() {
        final List<PropertyValue> defaultCondition = new ArrayList<>(3);
        PropertyValue condition;
        condition = new PropertyValue();
        condition.Name = "Operator";
        condition.Value = ConditionOperator.NOT_EQUAL;
        defaultCondition.add(condition);
        condition = new PropertyValue();
        condition.Name = "Formula1";
        condition.Value = "";
        defaultCondition.add(condition);
        condition = new PropertyValue();
        condition.Name = "StyleName";
        condition.Value = "Default";
        defaultCondition.add(condition);
        return defaultCondition.toArray(new PropertyValue[0]);
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
        addCostOfGoldSoldColumnProperties(columnPropertiesCollection);
        addAdjustedCostBasisColumnProperties(columnPropertiesCollection);
        return columnPropertiesCollection;
    }

    private String[][] createFormulas() throws com.sun.star.uno.Exception {
        List<String[]> rowsCollection = new ArrayList<>();
        rowsCollection.add(headerRow());
        final int taxYear = taxYear();
        final Object[][] costBasisFactorData = costBasisFactorData();
        final Object[][] taxLotData = taxLotData();
        int numberOfTaxLots = taxLotData.length - 1;
        for (int i = 1; i <= numberOfTaxLots; i++) {
            final Object[] row = taxLotData[i];
            final Double dateAcquiredObject = ((Double) row[Constants.TL_FIELD_DATE_ACQUIRED]);
            final double dateAcquired = dateAcquiredObject.doubleValue();
            final int dateAcquiredYear = SpreadsheetDocumentHelper.year(dateAcquired);
            final Double dateSoldObject =
                    (row[Constants.TL_FIELD_DATE_SOLD] instanceof Double) ?
                            ((Double) row[Constants.TL_FIELD_DATE_SOLD]) :
                                SpreadsheetDocumentHelper.dateValue(Integer.toString(taxYear + 1) + "-01-01");
            final double dateSold = dateSoldObject.doubleValue();
            if (dateAcquiredYear > taxYear) {
                continue;
            } else if (dateAcquiredYear < taxYear) {
                if (Arrays.stream(costBasisFactorData).skip(1).anyMatch(t -> ((Double) t[Constants.CBF_FIELD_DATE]).doubleValue() < dateSold)) {
                    rowsCollection.add(new String[] {"=$'tax-lots'.A" + Integer.toString(i + 1), "", "", ""});
                }
            } else {
                rowsCollection.add(
                        new String[] {
                                "=$'tax-lots'.A" + Integer.toString(i + 1),
                                "",
                                "",
                                "=$'tax-lots'.F" + Integer.toString(i + 1)
                                });
            }
            final int[] factorIndices = IntStream.range(1, costBasisFactorData.length)
                .filter(j -> ((Double) costBasisFactorData[j][Constants.CBF_FIELD_DATE]).compareTo(dateAcquiredObject) > 0 && ((Double) costBasisFactorData[j][Constants.CBF_FIELD_DATE]).compareTo(dateSoldObject) < 0)
                .toArray();
            for (int factorIndex : factorIndices) {
                rowsCollection.add(
                        new String[] {
                                "=$'tax-lots'.A" + Integer.toString(i + 1),
                                "=$'cost-basis-factors'.A" + Integer.toString(factorIndex + 1),
                                "=$'cost-basis-factors'.B" + Integer.toString(factorIndex + 1) + "*D" + Integer.toString(rowsCollection.size()),
                                "=$D" + Integer.toString(rowsCollection.size()) + "-C" + Integer.toString(rowsCollection.size() + 1)
                                });
            }
        }
        return rowsCollection.toArray(new String[0][0]);
    }

    private static String[] headerRow() {
        return new String[] {
                "Tax Lot ID",
                "Date of Gold Sale",
                "Cost of Gold Sold",
                "Adjusted Cost Basis"
        };
    }

    private Object[][] taxLotData() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet taxLotsSheet = SpreadsheetDocumentHelper.getSheet(document(), "tax-lots");
        return SheetHelper.getData(taxLotsSheet);
    }

    private int taxYear() throws com.sun.star.uno.Exception {
        final XSpreadsheet costBasisFactorsSheet = SpreadsheetDocumentHelper.getSheet(document(), "cost-basis-factors");
        final double date = SheetHelper.getCellValue(costBasisFactorsSheet, "A2");
        return SpreadsheetDocumentHelper.year(date);
    }
}
