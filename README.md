# gold-trust-cost-basis
Create a LibreOffice Calc spreadsheet using data from a SPDR® Gold Trust Tax Information PDF

## Overview
The app is under development.
Currently, it puts the data from a SPDR® Gold Trust Tax Information PDF into a LibreOffice Calc spreadsheet.
The main goal of the app is to make it easier to calculate the cost basis for sales of gold to cover expenses that are reported on Form 1099-B.

## License
The `gold-trust-cost-basis` app is licensed under the [MIT License](/LICENSE).

## Installation Instructions
### Prerequisites
Before attempting to run the app, install the following software:

1. A build of OpenJDK 17 or higher, such as [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17) or [Amazon Corretto 17](https://aws.amazon.com/corretto/)
   * A JDK download can be used to build and/or run the app.
   * A JRE download is smaller and can be used to run the app, but not build it.
2. [LibreOffice](https://www.libreoffice.org/download/download-libreoffice/) (24.8.x is recommended)

For tax year 2024, install additional software:

1. Software that can convert PDF pages to images, like [PDFBox standalone](https://pdfbox.apache.org/download.html), [ImageMagick](https://imagemagick.org/script/download.php) with [Ghostscript](https://www.ghostscript.com/releases/gsdnld.html), or [GIMP](https://www.gimp.org/downloads/)
2. [Tesseract OCR](https://github.com/tesseract-ocr/tesseract?tab=readme-ov-file#tesseract-ocr)

### Option 1: Install a Build of the App
Builds of the app are available for Linux, MacOS, and Windows.

1. From the [Releases](/../../releases) page, download the `.tar.gz` or `.zip` distributable archive and the `.sha256` file for your operating system.
2. Verify the integrity of the downloaded archive (e.g., on Linux, run `sha256sum -c gold-trust-cost-basis-2024.0.0-linux.tar.gz.sha256` from a terminal).
3. Extract the the files from the `.tar.gz` or `.zip` archive, keeping the directory structure that is in the archive.

### Option 2: Build the App
See [How to Build the App](/../../wiki/How-to-Build-the-App) in the Wiki.

### Test the App
Test the app by running the app without any arguments from a terminal. For example,

```Shell
java -jar gold-trust-cost-basis-2024.0.0.jar
```

If the app was packaged properly for the system, the app displays the following message:

```
Usage: java -jar gold-trust-cost-basis.jar <gold-tax-data-pdf-or-txt-file>
```

However, if the app fails with a message like

```
java.lang.ClassNotFoundException: com.sun.star.comp.helper.Bootstrap
```

verify that the LibreOffice installation has a `libreoffice.jar` file.
If the file exists, then either the wrong distributable archive was installed or the app needs to be repackaged with the
correct absolute `file` URL of the `libreoffice.jar` file.
See the **Troubleshooting** section on the [How to Build the App](/../../wiki/How-to-Build-the-App) page in the Wiki.

## Running the App
To use the app, a SPDR® Gold Trust Tax Information file from [spdrgoldshares.com](https://www.spdrgoldshares.com/#tax) is required.
When a PDF is passed to the app, the app will attempt to extract the gross proceeds data and put the data into a LibreOffice Calc spreadsheet.
This is known to work for tax years 2011 through 2023.

On Linux, the app can be used like this:

```Shell
wget https://www.spdrgoldshares.com/media/GLD/file/SPDR-Gold-Trust-Tax-Information-2023.pdf
java -jar gold-trust-cost-basis-2024.0.0.jar SPDR-Gold-Trust-Tax-Information-2023.pdf
```

However, the app cannot extract text from the 2024 PDF and the app displays the following error message:

```
Error: Type 3 font found in PDF; use OCR for text extraction
```

Thus, a different approach can be used for the 2024 PDF:

1. Use *PDFBox standalone* to convert pages in the PDF to PNG images
2. Use *Tesseract OCR* to extract the text from the images
3. Use the app to put the gross proceeds data from the text into a *LibreOffice Calc* spreadsheet

**Note:** See the [How to configure Tesseract OCR](/../../wiki/How-to-Configure-Tesseract-OCR) page in the Wiki.

On Linux, the entire process might look like this:

```Shell
wget https://www.spdrgoldshares.com/media/GLD/file/SPDR-Gold-Trust-Tax-Information-2024.pdf
java -jar pdfbox-app-3.0.4.jar render -color=GRAY -dpi 300 -format png -startPage=3 -endPage=10 \
     -i=SPDR-Gold-Trust-Tax-Information-2024.pdf
ls -rt1 SPDR-Gold-Trust-Tax-Information-2024-*.png | \
tesseract - - -l eng --tessdata-dir ~/tessdata gld | \
java -jar gold-trust-cost-basis-2024.0.0.jar -
```

Note that the app does not save the spreadsheet. It is up to the user to decide whether or not to save the spreadsheet.