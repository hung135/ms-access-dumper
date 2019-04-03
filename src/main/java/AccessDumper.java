import org.apache.commons.cli.*;

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

        Option input = new Option("f", "file", true, "input file path");
        input.setRequired(true);
        options.addOption(input);
        Option print_tables = new Option("p", "print_tables", false, "prints all tables");
        print_tables.setRequired(false);
        options.addOption(print_tables);
        Option regex = new Option("r", "regex", true, "regex pattern of table name");
        regex.setRequired(false);
        options.addOption(regex);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {

            cmd = parser.parse(options, args);
            String inputFilePath = cmd.getOptionValue("f");
            String regex1 = cmd.getOptionValue("r");
            String print_tbl = cmd.getOptionValue("p");
            System.out.println(inputFilePath);
            System.out.println(regex1);
            System.out.println(print_tbl);

            if (print_tbl != null){
                print_tables(inputFilePath);
                System.exit(0);
            }
            else {
                List<String> tableList;
                tableList=get_tables(inputFilePath,regex1);
                write_to_csv(inputFilePath,tableList);


            }

        } catch (Exception e) {
            System.out.println(e);
            System.out.println(formatter);
            System.exit(1);
        }



    }
    public static void write_to_csv(String filePath, List<String> tableNames){
        try {
            System.out.println(filePath);
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);
            Statement statement = conn.createStatement();
            File directory = new File(String.valueOf("./tmp/"));
            if(!directory.exists()){

                directory.mkdir();

            }

            for (String table : tableNames) {
                System.out.println(table);

                String query = "SELECT * FROM [" +table+"]";
                System.out.println(query);
                CSVWriter writer = new CSVWriter(new FileWriter("./tmp/"+table.replace(" ","_").toLowerCase()+".csv"), ',');

                Boolean includeHeaders = true;

                java.sql.ResultSet myResultSet =  statement.executeQuery(query);

                writer.writeAll(myResultSet, includeHeaders);

                writer.close();


            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    public static List<String> get_tables(String filePath, String tableRegex){
        List<String> tableList = new ArrayList<String>();
        if (tableRegex==null){
            // do nodthing

        }

        try {
            System.out.println(filePath);
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);

            ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null);

            int i = 0;

            while (rsMD.next()) {
                String tableName = rsMD.getString("TABLE_NAME");
                tableList.add(tableName);
                i++;


            }
            System.out.println("Total" + i);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tableList;
    }
    public static void print_tables(String filePath) throws SQLException {
        try {
            System.out.println(filePath);
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);
            /*
             * Each table description has the following columns:
             *
             * TABLE_CAT String => table catalog (may be null) TABLE_SCHEM String => table
             * schema (may be null) TABLE_NAME String => table name TABLE_TYPE String =>
             * table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE",
             * "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". REMARKS String =>
             * explanatory comment on the table TYPE_CAT String => the types catalog (may be
             * null) TYPE_SCHEM String => the types schema (may be null) TYPE_NAME String =>
             * type name (may be null) SELF_REFERENCING_COL_NAME String => name of the
             * designated "identifier" column of a typed table (may be null) REF_GENERATION
             * String => specifies how values in SELF_REFERENCING_COL_NAME are created.
             * Values are "SYSTEM", "USER", "DERIVED". (may be null)
             */

                ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null);

                int i = 0;

                while (rsMD.next()) {

                    System.out.println(rsMD.getString("TABLE_NAME"));
                    i++;

                }
                System.out.println("Total" + i);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
