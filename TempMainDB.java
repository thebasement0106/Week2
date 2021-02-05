import java.io.*;
import java.sql.*;
import java.util.Scanner;

/**
 * This program reads information from a database and displays a user-friendly report based on the data
 *
 * Author: Jeff Ostler
 */

public class TempMainDB {
    private static String[][] temps;
    private static final File outFile = new File("C:\\Users\\Jeff\\Documents\\TemperaturesReportFromDB.txt");

    public static void main(String[] args) throws IOException {
        int[] maxHi;
        int[] minLo;
        int variance;

        // Your database connection information may be different depending on
        // your MySQL installation and the dbLogin and dbPassword you choose
        // to use in your database.
        String connectionString = "jdbc:mysql://127.0.0.1:3306/practice";
        String dbLogin = "root";
        String dbPassword = "slccroot";
        Connection conn = null;

        System.out.print("Enter month to view (1-12):");
        Scanner input = new Scanner(System.in);
        int month = input.nextInt();

        String sql = "SELECT month, day, year, hi, lo FROM temperatures "
                + "WHERE month = " + month + " AND year = 2020 ORDER BY month, day, year;";

        try
        {
            conn = DriverManager.getConnection(connectionString, dbLogin, dbPassword);
            if (conn != null)
            {
                try (Statement stmt = conn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                     ResultSet rs = stmt.executeQuery(sql))
                {
                    int numRows;
                    int numCols = 5;
                    rs.last();
                    numRows = rs.getRow();

                    rs.first();
                    temps = new String[numRows][numCols];
                    for (int i = 0; i < numRows; i++)
                    {
                        temps[i][0] = rs.getString("month");
                        temps[i][1] = rs.getString("day");
                        temps[i][2] = rs.getString("year");
                        temps[i][3] = rs.getString("hi");
                        temps[i][4] = rs.getString("lo");
                        rs.next();
                    }
                }
                catch (SQLException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }

        /*
        outString is what gets displayed to the user
         */
        String outString =
                "--------------------------------------------------------------\n" +
                "December 2020: Temperatures in Utah\n" +
                "--------------------------------------------------------------\n" +
                "Date       High  Low  Variance\n" +
                "--------------------------------------------------------------\n";
        System.out.println(outString);

        writeFile(outFile, outString, false);

        //Display temperatures for each day in December in tubular format
        for (int i = 0; i <= temps.length - 1; i ++) {
            String date = String.format("%s/%s/%s", temps[i][0], temps[i][1],temps[i][2]);

            variance = Integer.parseInt(temps[i][3]) - Integer.parseInt(temps[i][4]);

            outString = String.format("%s%5s%5s%5s%n", date, temps[i][3], temps[i][4], variance);

            System.out.print(outString);
            writeFile(outFile, outString, true);
        }

        maxHi = getMaxHi();
        minLo = getMinLo();

        outString = "--------------------------------------------------------------\n";
        System.out.print(outString);
        writeFile(outFile, outString, true);

        //Display the average Hi and Lo and the Max/Min Hi/Lo for the entire month
        outString = String.format("December Highest Temperature: %d/%d: %d Average Hi: %.1f%n", month, maxHi[0], maxHi[1], getAverage(3));
        System.out.print(outString);
        writeFile(outFile, outString, true);
        outString = String.format("December Lowest Temperature: %d/%d: %d Average Low: %.1f%n", month, minLo[0], minLo[1], getAverage(4));
        System.out.print(outString);
        writeFile(outFile, outString, true);
        outString = "--------------------------------------------------------------\nGraph\n";
        System.out.print(outString);
        writeFile(outFile, outString, true);

        printGraph();

        //Graph the max
        for(int i = 0; i <= temps.length - 1; i++){
            int hi = Integer.parseInt(temps[i][3]);
            int lo = Integer.parseInt(temps[i][4]);

            outString = "";
            System.out.printf("%2s   Hi ", temps[i][1]);
            outString = String.format("%2s   Hi ", temps[i][1]);

            for (int j = 0; j < hi; j++){
                System.out.print("+");
                outString += "+";
            }
            System.out.println();
            outString += "\n";
            writeFile(outFile, outString, true);

            System.out.print("     Lo ");
            outString = "     Lo ";
            for (int j = 0; j < lo; j++){
                System.out.print("-");
                outString += "-";
            }
            System.out.println();
            outString += "\n";
            writeFile(outFile, outString, true);
        }

        printGraph();
    }

    /**
     * Calculates the average temperature
     * @param index to operated on
     * @return average temperature as a double
     */
    private static double getAverage(int index){
        double d = 0;
        int x;

        for (int i = 0; i <= temps.length - 1; i ++) {
            x = Integer.parseInt(temps[i][index]);
            d = d + x;
        }

        d = (d / temps.length);

        return d;
    }

    /**
     * Gets the highest temperature for the month
     * @return array of the highest temperature. Index 0 is the date, index 1 is the temperature
     */
    private static int[] getMaxHi(){
        int[] arr = new int[2];
        int d;
        int h;

        for (int i = 0; i <= temps.length - 1; i ++) {
            d = Integer.parseInt(temps[i][1]);
            h = Integer.parseInt(temps[i][3]);

            if (i == 0 || h > arr[1]){
                arr[0] = d;
                arr[1] = h;
            }
        }
        return arr;
    }

    /**
     * Gets the lowest temperature for the month
     * @return array of the lowest temperature. Index 0 is the date, index 1 is the temperature
     */
    private static int[] getMinLo(){
        int[] arr = new int[2];
        int d;
        int m;

        for (int i = 0; i <= temps.length - 1; i ++) {
            d = Integer.parseInt(temps[i][1]);
            m = Integer.parseInt(temps[i][4]);

            if (i == 0 || m < arr[1]){
                arr[0] = d;
                arr[1] = m;
            }
        }
        return arr;
    }

    /**
     * Prints the temperature graph
     * @throws IOException
     */
    private static void printGraph() throws IOException {
        String outString = "--------------------------------------------------------------\n";
        System.out.print(outString);
        writeFile(outFile, outString, true);

        outString = String.format("        %-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%n", "1","5","10","15","20","25","30","35","40","45","50");
        writeFile(outFile, outString,true);
        System.out.print(outString);outString = String.format("        %-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%n", "|","|","|","|","|","|","|","|","|","|","|");
        System.out.print(outString);
        writeFile(outFile, outString, true);

        outString = "--------------------------------------------------------------\n";
        System.out.print(outString);
        writeFile(outFile, outString, true);
    }

    /**
     * Writes data to file in the same format as the console output
     * @param file to be written to
     * @param ln: string representing one line to be written to the file
     * @throws IOException
     */
    private static void writeFile(File file, String ln, boolean append) throws IOException {
        FileWriter fw = new FileWriter(file, append);
        fw.write(ln);
        fw.close();
    }
}
