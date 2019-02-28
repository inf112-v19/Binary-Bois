package inf112.skeleton.app;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * This serves both as a rudimentary test of the CSV library
 * as well as documentation on how to use it.
 */
public class CSVTest {

    private static CSV getCSV() throws IOException, CSV.CSVError {
        return new CSV("./resources/Programcards.csv");
    }

    /**
     * How you should iterate through CSV file rows.
     *
     * This test attempts to find the card with highest priority, and print its movement.
     *
     * @throws IOException
     * @throws CSV.CSVError
     */
    @Test
    public void iterator() throws IOException, CSV.CSVError {
        CSV csv = getCSV();
        int pri, max_pri = Integer.MIN_VALUE;
        String mov = "";
        for (CSV.Row row : csv)
            // "Priority" and "Movement" correspond to the first row, the so-called "header row"
            // which names all the columns.
            if ((pri = row.getInt("Priority")) > max_pri) {
                max_pri = pri;
                mov = row.get("Movement");
            }
        System.out.println("Card with highest priority " + max_pri + " had movement: " + mov);
    }

    /**
     * A more "manual" way of iterating through a csv file,
     * probably not useful, but shows usage of some useful functions.
     *
     * This test attempts to find the card with lowest priority, and print its movement.
     *
     * @throws IOException
     * @throws CSV.CSVError
     */
    @Test
    public void iterateManual() throws IOException, CSV.CSVError {
        CSV csv = getCSV();
        int pri, min_pri = Integer.MAX_VALUE;
        String mov = "";
        for (int idx = 1; idx < csv.nRows()-1; idx++) {
            Vector<String> row = csv.getRowCells(idx);
            // Get header row indices.
            int mov_idx = csv.getColIndex("Movement");
            int pri_idx = csv.getColIndex("Priority");
            // Have to convert types
            if ((pri = Integer.parseInt(row.get(pri_idx))) < min_pri) {
                min_pri = pri;
                mov = row.get(mov_idx);
            }
        }
        System.out.println("Card with lowest priority " + min_pri + " had movement: " + mov);
    }

    /**
     * Iteration on specific columns.
     *
     * This test attempts to find out how many instances of each kind of movement
     * card there are.
     *
     * @throws IOException
     * @throws CSV.CSVError
     */
    @Test
    public void iterateColumn() throws IOException, CSV.CSVError {
        CSV csv = getCSV();
        HashMap<String, Integer> count = new HashMap<>();
        for (String cell : csv.getColCells("Movement")) {
            String[] pair = cell.split(" ");
            count.putIfAbsent(pair[0], 0);
            count.put(pair[0], count.get(pair[0]) + 1);
        }
        for (Map.Entry<String, Integer> c : count.entrySet()) {
            System.out.println(c.getValue() + " instances of " + c.getKey());
        }
    }
}