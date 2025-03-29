# gold-trust-cost-basis
Create a LibreOffice Calc spreadsheet using data from a consolidated 1099 PDF tax form

## Overview
The main goal of the app is to calculate the cost basis for the monthly sales of gold that are reported on Form 1099-B.

The app pulls cost basis factor data from a consolidated 1099 PDF tax form into a LibreOffice Calc spreadsheet.
The app also creates an empty tax lots sheet to be populated by the user with info about the purchases and sales of the gold trust ETF shares.

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
2. Verify the integrity of the downloaded archive (e.g., on Linux, run `sha256sum -c gold-trust-cost-basis-2024.0.1-linux.tar.gz.sha256` from a terminal).
3. Extract the the files from the `.tar.gz` or `.zip` archive, keeping the directory structure that is in the archive.

### Option 2: Build the App
See [How to Build the App](/../../wiki/How-to-Build-the-App) in the Wiki.

### Test the App
Test the app by running the app without any arguments from a terminal. For example,

```Shell
java -jar gold-trust-cost-basis-2024.0.1.jar
```

If the app was packaged properly for the system, the app displays the following message:

```
Usage: java -jar gold-trust-cost-basis.jar <gold-tax-data-file>
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
When a consolidated 1099 PDF is passed to the app, the app will launch LibreOffice Calc and create multiple sheets.

**Linux example:**

```Shell
cd ~/Downloads
java -jar ~/gold-trust-cost-basis-2024.0.1/gold-trust-cost-basis-2024.0.1.jar \
  2024-form-1099-consolidated.pdf
```

**Windows Command Prompt example:**

```Batchfile
cd %USERPROFILE%\Downloads
java -jar ~/gold-trust-cost-basis-2024.0.1/gold-trust-cost-basis-2024.0.1.jar ^
  2024-form-1099-consolidated.pdf
```

Note that the app does not save the spreadsheet. It is up to the user to decide whether or not to save the spreadsheet.
