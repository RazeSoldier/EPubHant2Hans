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

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EPUBReaderTest {
    private EPUBReader ePubReader;

    private EPUBReader getReader() throws URISyntaxException, InitException {
        if (ePubReader != null) {
            return ePubReader;
        }
        String filename = "The Seventy's Course in Theology, Third Year by B. H. Roberts.epub";
        ePubReader = new EPUBReader(getClass().getResource("/" + filename).toURI().getPath());
        return ePubReader;
    }

    @Test
    void testBookName() throws URISyntaxException, InitException {
        assertEquals("The Seventy's Course in Theology (Third Year) / The Doctrine of Deity",
                getReader().getBook().getBookName());
    }
}
