import org.apache.commons.cli.*;

public class AccessDumper {
    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option input = new Option("f", "file", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option regex = new Option("r", "regex", true, "regex pattern of tablename");
        regex.setRequired(false);
        options.addOption(regex);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;


        cmd = parser.parse(options, args);


        String inputFilePath = cmd.getOptionValue("f");
        String regex1 = cmd.getOptionValue("r");

        System.out.println(inputFilePath);
        System.out.println(regex1);

    }
}
