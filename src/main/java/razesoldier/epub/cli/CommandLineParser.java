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

class CommandLineParser {
    @NotNull
    CommandLine parse(@NotNull Options options, @NotNull String[] arguments) {
        String context = null;
        String argName = null;
        CommandLine commandLine = new CommandLine();
        for (String argument : arguments) {
            // Handle value for a option
            if (context != null) {
                if (context.equals("long")) {
                    Option opt = options.getOptionByLong(argName);
                    if (opt == null) {
                        context = null;
                        argName = null;
                        continue;
                    }
                    if (opt.isWithValue()) {
                        commandLine.addOption(opt, argument);
                    } else {
                        commandLine.addOption(opt, null);
                    }
                }
                if (context.equals("short")) {
                    Option opt = options.getOptionByShort(argName);
                    if (opt == null) {
                        context = null;
                        argName = null;
                        continue;
                    }
                    if (opt.isWithValue()) {
                        commandLine.addOption(opt, argument);
                    } else {
                        commandLine.addOption(opt, null);
                    }
                }
                context = null;
                argName = null;
            }
            // Handle long option
            if (argument.indexOf("--") == 0) {
                context = "long";
                argName = argument.substring(2);
                continue;
            }
            // Handle short option
            if (argument.indexOf("-") == 0) {
                context = "short";
                argName = argument.substring(1);
            }
        }
        // Prevent loss of unhandled context
        if (context != null) {
            Option opt;
            if (context.equals("short")) {
                opt = options.getOptionByShort(argName);
            } else {
                opt = options.getOptionByLong(argName);
            }
            if (opt != null) {
                commandLine.addOption(opt, null);
            }
        }
        return commandLine;
    }
}
