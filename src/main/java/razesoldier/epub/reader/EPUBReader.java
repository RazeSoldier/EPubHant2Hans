/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */

package razesoldier.epub.reader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;

/**
 * Used to read a EPUB file.
 */
public class EPUBReader implements Closeable {
    private EPUBBook epubBook;
    private String resourcePath;
    private FileSystem fileSystem;

    public EPUBReader(@NotNull String path) throws InitException {
        try {
            fileSystem = FileSystems.newFileSystem(Paths.get(new File(path).toURI()), (ClassLoader) null);
        } catch (IOException e) {
            throw new InitException(e);
        }
        String checkResult = checkZipFile();
        if (checkResult != null) {
            throw new InitException(checkResult);
        }
        initBook();
    }

    /**
     * Check if the zip file meets the EPUB standard.
     */
    @Nullable
    private String checkZipFile() {
        String mimeType;
        try {
            mimeType = readFile("mimetype");
        } catch (ZipEntryNotFoundException e) {
            return "mimetype file not found";
        } catch (ZipReadException e) {
            return e.getCause().getMessage();
        }
        if (!mimeType.contains("application/epub+zip")) {
            return "Invalid mimetype file";
        }

        if (entryNoExists("META-INF/container.xml")) {
            return "META-INF/container.xml file not found";
        }
        return null;
    }

    /**
     * Initialize the EPUBBook instance.
     */
    private void initBook() throws InitException {
        InputStream containerStream;
        try {
            containerStream = readFileWithStream("META-INF/container.xml");
        } catch (ZipReadException | ZipEntryNotFoundException e) {
            throw new InitException(e);
        }

        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(containerStream);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new InitException(e);
        }
        // Get OPF file path from the container file
        String opfPath = document.getElementsByTagName("rootfile").item(0).getAttributes().
                getNamedItem("full-path").getTextContent();
        try {
            OPFParser opfParser = new OPFParser(readFileWithStream(opfPath), opfPath);
            epubBook = opfParser.parse();
            resourcePath = opfParser.getResourcePath();
        } catch (ParserConfigurationException | IOException | SAXException | ZipReadException | ZipEntryNotFoundException e) {
            throw new InitException(e);
        }

        // Handle .ncx file
        NCXParser ncxParser;
        try {
            ncxParser = new NCXParser(readFileWithStream(epubBook.getSpines().getSpineFilePath()));
        } catch (ParserConfigurationException | ZipEntryNotFoundException | SAXException | ZipReadException e) {
            throw new InitException(e);
        }
        epubBook.setNcx(ncxParser.parse());
    }


    private boolean entryNoExists(@NotNull String entry) {
        Path path = fileSystem.getPath(entry);
        return !Files.exists(path);
    }

    public EPUBBook getBook() {
        return epubBook;
    }

    private String readFile(@NotNull String entry) throws ZipReadException, ZipEntryNotFoundException {
        if (entryNoExists(entry)) {
            throw new ZipEntryNotFoundException(entry);
        }
        Path path = fileSystem.getPath(entry);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new ZipReadException(e);
        }
    }

    @NotNull
    public InputStream readFileWithStream(@NotNull String filepath) throws ZipReadException, ZipEntryNotFoundException {
        if (entryNoExists(filepath)) {
            throw new ZipEntryNotFoundException(filepath);
        }
        Path path = fileSystem.getPath(filepath);
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new ZipReadException(e);
        }
    }

    public String readManifest(@NotNull String filepath) throws ZipEntryNotFoundException, ZipReadException {
        return readFile(resourcePath + "/" + filepath);
    }

    public void writeFile(@NotNull String dstPath, @NotNull String newText) throws IOException {
        Path path = fileSystem.getPath(dstPath);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(newText);
            writer.flush();
        }
    }

    public void writeManifest(@NotNull String dstPath, @NotNull String newText) throws IOException {
        writeFile(resourcePath + "/" + dstPath, newText);
    }

    public void close() throws IOException {
        fileSystem.close();
    }
}
