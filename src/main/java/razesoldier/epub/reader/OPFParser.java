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
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser that can parse a Open Packaging Format file (.opf).
 * Using {@link #parse()} to get the {@link EPUBBook} instance.
 */
class OPFParser {
    private Document document;
    private EPUBBook book;
    private String resourcePath;

    OPFParser(@NotNull InputStream inputStream, @NotNull String opfFilePath) throws ParserConfigurationException, IOException, SAXException {
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        resourcePath = new File(opfFilePath).getParent();
        if (resourcePath == null) {
            resourcePath = "/";
        } else {
            resourcePath = "/" + resourcePath;
        }
    }

    EPUBBook parse() {
        book = new EPUBBook();
        readMetadata();
        readManifest();
        readSpine();
        return book;
    }

    @NotNull
    String getResourcePath() {
        return resourcePath;
    }

    private void readMetadata() {
        // Get title meta @{
        String title = getMetadataFromDOM("dc:title", "");
        // @}

        // Get language @{
        String language = getMetadataFromDOM("dc:language", "en");
        // @}

        // Get all metadata @{
        NodeList metadata = document.getElementsByTagName("metadata").item(0).getChildNodes();
        Map<String, String> metaMap = new HashMap<>();
        for (int i = 0; i < metadata.getLength(); ++i) {
            Node node = metadata.item(i);
            if (node.getNodeName().contains("dc:")) {
                String metaName = node.getNodeName().replaceFirst("dc:", "");
                metaMap.put(metaName, node.getTextContent());
            }
        }
        // @}
        book.setMetadata(title, language, metaMap);
    }

    private void readManifest() {
        NodeList manifests = document.getElementsByTagName("manifest").item(0).getChildNodes();
        Map<String, EPUBBook.Manifest> manifestMap = new HashMap<>();
        for (int i = 0; i < manifests.getLength(); ++i) {
            Node node = manifests.item(i);
            if (!node.getNodeName().equals("item")) {
                continue;
            }
            NamedNodeMap attributes = node.getAttributes();
            String id = attributes.getNamedItem("id").getTextContent();
            String filePath = attributes.getNamedItem("href").getTextContent();
            String mediaType = attributes.getNamedItem("media-type").getTextContent();
            manifestMap.put(id, new EPUBBook.Manifest(id, filePath, mediaType));
        }
        book.setManifests(manifestMap);
    }

    private void readSpine() {
        Element spineNode = (Element) document.getElementsByTagName("spine").item(0);
        String spineManifestId = spineNode.getAttribute("toc");
        NodeList spines = spineNode.getChildNodes();
        List<String> spineList = new ArrayList<>();
        for (int i = 0; i < spines.getLength(); ++i) {
            Node node = spines.item(i);
            if (!node.getNodeName().equals("itemref")) {
                continue;
            }
            spineList.add(node.getAttributes().getNamedItem("idref").getTextContent());
        }
        book.setSpines(
                new EPUBBook.Spines(resourcePath + "/" + book.getManifests().get(spineManifestId).getFilePath(),
                        spineList)
        );
    }

    private String getMetadataFromDOM(@NotNull String nodeName, @NotNull String defaultValue) {
        NodeList nodeList = document.getElementsByTagName(nodeName);
        String result = null;
        if (nodeList.getLength() != 0) {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                Node parent = node.getParentNode();
                if (parent != null) {
                    if (parent.getNodeName().equals("metadata")) {
                        result = node.getTextContent();
                        break;
                    }
                }
            }
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
}
