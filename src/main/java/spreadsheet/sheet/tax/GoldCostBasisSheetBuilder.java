// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterConnection;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.uno.Exception;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class GoldCostBasisSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] filterFields = createFilterFields();

    @Override
    public void build() throws Exception {
        final String sheetName = "gold-cost-basis";
        if (SpreadsheetDocumentHelper.hasSheet(document(), sheetName)) {
            return;
        }
        final XSpreadsheet goldCostBasisSheet = SpreadsheetDocumentHelper.addSheet(document(), sheetName);
        final CellAddress cellAddress = SheetHelper.getCellAddress(goldCostBasisSheet, 0, 0);

        pivotTableHelper().initialize(goldCostBasisSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.GS_FIELD_TAX_LOT_ID);
        pivotTableHelper().setColumnOrientation(Constants.GS_FIELD_DATE_OF_GOLD_SALE);
        pivotTableHelper().setDataOrientation(Constants.GS_FIELD_COST_OF_GOLD_SOLD);
        pivotTableHelper().setSumFunction(Constants.GS_FIELD_COST_OF_GOLD_SOLD);
        pivotTableHelper().setFilterFields(filterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("gold-cost-basis", cellAddress);
        sheetHelper().updateSheet(goldCostBasisSheet, true);
    }

    private static TableFilterField[] createFilterFields() {
        final int[] fieldNums = new int[] {
                Constants.GS_FIELD_DATE_OF_GOLD_SALE,
                Constants.GS_FIELD_COST_OF_GOLD_SOLD
        };
        final List<TableFilterField> fields = new ArrayList<>(1);

        TableFilterField field;

        for (int fieldNum : fieldNums) {
            field = new TableFilterField();
            field.Connection = FilterConnection.AND;
            field.Field = fieldNum;
            field.IsNumeric = true;
            field.Operator = FilterOperator.NOT_EMPTY;
            fields.add(field);
        }
        return fields.toArray(new TableFilterField[0]);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet goldSalesSheet = SpreadsheetDocumentHelper.getSheet(document(), "gold-sales");
        return SheetHelper.getCellRangeAddressOfUsedArea(goldSalesSheet);
    }
}
