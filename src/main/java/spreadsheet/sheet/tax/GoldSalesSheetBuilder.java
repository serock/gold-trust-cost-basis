// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import com.sun.star.awt.FontWeight;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.SheetBuilder;
import spreadsheet.sheet.SheetHelper;

public class GoldSalesSheetBuilder extends SheetBuilder {

    private static int GGP_DATE_INDEX = 0;
    private static int TL_DATE_ACQUIRED_INDEX = 2;
    private static int TL_DATE_SOLD_INDEX = 3;

    @Override
    public void build() throws com.sun.star.uno.Exception {
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

    private void addGoldOuncesOwnedColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int ouncesFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "#,##0.00000000;[RED]-#,##0.00000000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(ouncesFormatIndexKey));
        columnPropertiesCollection.add(columnProperties);
    }

    private void addGoldOuncesSoldColumnProperties(final List<SortedMap<String, Object>> columnPropertiesCollection) {
        final int ouncesFormatIndexKey = SpreadsheetDocumentHelper.queryNumberFormatCode(document(), "#,##0.00000000;[RED]-#,##0.00000000");
        final SortedMap<String, Object> columnProperties = new TreeMap<>();
        addNumberFormatColumnProperty(columnProperties, Integer.valueOf(ouncesFormatIndexKey));
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

    private String[][] createFormulas() throws com.sun.star.uno.Exception {
        List<String[]> rowsCollection = new ArrayList<>();
        rowsCollection.add(headerRow());
        final int taxYear = taxYear();
        final Object[][] goldOuncesData = goldOuncesData();
        final Object[][] grossProceedsData = grossProceedsData();
        final Object[][] taxLotData = taxLotData();
        int numberOfTaxLots = taxLotData.length - 1;
        for (int i = 1; i <= numberOfTaxLots; i++) {
            final Object[] row = taxLotData[i];
            final Double dateAcquiredObject = ((Double) row[TL_DATE_ACQUIRED_INDEX]); 
            final double dateAcquired = dateAcquiredObject.doubleValue();
            final int dateAcquiredYear = SpreadsheetDocumentHelper.year(dateAcquired);
            final Double dateSoldObject =
                    (row[TL_DATE_SOLD_INDEX] instanceof Double) ?
                            ((Double) row[TL_DATE_SOLD_INDEX]) :
                                SpreadsheetDocumentHelper.dateValue(Integer.toString(taxYear + 1) + "-01-01");
            final double dateSold = dateSoldObject.doubleValue();
            if (dateAcquiredYear > taxYear) {
                continue;
            } else if (dateAcquiredYear < taxYear) {
                if (Arrays.stream(grossProceedsData).skip(1).anyMatch(t -> ((Double) t[GGP_DATE_INDEX]).doubleValue() < dateSold)) {
                    rowsCollection.add(new String[] {"=$'tax-lots'.A" + Integer.toString(i + 1), "", "", "", "", ""});
                }
            } else {
                rowsCollection.add(
                        new String[] {
                                "=$'tax-lots'.A" + Integer.toString(i + 1),
                                "",
                                "",
                                "=$'tax-lots'.B" + Integer.toString(i + 1) + "*VLOOKUP($'tax-lots'.C" + Integer.toString(i + 1) +";$'gld-gold-ounces'.A1:B" + Integer.toString(goldOuncesData.length) + ";2)",
                                "",
                                "=$'tax-lots'.F" + Integer.toString(i + 1)
                                });
            }
            final int[] saleIndices = IntStream.range(1, grossProceedsData.length)
                .filter(j -> ((Double) grossProceedsData[j][GGP_DATE_INDEX]).compareTo(dateAcquiredObject) > 0 && ((Double) grossProceedsData[j][GGP_DATE_INDEX]).compareTo(dateSoldObject) < 0)
                .toArray();
            for (int saleIndex : saleIndices) {
                rowsCollection.add(
                        new String[] {
                                "=$'tax-lots'.A" + Integer.toString(i + 1),
                                "=$'gld-gross-proceeds'.A" + Integer.toString(saleIndex + 1),
                                "=$'tax-lots'.B" + Integer.toString(i + 1) + "*$'gld-gross-proceeds'.B" + Integer.toString(saleIndex + 1),
                                "=D" + Integer.toString(rowsCollection.size()) + "-C" + Integer.toString(rowsCollection.size() + 1),
                                "=ROUND(F" + Integer.toString(rowsCollection.size()) + "*C" + Integer.toString(rowsCollection.size() + 1) + "/D" + Integer.toString(rowsCollection.size()) + ";2)",
                                "=F" + Integer.toString(rowsCollection.size()) + "-E" + Integer.toString(rowsCollection.size() + 1)
                                });
            }
        }
        return rowsCollection.toArray(new String[0][0]);
    }

    private Object[][] goldOuncesData() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet gldGoldOuncesSheet = SpreadsheetDocumentHelper.getSheet(document(), "gld-gold-ounces");
        return SheetHelper.getData(gldGoldOuncesSheet);
    }

    private Object[][] grossProceedsData() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet gldGrossProceedsSheet = SpreadsheetDocumentHelper.getSheet(document(), "gld-gross-proceeds");
        return SheetHelper.getData(gldGrossProceedsSheet);
    }

    private static String[] headerRow() {
        return new String[] {
                "Tax Lot ID",
                "Date of Gold Sale",
                "Gold Ounces Sold",
                "Gold Ounces Owned",
                "Cost of Gold Sold",
                "Adjusted Cost Basis"
        };
    }

    private Object[][] taxLotData() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet taxLotsSheet = SpreadsheetDocumentHelper.getSheet(document(), "tax-lots");
        return SheetHelper.getData(taxLotsSheet);
    }

    private int taxYear() throws com.sun.star.uno.Exception {
        final XSpreadsheet gldGoldOuncesSheet = SpreadsheetDocumentHelper.getSheet(document(), "gld-gold-ounces");
        final double date = SheetHelper.getCellValue(gldGoldOuncesSheet, "A2");
        return SpreadsheetDocumentHelper.year(date);
    }
}
