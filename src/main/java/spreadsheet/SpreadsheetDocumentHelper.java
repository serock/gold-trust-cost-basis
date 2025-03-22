// SPDX-License-Identifier: MIT
package spreadsheet;

import java.io.File;
import java.net.MalformedURLException;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XIndexAccess;
import com.sun.star.frame.FrameSearchFlag;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop2;
import com.sun.star.frame.XModel;
import com.sun.star.frame.theDesktop;
import com.sun.star.i18n.NumberFormatIndex;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.Locale;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.sheet.XViewFreezable;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.MalformedNumberFormatException;
import com.sun.star.util.XNumberFormatTypes;
import com.sun.star.util.XNumberFormats;
import com.sun.star.util.XNumberFormatsSupplier;

public class SpreadsheetDocumentHelper {

    private final static Locale locale = new Locale();

    private final XDesktop2 desktop;

    public SpreadsheetDocumentHelper() throws BootstrapException {
        final XComponentContext componentContext = Bootstrap.bootstrap();
        this.desktop = theDesktop.get(componentContext);
    }

    public XSpreadsheetDocument createDocument() throws IllegalArgumentException, IOException {
        return UnoRuntime.queryInterface(XSpreadsheetDocument.class, this.desktop.loadComponentFromURL("private:factory/scalc", "_blank", FrameSearchFlag.CREATE, new PropertyValue[0]));
    }

    public XSpreadsheetDocument loadDocument(final File file) throws IllegalArgumentException, IOException, MalformedURLException {
        return UnoRuntime.queryInterface(XSpreadsheetDocument.class, this.desktop.loadComponentFromURL(file.toURI().toURL().toString(), "_blank", FrameSearchFlag.CREATE, new PropertyValue[0]));
    }

    public static Integer getCurrencyNumberFormat(final XSpreadsheetDocument document) {
        return Integer.valueOf(getNumberFormatTypes(document).getFormatIndex(NumberFormatIndex.CURRENCY_1000DEC2_RED, locale));
    }

    public static Integer getDateNumberFormat(final XSpreadsheetDocument document) {
        return Integer.valueOf(getNumberFormatTypes(document).getFormatIndex(NumberFormatIndex.DATE_DIN_YYYYMMDD, locale));
    }

    public static Integer getPercentNumberFormat(final XSpreadsheetDocument document) {
        return Integer.valueOf(getNumberFormatTypes(document).getFormatIndex(NumberFormatIndex.PERCENT_DEC2, locale));
    }

    public static Integer getTextFormat(final XSpreadsheetDocument document) {
        return Integer.valueOf(getNumberFormatTypes(document).getFormatIndex(NumberFormatIndex.TEXT, locale));
    }

    public static XSpreadsheet getSheet(final XSpreadsheetDocument document, final int index) throws IndexOutOfBoundsException, WrappedTargetException {
        final XIndexAccess indexedSheets = UnoRuntime.queryInterface(XIndexAccess.class, document.getSheets());
        return UnoRuntime.queryInterface(XSpreadsheet.class, indexedSheets.getByIndex(index));
    }

    public static XSpreadsheet getSheet(final XSpreadsheetDocument document, final String sheetName) throws WrappedTargetException, NoSuchElementException {
        return UnoRuntime.queryInterface(XSpreadsheet.class, document.getSheets().getByName(sheetName));
    }

    public static boolean hasSheet(final XSpreadsheetDocument document, final String sheetName) {
        return document.getSheets().hasByName(sheetName);
    }

    public static XSpreadsheet getActiveSheet(final XSpreadsheetDocument document) {
        return UnoRuntime.queryInterface(XSpreadsheetView.class, getCurrentController(document)).getActiveSheet();
    }

    public static void setActiveSheet(final XSpreadsheetDocument document, XSpreadsheet sheet) {
        UnoRuntime.queryInterface(XSpreadsheetView.class, getCurrentController(document)).setActiveSheet(sheet);
    }

    public static XSpreadsheet addSheet(final XSpreadsheetDocument document, final String sheetName) throws NoSuchElementException, WrappedTargetException {
        final XSpreadsheets sheets = document.getSheets();
        sheets.insertNewByName(sheetName, (short) sheets.getElementNames().length);
        return UnoRuntime.queryInterface(XSpreadsheet.class, sheets.getByName(sheetName));
    }

    public static void freezeColumnsOfActiveSheet(final XSpreadsheetDocument document, final int columns) {
        final int rows = 0;
        UnoRuntime.queryInterface(XViewFreezable.class, getCurrentController(document)).freezeAtPosition(columns, rows);
    }

    public static void freezeRowsOfActiveSheet(final XSpreadsheetDocument document, final int rows) {
        final int columns = 0;
        UnoRuntime.queryInterface(XViewFreezable.class, getCurrentController(document)).freezeAtPosition(columns, rows);
    }

    public static int addNumberFormatCode(final XSpreadsheetDocument document, final String formatCode) throws MalformedNumberFormatException {
        final XNumberFormatsSupplier numberFormatsSupplier = UnoRuntime.queryInterface(XNumberFormatsSupplier.class, document);
        final XNumberFormats numberFormats = numberFormatsSupplier.getNumberFormats();
        return numberFormats.addNew(formatCode, locale);
    }

    public static int queryNumberFormatCode(final XSpreadsheetDocument document, final String formatCode) {
        final XNumberFormatsSupplier numberFormatsSupplier = UnoRuntime.queryInterface(XNumberFormatsSupplier.class, document);
        final XNumberFormats numberFormats = numberFormatsSupplier.getNumberFormats();
        return numberFormats.queryKey(formatCode, locale, false);
    }

    private static XController getCurrentController(final XSpreadsheetDocument document) {
        return UnoRuntime.queryInterface(XModel.class, document).getCurrentController();
    }

    private static XNumberFormatTypes getNumberFormatTypes(final XSpreadsheetDocument document) {
        final XNumberFormatsSupplier numberFormatsSupplier = UnoRuntime.queryInterface(XNumberFormatsSupplier.class, document);
        return UnoRuntime.queryInterface(XNumberFormatTypes.class, numberFormatsSupplier.getNumberFormats());
    }
}
