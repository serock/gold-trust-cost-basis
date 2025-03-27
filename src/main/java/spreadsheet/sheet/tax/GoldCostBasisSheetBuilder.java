// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.uno.Exception;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class GoldCostBasisSheetBuilder extends PivotTableSheetBuilder {

    @Override
    public void build() throws Exception {
        final XSpreadsheet goldCostBasisSheet = SpreadsheetDocumentHelper.addSheet(document(), "gold-cost-basis");
        final CellAddress cellAddress = SheetHelper.getCellAddress(goldCostBasisSheet, 0, 0);

        pivotTableHelper().initialize(goldCostBasisSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.GS_FIELD_TAX_LOT_ID);
        pivotTableHelper().setColumnOrientation(Constants.GS_FIELD_DATE_OF_GOLD_SALE);
        pivotTableHelper().setDataOrientation(Constants.GS_FIELD_COST_OF_GOLD_SOLD);
        pivotTableHelper().setSumFunction(Constants.GS_FIELD_COST_OF_GOLD_SOLD);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("gold-cost-basis", cellAddress);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet goldSalesSheet = SpreadsheetDocumentHelper.getSheet(document(), "gold-sales");
        return SheetHelper.getCellRangeAddressOfUsedArea(goldSalesSheet);
    }
}
