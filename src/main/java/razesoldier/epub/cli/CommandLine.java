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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

class CommandLine {
    private Map<Option, String> optionMap = new LinkedHashMap<>();

    void addOption(@NotNull Option option, @Nullable String value) {
        optionMap.put(option, value);
    }

    boolean hasOptionByShort(@NotNull String shortOpt) {
        for (Map.Entry<Option, String> entry : optionMap.entrySet()) {
            String opt = entry.getKey().getShortOpt();
            if (opt != null && opt.equals(shortOpt)) {
                return true;
            }
        }
        return false;
    }

    boolean hasOptionByLong(@NotNull String longOpt) {
        for (Map.Entry<Option, String> entry : optionMap.entrySet()) {
            String opt = entry.getKey().getLongOpt();
            if (opt != null && opt.equals(longOpt)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    String getOptionValue(@NotNull String opt) {
        for (Map.Entry<Option, String> entry : optionMap.entrySet()) {
            String shortOpt = entry.getKey().getShortOpt();
            String longOpt = entry.getKey().getLongOpt();
            if ((shortOpt != null && shortOpt.equals(opt)) || (longOpt != null && longOpt.equals(opt))) {
                return entry.getValue();
            }
        }
        return null;
    }
}
