// SPDX-License-Identifier: MIT
package spreadsheet.pivottable;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.DataPilotFieldOrientation;
import com.sun.star.sheet.DataPilotFieldSortInfo;
import com.sun.star.sheet.DataPilotFieldSortMode;
import com.sun.star.sheet.GeneralFunction;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XDataPilotDescriptor;
import com.sun.star.sheet.XDataPilotField;
import com.sun.star.sheet.XDataPilotTable;
import com.sun.star.sheet.XDataPilotTables;
import com.sun.star.sheet.XDataPilotTablesSupplier;
import com.sun.star.sheet.XSheetFilterDescriptor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.uno.UnoRuntime;

public class PivotTableHelper {

    private XDataPilotTables dataPilotTables;
    private XDataPilotDescriptor dataPilotDescriptor;

    public PivotTableHelper() {
        super();
    }

    public void initialize(final XSpreadsheet sheet) {
        final XDataPilotTablesSupplier dataPilotTablesSupplier = UnoRuntime.queryInterface(XDataPilotTablesSupplier.class, sheet);
        this.dataPilotTables = dataPilotTablesSupplier.getDataPilotTables();
        this.dataPilotDescriptor = dataPilotTables().createDataPilotDescriptor();
    }

    public void setColumnOrientation(final int fieldIndex) throws IndexOutOfBoundsException, WrappedTargetException, IllegalArgumentException, UnknownPropertyException, PropertyVetoException {
        UnoRuntime.queryInterface(XPropertySet.class, dataPilotDescriptor().getDataPilotFields().getByIndex(fieldIndex)).setPropertyValue("Orientation", DataPilotFieldOrientation.COLUMN);
    }

    public void setDataOrientation(final int fieldIndex) throws IndexOutOfBoundsException, WrappedTargetException, IllegalArgumentException, UnknownPropertyException, PropertyVetoException {
        UnoRuntime.queryInterface(XPropertySet.class, dataPilotDescriptor().getDataPilotFields().getByIndex(fieldIndex)).setPropertyValue("Orientation", DataPilotFieldOrientation.DATA);
    }

    public void setFilterFields(final TableFilterField[] fields) throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException, WrappedTargetException {
        final XSheetFilterDescriptor filterDescriptor = dataPilotDescriptor().getFilterDescriptor();
        filterDescriptor.setFilterFields(fields);
        UnoRuntime.queryInterface(XPropertySet.class, filterDescriptor).setPropertyValue("ContainsHeader", Boolean.TRUE);
    }

    public void setRowOrientation(final int fieldIndex) throws IndexOutOfBoundsException, WrappedTargetException, IllegalArgumentException, UnknownPropertyException, PropertyVetoException {
        UnoRuntime.queryInterface(XPropertySet.class, dataPilotDescriptor().getDataPilotFields().getByIndex(fieldIndex)).setPropertyValue("Orientation", DataPilotFieldOrientation.ROW);
    }

    public void setSortInfo(final int fieldIndex, final String[] fieldValues) throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException, WrappedTargetException, IndexOutOfBoundsException, NoSuchElementException {
        final DataPilotFieldSortInfo sortInfo = new DataPilotFieldSortInfo();
        sortInfo.IsAscending = false;
        sortInfo.Mode = DataPilotFieldSortMode.MANUAL;
        UnoRuntime.queryInterface(XPropertySet.class, dataPilotDescriptor().getDataPilotFields().getByIndex(fieldIndex)).setPropertyValue("SortInfo", sortInfo);
        final XDataPilotField dataPilotField = UnoRuntime.queryInterface(XDataPilotField.class, dataPilotDescriptor().getDataPilotFields().getByIndex(fieldIndex));
        final XNameAccess dataPilotItems = UnoRuntime.queryInterface(XNameAccess.class, dataPilotField.getItems());
        int i = 0;
        for (String fieldValue : fieldValues) {
            UnoRuntime.queryInterface(XPropertySet.class, dataPilotItems.getByName(fieldValue)).setPropertyValue("Position", Integer.valueOf(i));
            i++;
        }
    }

    public void setSourceRange(final CellRangeAddress sourceRange) {
        dataPilotDescriptor().setSourceRange(sourceRange);
    }

    public void setSumFunction(final int fieldIndex) throws IndexOutOfBoundsException, WrappedTargetException, IllegalArgumentException, UnknownPropertyException, PropertyVetoException {
        UnoRuntime.queryInterface(XPropertySet.class, dataPilotDescriptor().getDataPilotFields().getByIndex(fieldIndex)).setPropertyValue("Function", GeneralFunction.SUM);
    }

    public void showFilterButton(final boolean show) throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException, WrappedTargetException {
        UnoRuntime.queryInterface(XPropertySet.class, dataPilotDescriptor()).setPropertyValue("ShowFilterButton", Boolean.valueOf(show));
    }

    public void showTotalsColumn(final boolean show) throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException, WrappedTargetException {
        UnoRuntime.queryInterface(XPropertySet.class, dataPilotDescriptor()).setPropertyValue("ColumnGrand", Boolean.valueOf(show));
    }

    public void insertPivotTable(final String pivotTableName, final CellAddress cellAddress) {
        dataPilotTables().insertNewByName(pivotTableName, cellAddress, dataPilotDescriptor());
    }

    public void refreshPivotTable(final String pivotTableName) throws NoSuchElementException, WrappedTargetException {
        UnoRuntime.queryInterface(XDataPilotTable.class, dataPilotTables().getByName(pivotTableName)).refresh();
    }

    private XDataPilotDescriptor dataPilotDescriptor() {
        return this.dataPilotDescriptor;
    }

    private XDataPilotTables dataPilotTables() {
        return this.dataPilotTables;
    }
}
