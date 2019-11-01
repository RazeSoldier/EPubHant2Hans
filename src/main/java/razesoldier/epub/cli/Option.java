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
import org.jetbrains.annotations.Nullable;

class Option {
    private String shortOpt;
    private String longOpt;
    private boolean withValue;
    private String description;

    @Contract(pure = true)
    Option(String shortOpt, String longOpt, boolean withValue, String description) {
        this.shortOpt = shortOpt;
        this.longOpt = longOpt;
        this.withValue = withValue;
        this.description = description;
    }

    @Nullable
    String getShortOpt() {
        return shortOpt;
    }

    @Nullable
    String getLongOpt() {
        return longOpt;
    }

    boolean isWithValue() {
        return withValue;
    }

    @Nullable
    String getDescription() {
        return description;
    }
}
