// SPDX-License-Identifier: MIT
package app;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.sheet.XSpreadsheetDocument;

import pdf.PDFHelper;
import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.tax.GrossProceedsSheetBuilder;
import spreadsheet.sheet.tax.TaxLotsSheetBuilder;
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
        final String taxDataFileName = args[0];
        if (!taxDataFileName.equals("-")) {
            app.taxDataFile = new File(taxDataFileName);
        }
        app.run();
        System.exit(0);
    }

    @Override
    public void run() {
        if (taxDataFile() != null && !taxDataFile().canRead()) {
            System.err.println("Error: Cannot read " + taxDataFile().getAbsolutePath());
            System.exit(2);
        }
        try (Stream<String> lines = getLines()) {
            lines.forEachOrdered(this);
            if (context().getGrossProceedsFormulas().length < 1) {
                System.err.println("Error: Could not read gross proceeds data from file");
                System.exit(2);
            }
            final SpreadsheetDocumentHelper docHelper = new SpreadsheetDocumentHelper();
            final XSpreadsheetDocument document = docHelper.createDocument();
            buildTaxLotsSheet(document);
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

    private void buildTaxLotsSheet(final XSpreadsheetDocument document) throws com.sun.star.uno.Exception {
        final TaxLotsSheetBuilder builder = new TaxLotsSheetBuilder();
        builder.setDocument(document);
        builder.setSheetFormulas(context().getTaxLotsFormulas());
        builder.build();
    }

    private static void checkClassPath() {
        try {
            Class.forName("com.sun.star.comp.helper.Bootstrap");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @SuppressWarnings("resource")
    private Stream<String> getLines() throws IOException {
        if (taxDataFile() == null) {
            final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            scanner.useDelimiter("\n");
            return scanner.tokens();
        }
        final String taxDataFileName = taxDataFile().getName();
        if (taxDataFileName.endsWith(".pdf")) {
            final PDFHelper pdfHelper = new PDFHelper();
            pdfHelper.setStartPage(3);
            pdfHelper.setEndPage(10);
            pdfHelper.extractText(taxDataFile());
            return pdfHelper.getTextLines();
        }
        return Files.lines(taxDataFile().toPath());
    }

    private Context context() {
        return this.context;
    }

    private File taxDataFile() {
        return this.taxDataFile;
    }

    private static void showUsage() {
        System.out.println("Usage: java -jar gold-trust-cost-basis.jar <gold-tax-data-pdf-or-txt-file>");
    }
}
