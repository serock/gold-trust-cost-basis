// SPDX-License-Identifier: MIT
package pdf;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFHelper {

    private String documentTitle;
    private boolean sortByPosition = true;

    public Stream<String> getTextLines(final File pdfFile) throws IOException {
        final PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text;
        try (final PDDocument doc = Loader.loadPDF(pdfFile)) {
            this.documentTitle = doc.getDocumentInformation().getTitle();
            pdfTextStripper.setSortByPosition(sortByPosition());
            text = pdfTextStripper.getText(doc);
        }
        return text.lines();
    }

    public String documentTitle() {
        return this.documentTitle;
    }

    public void setSortByPosition(boolean sort) {
        this.sortByPosition = sort;
    }

    private boolean sortByPosition() {
        return this.sortByPosition;
    }
}
