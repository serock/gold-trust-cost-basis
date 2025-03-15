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

### Tax Years 2011 Through 2023
**Linux example:**

```Shell
cd ~/Downloads
wget https://www.spdrgoldshares.com/media/GLD/file/SPDR-Gold-Trust-Tax-Information-2023.pdf
java -jar gold-trust-cost-basis-2024.0.0.jar SPDR-Gold-Trust-Tax-Information-2023.pdf
```

**Windows Command Prompt example:**

```Batchfile
cd %USERPROFILE%\Downloads
curl -s -o SPDR-Gold-Trust-Tax-Information-2023.pdf ^
  https://www.spdrgoldshares.com/media/GLD/file/SPDR-Gold-Trust-Tax-Information-2023.pdf
java -jar gold-trust-cost-basis-2024.0.0.jar SPDR-Gold-Trust-Tax-Information-2023.pdf
```

### Tax Year 2024
Because Type 3 fonts are used in the `SPDR-Gold-Trust-Tax-Information-2024.pdf`, convert the PDF pages to images and use OCR to extract text from the PDF.
See the [How to Configure Tesseract OCR](/../../wiki/How-to-Configure-Tesseract-OCR) page in the Wiki.

:warning: The examples below expect the `pdfbox-app-3.0.4.jar` to be in your `Downloads` directory.

**Linux example:**

```Shell
cd ~/Downloads
wget https://www.spdrgoldshares.com/media/GLD/file/SPDR-Gold-Trust-Tax-Information-2024.pdf
java -jar pdfbox-app-3.0.4.jar render -color=GRAY -dpi 300 -format png \
     -startPage=3 -endPage=10 \
     -i=SPDR-Gold-Trust-Tax-Information-2024.pdf
ls -rt1 SPDR-Gold-Trust-Tax-Information-2024-*.png | \
tesseract - - -l eng --tessdata-dir ~/tessdata gld | \
java -jar ~/gold-trust-cost-basis-2024.0.0/gold-trust-cost-basis-2024.0.0.jar -
rm SPDR-Gold-Trust-Tax-Information-2024-*.png
```

**Windows Command Prompt example:**

:warning: This example assumes that *Tesseract OCR* was installed for the current user only, not for all users.

```Batchfile
cd %USERPROFILE%\Downloads
curl -s -o SPDR-Gold-Trust-Tax-Information-2024.pdf ^
  https://www.spdrgoldshares.com/media/GLD/file/SPDR-Gold-Trust-Tax-Information-2024.pdf
java -jar pdfbox-app-3.0.4.jar render -color=GRAY -dpi 300 -format png ^
     -startPage=3 -endPage=10 ^
     -i=SPDR-Gold-Trust-Tax-Information-2024.pdf
dir /b /o:d SPDR-Gold-Trust-Tax-Information-2024-*.png | ^
%LOCALAPPDATA%\Programs\Tesseract-OCR\tesseract.exe - - ^
  -l eng --tessdata-dir %USERPROFILE%\tessdata gld | ^
java -jar %USERPROFILE%\gold-trust-cost-basis-2024.0.0\gold-trust-cost-basis-2024.0.0.jar -
del SPDR-Gold-Trust-Tax-Information-2024-*.png
```

Note that the app does not save the spreadsheet. It is up to the user to decide whether or not to save the spreadsheet.
