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

package razesoldier.epub.cli;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;
import razesoldier.epub.hant2hans.VariantConverter;
import razesoldier.epub.hant2hans.VariantConverterFactory;
import razesoldier.epub.reader.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ZhHantToHansCommand implements Command {
    private CommandLine commandLine;
    private VariantConverter variantConverter;

    @Contract(pure = true)
    ZhHantToHansCommand(@NotNull Context context) {
        Options options = new Options();
        options.addOption(null, "srcPath", true, null);
        commandLine = new CommandLineParser().parse(options, context.getCommandArgs());
        variantConverter = VariantConverterFactory.newConverter();
    }

    public void execute() throws ExecuteException {
        String srcPath = commandLine.getOptionValue("srcPath");
        if (srcPath == null) {
            throw new ExecuteException("Missing required option: --srcPath");
        }

        try (EPUBReader epubReader = new EPUBReader(srcPath)) {
            EPUBBook epubBook = epubReader.getBook();
            // Use multithreading in batch operations that convert Manifest.
            // Since the IO target of each task are different,
            // we don't have to worry about resource sharing conflicts.
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            for (Map.Entry<String, EPUBBook.Manifest> entry : epubBook.getManifests().entrySet()) {
                EPUBBook.Manifest manifest = entry.getValue();
                if (!manifest.getMediaType().equals("application/xhtml+xml")) {
                    continue;
                }
                executorService.execute(new ConvertTask(manifest, epubReader, variantConverter));
            }
            executorService.shutdown();

            String ncxText = handleNCX(epubReader.readFileWithStream(epubBook.getSpines().getSpineFilePath()));
            epubReader.writeFile(epubBook.getSpines().getSpineFilePath(), ncxText);

            executorService.awaitTermination(1, TimeUnit.MINUTES); // TODO: For large EPUB files, it may time out
        } catch (InitException | IOException | ZipReadException | ZipEntryNotFoundException | ParserConfigurationException
                | SAXException | InterruptedException e) {
            throw new ExecuteException(e);
        }
    }

    private String handleNCX(@NotNull InputStream is) throws ParserConfigurationException, IOException, SAXException {
        org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        NodeList nodeList = document.getElementsByTagName("text");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            org.w3c.dom.Element element = (org.w3c.dom.Element) nodeList.item(i);
            org.w3c.dom.Element newElement = document.createElement("text");
            newElement.setTextContent(variantConverter.convert(element.getTextContent()));
            element.getParentNode().replaceChild(newElement, element);
        }

        // Following code refer <Core Java Volume Ⅱ--Advanced Features (10th Edition)>
        // TODO: Run here for a long time, should consider performance issues
        DOMImplementation impl = document.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer serializer = implLS.createLSSerializer();
        serializer.getDomConfig().setParameter("format-pretty-print", true);
        return serializer.writeToString(document);
    }

    private static class ConvertTask implements Runnable {
        private EPUBBook.Manifest manifest;
        private EPUBReader reader;
        private VariantConverter variantConverter;

        @Contract(pure = true)
        private ConvertTask(@NotNull EPUBBook.Manifest manifest, @NotNull EPUBReader reader, @NotNull VariantConverter variantConverter) {
            this.manifest = manifest;
            this.reader = reader;
            this.variantConverter = variantConverter;
        }

        public void run() {
            try {
                String text = reader.readManifest(manifest.getFilePath());
                reader.writeManifest(manifest.getFilePath(), doConvert(text));
            } catch (ZipEntryNotFoundException | ZipReadException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        private String doConvert(@NotNull String text) {
            Document document = Jsoup.parse(text);
            Element div = document.body().getElementsByTag("div").get(0);
            Elements pList = div.children();

            pList.forEach(element -> {
                if (!element.nodeName().equals("p")) {
                    return;
                }
                element.text(variantConverter.convert(element.text())); // Do zh-hant to zh-hans
            });

            return document.html();
        }
    }
}
