// SPDX-License-Identifier: MIT
package spreadsheet.sheet;

import com.sun.star.sheet.XSpreadsheetDocument;

public abstract class SheetBuilder {

    private final SheetHelper sheetHelper;

    private XSpreadsheetDocument spreadsheetDocument;

    public SheetBuilder() {
        this.sheetHelper = new SheetHelper();
    }

    public void setDocument(final XSpreadsheetDocument document) {
        this.spreadsheetDocument = document;
    }

    public void setSheetFormulas(final String[][] formulas) {
        sheetHelper().setSheetFormulas(formulas);
    }

    public abstract void build() throws com.sun.star.uno.Exception;

    protected XSpreadsheetDocument document() {
        return this.spreadsheetDocument;
    }

    protected SheetHelper sheetHelper() {
        return this.sheetHelper;
    }
}
