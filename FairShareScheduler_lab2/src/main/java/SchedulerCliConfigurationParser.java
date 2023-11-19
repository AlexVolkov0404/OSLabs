import org.apache.commons.cli.*;

public class SchedulerCliConfigurationParser {
    public SchedulerProcessConfiguration parseConfigurationFromArgs(String[] args) {
        SchedulerProcessConfiguration config = new SchedulerProcessConfiguration();

        CommandLine cli = parseArgs(args);

        int priority = Integer.parseInt(cli.getOptionValue("priority"));
        config.setDefaultProcessPriority(priority);

        parseFilesWithGroups(config, cli.getOptionValues("files"));

        return config;
    }

    private void parseFilesWithGroups(SchedulerProcessConfiguration config, String[] files) {
        for (String file : files) {
            String[] parts = file.split(":");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid file name \"" + file + "\" given. Expected  {filename}:{group #}.");
            }

            config.addFile(parts[0], Integer.parseInt(parts[1]));
        }
    }

    private CommandLine parseArgs(String[] args) {
        Options options = initializeParserOptions();

        CommandLineParser parser = new DefaultParser();

        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        return null;
    }

    private Options initializeParserOptions() {
        Options options = new Options();

        Option priorityArg = new Option("p", "priority", true, "The default priority for all processes.");
        priorityArg.setRequired(true);
        options.addOption(priorityArg);

        Option processesArg = new Option("f", "files", true, "A list of processes (pexe files) to be executed, followed by " +
                "the group id. (i.e filename.pexe:5 filename2:3 ...).");
        processesArg.setRequired(true);
        processesArg.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(processesArg);

        return options;
    }
}
