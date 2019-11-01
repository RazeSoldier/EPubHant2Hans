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

import java.util.ArrayList;
import java.util.List;

class Options {
    private List<Option> recognizedOption = new ArrayList<>();

    void addOption(String shortOpt, String longOpt, boolean withValue, String description) {
        recognizedOption.add(new Option(shortOpt, longOpt, withValue, description));
    }

    @NotNull
    List<Option> getOptions() {
        return recognizedOption;
    }

    @Nullable
    Option getOptionByShort(@NotNull String shortOpt) {
        for (Option option : recognizedOption) {
            String shortOptValue = option.getShortOpt();
            if (shortOptValue != null && shortOptValue.equals(shortOpt)) {
                return option;
            }
        }
        return null;
    }

    @Nullable
    Option getOptionByLong(@NotNull String longOpt) {
        for (Option option : recognizedOption) {
            String longOptValue = option.getLongOpt();
            if (longOptValue != null && longOptValue.equals(longOpt)) {
                return option;
            }
        }
        return null;
    }
}
