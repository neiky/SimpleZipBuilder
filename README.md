# SimpleZipBuilder
Simply create zip files and add files and folders to it.

## Usage
1. Initiate a new zip file using the SimpleZipBuilder:
    ```SimpleZipBuilder zipBuilder = new SimpleZipBuilder (new File("archive.zip"));```
2. Add files and folders to it:
    ```zipBuilder.addFilesToZip (myFileObject);```
3. Build the zip file:
    ```File zipFile = zipBuiler.buildZipFile();```
