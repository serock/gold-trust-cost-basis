// SPDX-License-Identifier: MIT
package app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.sheet.XSpreadsheetDocument;

import pdf.PDFHelper;
import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.tax.GrossProceedsSheetBuilder;
import text.Context;

public class GoldTrustCostBasis implements Consumer<String>, Runnable {

    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("app.debug", "false"));

    private final Context context = new Context();

    private File taxDataFile;

    @Override
    public void accept(final String text) {
        if (DEBUG) {
            System.out.println(text);
        }
        context().state().accept(context(), text);
    }

    public static void main(final String[] args) {
        checkClassPath();
        if (args.length != 1) {
            showUsage();
            System.exit(1);
        }
        final GoldTrustCostBasis app = new GoldTrustCostBasis();
        app.taxDataFile = new File(args[0]);
        if (!app.taxDataFile.canRead()) {
            System.err.println("Cannot read " + app.taxDataFile.getAbsolutePath());
            System.exit(2);
        }
        app.run();
    }

    @Override
    public void run() {
        try (Stream<String> lines = getLines()) {
            lines.forEachOrdered(this);
            final SpreadsheetDocumentHelper docHelper = new SpreadsheetDocumentHelper();
            final XSpreadsheetDocument document = docHelper.createDocument();
            buildGrossProceedsSheet(document);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void buildGrossProceedsSheet(final XSpreadsheetDocument document) throws IllegalArgumentException, com.sun.star.uno.Exception {
        final GrossProceedsSheetBuilder builder = new GrossProceedsSheetBuilder();
        builder.setDocument(document);
        builder.setSheetFormulas(context().getGrossProceedsFormulas());
        builder.build();
    }

    private static void checkClassPath() {
        try {
            Class.forName("com.sun.star.comp.helper.Bootstrap");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private Context context() {
        return this.context;
    }

    private Stream<String> getLines() throws IOException {
        Stream<String> lines = null;
        if (this.taxDataFile.getName().endsWith(".pdf")) {
            final PDFHelper pdfHelper = new PDFHelper();
            lines = pdfHelper.getTextLines(this.taxDataFile);
        } else {
            lines = Files.lines(this.taxDataFile.toPath());
        }
        return lines;
    }

    private static void showUsage() {
        System.out.println("Usage: java -jar gold-trust-cost-basis.jar <gold-tax-data-pdf-or-txt-file>");
    }
}
