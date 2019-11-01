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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to parse the NCX file (Navigation Control file for XML).
 */
class NCXParser {
    private Document document;
    private EPUBBook.NCX ncx;

    NCXParser(@NotNull InputStream is) throws ParserConfigurationException, SAXException, ZipReadException {
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        } catch (IOException e) {
            throw new ZipReadException(e);
        }
    }

    EPUBBook.NCX parse() {
        ncx = new EPUBBook.NCX();
        parseHead();
        parseTitle();
        parseAuthor();
        parseNavMap();
        return ncx;
    }

    private void parseHead() {
        NodeList headNodes = document.getElementsByTagName("head").item(0).getChildNodes();
        Map<String, String> metadata = new HashMap<>();
        for (int i = 0; i < headNodes.getLength(); ++i) {
            Node node = headNodes.item(i);
            if (!(node instanceof Element) || !node.getNodeName().equals("meta")) {
                continue;
            }
            metadata.put(((Element) node).getAttribute("name"), ((Element) node).getAttribute("content"));
        }

        ncx.setMetadata(metadata);
    }

    private void parseTitle() {
        Element element = (Element) document.getElementsByTagName("docTitle").item(0);
        if (element == null) {
            return;
        }
        ncx.setTitle(element.getElementsByTagName("text").item(0).getTextContent());
    }

    private void parseAuthor() {
        Element element = (Element) document.getElementsByTagName("docAuthor").item(0);
        if (element == null) {
            return;
        }
        ncx.setAuthor(element.getElementsByTagName("text").item(0).getTextContent());
    }

    private void parseNavMap() {
        NodeList navMap = document.getElementsByTagName("navMap").item(0).getChildNodes();
        Map<String, EPUBBook.NCX.NavPoint> navPointMap = new HashMap<>();
        for (int i = 0; i < navMap.getLength(); ++i) {
            Node node = navMap.item(i);
            if (!(node instanceof Element) || !node.getNodeName().equals("navPoint")) {
                continue;
            }
            Element element = (Element) node;
            String id = element.getAttribute("id");
            Integer order = Integer.valueOf(element.getAttribute("playOrder"));
            String text = element.getElementsByTagName("text").item(0).getTextContent();
            String ref = ((Element) element.getElementsByTagName("content").item(0)).getAttribute("src");
            navPointMap.put(id, new EPUBBook.NCX.NavPoint(order, id, text, ref));
        }

        ncx.setNavPointMap(navPointMap);
    }
}
