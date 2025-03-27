// SPDX-License-Identifier: MIT
package spreadsheet.sheet;

import spreadsheet.pivottable.PivotTableHelper;

public abstract class PivotTableSheetBuilder extends SheetBuilder {

    private final PivotTableHelper pivotTableHelper;

    public PivotTableSheetBuilder() {
        super();
        this.pivotTableHelper = new PivotTableHelper();
    }

    protected PivotTableHelper pivotTableHelper() {
        return this.pivotTableHelper;
    }
}
