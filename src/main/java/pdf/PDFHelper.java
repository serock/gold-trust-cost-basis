// SPDX-License-Identifier: MIT
package pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFHelper implements Consumer<PDPage> {

    private int endPage = 0;
    private List<Set<String>> fontTypes;
    private int pageCount = 0;
    private boolean sortByPosition = true;
    private int startPage = 0;
    private String text;

    @Override
    public void accept(final PDPage page) {
        final PDResources resources = page.getResources();
        final Iterable<COSName> fontNames = resources.getFontNames();
        final Set<String> fontTypesOnPage = new HashSet<>();
        for (COSName fontName : fontNames) {
            try {
                final PDFont font = resources.getFont(fontName);
                if (font != null) {
                    fontTypesOnPage.add(font.getSubType());
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        fontTypes().add(fontTypesOnPage);
    }

    public void extractText(final File pdfFile) throws IOException {
        try (final PDDocument doc = Loader.loadPDF(pdfFile)) {
            final PDPageTree pageTree = doc.getDocumentCatalog().getPages();
            this.pageCount = pageTree.getCount();
            final PDFTextStripper pdfTextStripper = new PDFTextStripper();
            if (startPage() > 0) {
                pdfTextStripper.setStartPage(startPage());
            }
            if (endPage() > 0) {
                pdfTextStripper.setEndPage(endPage());
            }
            pdfTextStripper.setPageEnd("\n\f");
            getFontTypes(pageTree);
            if (hasType3Font()) {
                System.err.println("Error: Type 3 font found in PDF; use OCR for text extraction");
                System.exit(2);
            }
            pdfTextStripper.setSortByPosition(sortByPosition());
            setText(pdfTextStripper.getText(doc));
        }
    }

    public Stream<String> getTextLines() {
        return text().lines();
    }

    public void setEndPage(final int page) {
        this.endPage = page;
    }

    public void setSortByPosition(boolean sort) {
        this.sortByPosition = sort;
    }

    public void setStartPage(final int page) {
        this.startPage = page;
    }

    private void getFontTypes(final PDPageTree pageTree) {
        this.fontTypes = new ArrayList<>(pageTree.getCount());
        pageTree.forEach(this);
    }

    private boolean hasType3Font() {
        final int start = Math.max(startPage(), 1);
        final int end = Math.min(endPage(), pageCount());
        return fontTypes().stream().skip(start - 1).limit(end - start + 1).anyMatch(t -> t.contains("Type3"));
    }

    private String setText(final String t) {
        return this.text = t;
    }

    private int endPage() {
        return this.endPage;
    }

    private List<Set<String>> fontTypes() {
        return this.fontTypes;
    }

    private int pageCount() {
        return this.pageCount;
    }

    private boolean sortByPosition() {
        return this.sortByPosition;
    }

    private int startPage() {
        return this.startPage;
    }

    private String text() {
        return this.text;
    }
}
