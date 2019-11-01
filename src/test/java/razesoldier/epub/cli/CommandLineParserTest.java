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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineParserTest {
    @Test
    void testSimpleParse() {
        String[] args = {"--command", "test", "--debug", "--srcPath", "/srv"};
        Options options = new Options();
        options.addOption(null, "command", true, null);
        options.addOption(null, "debug", false, null);
        options.addOption(null, "srcPath", true, null);
        CommandLine commandLine = new CommandLineParser().parse(options, args);
        assertEquals("test", commandLine.getOptionValue("command"));
        assertTrue(commandLine.hasOptionByLong("debug"));
        assertNull(commandLine.getOptionValue("debug"));
        assertEquals("/srv", commandLine.getOptionValue("srcPath"));
    }

    @Test
    void testComplexParse() {
        String[] args = {"--command", "test", "--debug", "--srcPath", "/srv", "-g", "-s", "ok"};
        Options options = new Options();
        options.addOption(null, "command", true, null);
        options.addOption(null, "srcPath", true, null);
        options.addOption("s", null, true, null);
        options.addOption("g", null, false, null);
        CommandLine commandLine = new CommandLineParser().parse(options, args);
        assertEquals("test", commandLine.getOptionValue("command"));
        assertFalse(commandLine.hasOptionByLong("debug"));
        assertNull(commandLine.getOptionValue("debug"));
        assertEquals("/srv", commandLine.getOptionValue("srcPath"));
        assertEquals("ok", commandLine.getOptionValue("s"));
        assertTrue(commandLine.hasOptionByShort("g"));
        assertNull(commandLine.getOptionValue("g"));
    }
}
