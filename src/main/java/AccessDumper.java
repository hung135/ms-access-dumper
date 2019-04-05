import org.apache.commons.cli.*;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;

import java.util.List;

import com.opencsv.CSVWriter;

import java.io.*;

public class AccessDumper {
    public static void main(String[] args) throws Exception {


        argParser(args);

    }


    public static void argParser(String[] args) throws InterruptedException {
        Options options = new Options();

        Option f = new Option("f", "file", true, "input file path");
        Option h = new Option("h", "help", false, "show this help screen");
        Option d = new Option("d", "dir", true, "output directory path");
        Option p = new Option("p", "print_tables", false, "prints all tables");
        Option r = new Option("r", "regex", true, "regex pattern of table name");
        Option c = new Option("c", "clean_column", false, "clean up and lowercase column names");


        f.setRequired(true);
        p.setRequired(false);
        r.setRequired(false);
        h.setRequired(false);
        d.setRequired(false);
        c.setRequired(false);

        options.addOption(f);
        options.addOption(p);
        options.addOption(r);
        options.addOption(d);
        options.addOption(h);
        options.addOption(c);


        String header = "Dumps MSAccess to CSV files\n\n";
        String footer = "\nPlease report issues at https://github.com/hung135/ms-access-dumper/issues";
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {

            cmd = parser.parse(options, args);
            String inputFilePath = cmd.getOptionValue("f");
            String regex1 = cmd.getOptionValue("r");
            Boolean print_tbl = cmd.hasOption("p");
            Boolean help = cmd.hasOption("h");
            Boolean clean_columns = cmd.hasOption("c");

            String dir = cmd.getOptionValue("d");


            if (help) {
                formatter.printHelp("ms-access-dumper", header, options, footer, true);
                System.exit(0);
            }

            if (print_tbl) {
                print_tables(inputFilePath);
                System.exit(0);
            } else {
                if (dir == null) {
                    dir = ".";
                }
                List<String> tableList;
                tableList = get_tables(inputFilePath, regex1);
                write_to_csv(inputFilePath, tableList, dir, clean_columns);


            }

        } catch (Exception e) {
            System.out.println(e);


            formatter.printHelp("ms-access-dumper", header, options, footer, true);

            System.exit(1);
        }


    }

    public static void write_to_csv(String filePath, List<String> tableNames, String directory_path, Boolean clean_columns) {
        try {


            String dir_path = directory_path.replaceFirst("^~", System.getProperty("user.home"));
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);
            Statement statement = conn.createStatement();
            File directory = new File(String.valueOf(dir_path));
            if (!directory.exists()) {

                directory.mkdir();

            }

            for (String table : tableNames) {
                String fqn;

                if (clean_columns) {
                    fqn = dir_path + "/" + table.replace(" ", "_").toLowerCase() + ".csv";
                } else {
                    fqn = dir_path + "/" + table + ".csv";
                }
                File csv_file = new File(String.valueOf(fqn));
                String query = "SELECT * FROM [" + table + "]";
                System.out.println(query);


                CSVWriter writer = new CSVWriter(
                        new OutputStreamWriter(new FileOutputStream(csv_file), StandardCharsets.UTF_8),
                        ',',
                        CSVWriter.DEFAULT_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END
                );
                Boolean includeHeaders = true;

                java.sql.ResultSet myResultSet = statement.executeQuery(query);

                int x = writer.writeAll(myResultSet, includeHeaders);
                System.out.println(x + " Rows INTO -->\t" + csv_file.getCanonicalPath());

                writer.close();


            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public static List<String> get_tables(String filePath, String tableRegex) {
        List<String> tableList = new ArrayList<String>();
        if (tableRegex == null) {
            // do nothing

        }

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);

            ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null);

            int i = 0;

            while (rsMD.next()) {
                String tableName = rsMD.getString("TABLE_NAME");
                tableList.add(tableName);
                i++;


            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tableList;
    }

    public static void print_tables(String filePath) throws SQLException {
        try {

            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);

            ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null);

            int i = 0;

            while (rsMD.next()) {

                System.out.println(rsMD.getString("TABLE_NAME"));
                i++;

            }


        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
