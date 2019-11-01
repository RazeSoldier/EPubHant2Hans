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

/**
 * The entry for Cli.
 */
public class CliEntry {
    private static final String helpMsg;
    private static final Options commandOpts;

    static {
        commandOpts = new Options();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Usage: epubconverter <--command Command>\n");
        commandOpts.addOption(null, "command", true, "The command to execute");
        commandOpts.addOption(null, "debug", false, "Turn on debug mode");
        commandOpts.getOptions().forEach(option -> {
            stringBuilder.append("\t");
            if (option.getShortOpt() == null) {
                stringBuilder.append("--").append(option.getLongOpt()).append("\t\t").append(option.getDescription()).append("\n");
            }
        });
        helpMsg = stringBuilder.toString();
    }

    public static void main(String[] args) {
        CommandLineParser commandLineParser = new CommandLineParser();
        CommandLine commandLine;
        commandLine = commandLineParser.parse(commandOpts, args);
        Context context = new Context(args);

        String commandName = commandLine.getOptionValue("command");
        if (commandName == null) {
            System.out.println("Missing required argument: --command");
            System.out.println(helpMsg);
            return;
        }

        // Command routing
        Command command = CommandFactory.newCommand(commandName, context);
        if (command == null) {
            System.out.println("Unknown command: " + commandName);
            return;
        }
        try {
            command.execute();
        } catch (ExecuteException e) {
            if (commandLine.hasOptionByLong("debug")) {
                e.printStackTrace();
            } else {
                System.out.println(e.getMessage());
            }
        }
    }
}
