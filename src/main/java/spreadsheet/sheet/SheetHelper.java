// SPDX-License-Identifier: MIT
package spreadsheet.sheet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Stream;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XMultiPropertySet;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNamed;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XCellAddressable;
import com.sun.star.sheet.XCellRangeAddressable;
import com.sun.star.sheet.XCellRangeData;
import com.sun.star.sheet.XCellRangeFormula;
import com.sun.star.sheet.XSheetConditionalEntries;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XUsedAreaCursor;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.table.TableSortField;
import com.sun.star.table.XCellRange;
import com.sun.star.table.XColumnRowRange;
import com.sun.star.table.XTableColumns;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XSortable;

public class SheetHelper {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private Map<String, List<PropertyValue[]>> conditionalFormats = Collections.emptyMap();
    private List<SortedMap<String, Object>> columnProperties = Collections.emptyList();
    private SortedMap<String, Object> headerProperties = Collections.emptySortedMap();
    private String[][] sheetFormulas;
    private String sheetName;
    private TableSortField[] sortFields;

    private static void addConditionalFormat(final XSpreadsheet sheet, final String cellRangeName, final PropertyValue[] condition) throws UnknownPropertyException, WrappedTargetException, IllegalArgumentException, PropertyVetoException {
        final XCellRange cellRange = sheet.getCellRangeByName(cellRangeName);
        final XPropertySet propertySet = UnoRuntime.queryInterface(XPropertySet.class, cellRange);
        final XSheetConditionalEntries conditionalEntries = UnoRuntime.queryInterface(XSheetConditionalEntries.class, propertySet.getPropertyValue("ConditionalFormat"));
        conditionalEntries.addNew(condition);
        propertySet.setPropertyValue("ConditionalFormat", conditionalEntries);
    }

    public static CellRangeAddress getCellRangeAddressOfUsedArea(final XSpreadsheet sheet) {
        return UnoRuntime.queryInterface(XCellRangeAddressable.class, getUsedAreaCursor(sheet)).getRangeAddress();
    }

    public static CellAddress getCellAddress(final XSpreadsheet sheet, final int column, final int row) throws IndexOutOfBoundsException {
        return UnoRuntime.queryInterface(XCellAddressable.class, sheet.getCellByPosition(column, row)).getCellAddress();
    }

    public static double getCellValue(final XSpreadsheet sheet, final String cellName) throws IndexOutOfBoundsException {
        return sheet.getCellRangeByName(cellName).getCellByPosition(0, 0).getValue();
    }

    public static Object[][] getData(final XSpreadsheet sheet) {
       return UnoRuntime.queryInterface(XCellRangeData.class, getUsedAreaCursor(sheet)).getDataArray();
    }

    public boolean isColumnEmpty(final int column) {
        return Stream.of(sheetFormulas())
                .skip(1)
                .map(row -> row[column])
                .allMatch(cell -> "".equals(cell));
    }

    public void setHeaderProperties(final SortedMap<String, Object> properties) {
        this.headerProperties = properties;
    }

    public void setColumnProperties(final List<SortedMap<String, Object>> properties) {
        this.columnProperties = properties;
    }

    public void setConditionalFormats(final Map<String, List<PropertyValue[]>> formats) {
        this.conditionalFormats = formats;
    }

    public void setSheetFormulas(final String[][] formulas) {
        this.sheetFormulas = formulas;
    }

    public void setSheetName(final String name) {
        this.sheetName = name;
    }

    public void setSortFields(final TableSortField[] fields) {
        this.sortFields = fields;
    }

    public void updateSheet(final XSpreadsheet sheet, final boolean optimalWidth) throws com.sun.star.uno.Exception {
        if (sheetName() != null) {
            setSheetName(sheet);
        }
        if (sheetFormulas() != null) {
            setFormulas(sheet);
        }
        if (!headerProperties().isEmpty()) {
            setHeaderProperties(sheet);
        }
        if (!columnProperties().isEmpty()) {
            setColumnProperties(sheet);
        }
        if (!headerProperties().isEmpty()) {
            // do again for NumberFormat property
            setHeaderProperties(sheet);
        }
        if (!conditionalFormats().isEmpty()) {
            final Set<String> cellRangeNames = conditionalFormats().keySet();
            List<PropertyValue[]> conditions;
            for (String cellRangeName : cellRangeNames) {
                conditions = conditionalFormats().get(cellRangeName);
                for (PropertyValue[] condition : conditions) {
                    addConditionalFormat(sheet, cellRangeName, condition);
                }
            }
        }
        if (optimalWidth) {
            setOptimalWidth(sheet);
        }
        if (sortFields() != null) {
            sort(sheet);
        }
    }

    private static XTableColumns getUsedColumns(final XSpreadsheet sheet) {
       return UnoRuntime.queryInterface(XColumnRowRange.class, getUsedAreaCursor(sheet)).getColumns();
    }

    private void setSheetName(final XSpreadsheet sheet) {
        UnoRuntime.queryInterface(XNamed.class, sheet).setName(sheetName());
    }

    private void setHeaderProperties(final XSpreadsheet sheet) throws IndexOutOfBoundsException, IllegalArgumentException, PropertyVetoException, WrappedTargetException {
        final XCellRange cellRange = sheet.getCellRangeByPosition(0, 0, columnProperties().size() - 1, 0);
        final String[] keys = headerProperties().keySet().toArray(EMPTY_STRING_ARRAY);
        final Object[] values = headerProperties().values().toArray();
        UnoRuntime.queryInterface(XMultiPropertySet.class, cellRange).setPropertyValues(keys, values);
    }

    private void setColumnProperties(final XSpreadsheet sheet) throws IndexOutOfBoundsException, WrappedTargetException, IllegalArgumentException, PropertyVetoException {
        XTableColumns columns = UnoRuntime.queryInterface(XColumnRowRange.class, sheet).getColumns();
        int i = 0;
        String[] keys;
        Object[] values;
        for (SortedMap<String, Object> propertyCollection : columnProperties()) {
            keys = propertyCollection.keySet().toArray(EMPTY_STRING_ARRAY);
            values = propertyCollection.values().toArray();
            UnoRuntime.queryInterface(XMultiPropertySet.class, columns.getByIndex(i)).setPropertyValues(keys, values);
            i++;
        }
    }

    private void setOptimalWidth(final XSpreadsheet sheet) throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException, WrappedTargetException, IndexOutOfBoundsException {
        final XTableColumns columns = getUsedColumns(sheet);
        final int count = columns.getCount();
        if (columnProperties().isEmpty()) {
            for (int index = 0; index < count; index++) {
                UnoRuntime.queryInterface(XPropertySet.class, columns.getByIndex(index)).setPropertyValue("OptimalWidth", Boolean.TRUE);
            }
        } else {
            for (int index = 0; index < count; index++) {
                if (((Boolean) columnProperties().get(index).getOrDefault("IsVisible", Boolean.TRUE)).booleanValue()) {
                    UnoRuntime.queryInterface(XPropertySet.class, columns.getByIndex(index)).setPropertyValue("OptimalWidth", Boolean.TRUE);
                }
            }
        }
    }

    private static XUsedAreaCursor getUsedAreaCursor(final XSpreadsheet sheet) {
        final XUsedAreaCursor usedAreaCursor = UnoRuntime.queryInterface(XUsedAreaCursor.class, sheet.createCursor());
        usedAreaCursor.gotoStartOfUsedArea(false);
        usedAreaCursor.gotoEndOfUsedArea(true);
        return usedAreaCursor;
    }

    private void sort(final XSpreadsheet sheet) {
        XSortable sortable = UnoRuntime.queryInterface(XSortable.class, getUsedAreaCursor(sheet));
        PropertyValue[] sortDescriptor = sortable.createSortDescriptor();
        if (!headerProperties().isEmpty()) {
            Stream.of(sortDescriptor)
                .filter(pv -> "ContainsHeader".equals(pv.Name))
                .findFirst()
                .get().Value = Boolean.TRUE;
        }
        Stream.of(sortDescriptor)
            .filter(pv -> "SortFields".equals(pv.Name))
            .findFirst()
            .get().Value = sortFields();
        sortable.sort(sortDescriptor);
    }

    private void setFormulas(final XSpreadsheet sheet) throws IndexOutOfBoundsException {
        final XCellRange cellRange = sheet.getCellRangeByPosition(0, 0, sheetFormulas()[0].length - 1, sheetFormulas().length - 1);
        UnoRuntime.queryInterface(XCellRangeFormula.class, cellRange).setFormulaArray(sheetFormulas());
    }

    private Map<String, List<PropertyValue[]>> conditionalFormats() {
        return this.conditionalFormats;
    }

    private List<SortedMap<String, Object>> columnProperties() {
        return this.columnProperties;
    }

    private SortedMap<String, Object> headerProperties() {
        return this.headerProperties;
    }

    private String[][] sheetFormulas() {
        return this.sheetFormulas;
    }

    private String sheetName() {
        return this.sheetName;
    }

    private TableSortField[] sortFields() {
        return this.sortFields;
    }
}
