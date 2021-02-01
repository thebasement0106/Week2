import java.io.*;

/**
 * This program reads information from a file and displays a user-friendly report based on the data
 *
 * Author: Jeff Ostler
 */

public class TempMain {
    private static final int[][] temps = new int[31][3];
    private static File inFile = new File("C:\\Users\\Jeff\\Documents\\SLCDecember2020Temperatures.csv");
    private static File outFile = new File("C:\\Users\\Jeff\\Documents\\TemperaturesReport.txt");

    public static void main(String[] args) throws IOException {

        int[] maxHi;
        int[] minLo;

        //Read information from .csv file
        if (inFile.exists()){
            String line;
            String[] tempArr;
            int rowCount = 0;

            BufferedReader br = new BufferedReader(new FileReader(inFile));

            while((line = br.readLine()) != null) {
                tempArr = line.split(",");


                for (int i = 0; i <= tempArr.length - 1; i++) {
                    temps[rowCount][i] = Integer.parseInt(tempArr[i]);
                }
                rowCount ++;
            }
            br.close();
        }

        /*
        outString is what gets displayed to the user
         */
        String outString =
                "--------------------------------------------------------------\n" +
                "December 2020: Temperatures in Utah\n" +
                "--------------------------------------------------------------\n" +
                "Day  High Low  Variance\n" +
                "--------------------------------------------------------------\n";
        System.out.println(outString);

        writeFile(outFile, outString);

        //Display temperatures for each day in December in tubular format
        for (int i = 0; i <= temps.length - 1; i ++) {
            outString = String.format("%-5s%-5s%-5s%-5s%n", temps[i][0], temps[i][1], temps[i][2],
                    (temps[i][1] - temps[i][2]));

            System.out.print(outString);
            writeFile(outFile, outString);
        }

        maxHi = getMaxHi();
        minLo = getMinLo();

        outString = "--------------------------------------------------------------\n";
        System.out.print(outString);
        writeFile(outFile, outString);

        //Display the average Hi and Lo and the Max/Min Hi/Lo for the entire month
        outString = String.format("December Highest Temperature: 12/%d: %d Average Hi: %.1f%n", maxHi[0], maxHi[1], getAverage(1));
        System.out.print(outString);
        writeFile(outFile, outString);
        outString = String.format("December Lowest Temperature: 12/%d: %d Average Low: %.1f%n", minLo[0], minLo[1], getAverage(2));
        System.out.print(outString);
        writeFile(outFile, outString);
        outString = "--------------------------------------------------------------\nGraph\n";
        System.out.print(outString);
        writeFile(outFile, outString);

        printGraph();

        //Graph the max
        for(int i = 0; i <= temps.length - 1; i++){
            outString = "";
            System.out.printf("%2d   Hi ", temps[i][0]);
            outString = String.format("%2d   Hi ", temps[i][0]);

            for (int j = 0; j < temps[i][1]; j++){
                System.out.print("+");
                outString += "+";
            }
            System.out.println();
            outString += "\n";
            writeFile(outFile, outString);

            System.out.print("     Lo ");
            outString = "     Lo ";
            for (int j = 0; j < temps[i][2]; j++){
                System.out.print("-");
                outString += "-";
            }
            System.out.println();
            outString += "\n";
            writeFile(outFile, outString);
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

        for (int i = 0; i <= temps.length - 1; i ++) {
            d = d + temps[i][index];
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

        for (int i = 0; i <= temps.length - 1; i ++) {
            if (i == 0 || temps[i][1] > arr[1]){
                arr[0] = temps[i][0];
                arr[1] = temps[i][1];
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

        for (int i = 0; i <= temps.length - 1; i ++) {
            if (i == 0 || temps[i][2] < arr[1]){
                arr[0] = temps[i][0];
                arr[1] = temps[i][2];
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
        writeFile(outFile, outString);

        outString = String.format("        %-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%n", "1","5","10","15","20","25","30","35","40","45","50");
        writeFile(outFile, outString);
        System.out.print(outString);outString = String.format("        %-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%n", "|","|","|","|","|","|","|","|","|","|","|");
        System.out.print(outString);
        writeFile(outFile, outString);

        outString = "--------------------------------------------------------------\n";
        System.out.print(outString);
        writeFile(outFile, outString);
    }

    /**
     * Writes data to file in the same format as the console output
     * @param file to be written to
     * @param ln: string representing one line to be written to the file
     * @throws IOException
     */
    private static void writeFile(File file, String ln) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        fw.write(ln);
        fw.close();
    }
}
