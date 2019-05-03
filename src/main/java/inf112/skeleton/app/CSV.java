package inf112.skeleton.app;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Object representing a CSV data file. */
public class CSV implements Iterable<CSV.Row> {
    private Vector<String> matrix;
    private int w = 0, h = 1;

    public class CSVError extends Exception {
        public CSVError(String message) {
            super(message);
        }
    }

    /**
     * Initialize a CSV object from a file.
     *
     * @param path Path to the file
     * @throws IOException If the underlying Java IO functions fail.
     * @throws CSVError If the file is empty, or it has an inconsistent
     *                  number of columns per line
     */
    public CSV(String path) throws IOException, CSVError {
        matrix = new Vector<String>();
        try (BufferedReader istream = new BufferedReader(new FileReader(path))) {
            String header_line = istream.readLine();
            if (header_line == null)
                throw new CSVError("No content in file");
            Vector<String> header = csvSplitLine(header_line);
            matrix.addAll(header);
            w = header.size();
            for (String line = istream.readLine(); line != null; line = istream.readLine()) {
                Vector<String> csv_line = csvSplitLine(line);
                h++;
                if (csv_line.size() != w)
                    throw new CSVError("Found csv line of different size at " + path + ":" + h);
                matrix.addAll(csv_line);
            }
        }
    }

    /**
     * Split a line into columns according to csv rules.
     *
     * We can't just use String.split as this would split
     * up strings based on commas embedded in quotes.
     *
     * Note: In CSV quotes inside quotes are escaped like this "something ""csv-like"""
     *       i.e double quotes inside quotes become normal quotes.
     *
     * @param line The text line containing the columns.
     * @param comma The separation character, for most csv files this will be ','.
     * @return A list of substrings from the given line.
     */
    private static Vector<String> csvSplitLine(String line, char comma) {
        Vector<String> subs = new Vector<>();
        int beg = 0,
            i = 0;
        char quot = '"';
        boolean in_quot = false,
                quit_quot = false;
        for (char c: line.toCharArray()) {
            if (in_quot) {
                if (c == quot) {
                    // Both the last character and this character are quotes, this was
                    // an escaped quote, continue.
                    quit_quot = !quit_quot;
                    i++;
                    continue;
                } else if (quit_quot) {
                    // Last character was a quote, but this character is not a quote.
                    // this means we are outside the quoted space.
                    quit_quot = in_quot = false;
                }
            }

            if (c == quot) {
                in_quot = true;
            } else if (c == comma && !in_quot) {
                subs.add(CSV.readCell(line.substring(beg, i)));
                beg = i+1;
            }
            i++;
        }
        subs.add(CSV.readCell(line.substring(beg, i)));
        return subs;
    }

    /** Call csvSplitLine(String, char) with the default separator (',') */
    private static Vector<String> csvSplitLine(String line) {
        return csvSplitLine(line, ',');
    }

    /** Read a csv cell and turn escaped/quoted strings back into normal ones. */
    private static String readCell(String cell) {
        if (cell.length() == 0 || cell.charAt(0) != '"')
            // Cell is empty or not quoted, no modification needed.
            return cell;
        // Remove quotes and escaped quotes
        return cell.substring(1, cell.length()-1).replace("\"\"", "\"");
    }

    /** Format a cell to be outputted into a csv-formatted file. */
    private static String fmtCell(String s) {
        char sarr[] = s.toCharArray();
        for (char c: sarr) {
            // Found character that needs to be escaped/quoted
            if (c == ',' || c == '"' ) {
                String ss = "";
                ss += "\"";

                // Escape quotes
                for (char j: sarr)
                    if (j == '"')
                        ss += "\"\"";
                    else
                        ss += c;

                ss += "\"";
                // Return modified cell
                return ss;
            }
        }
        // No changes to the cell required
        return s;
    }

    /** Write the CSV object to an output stream. */
    public void write(PrintStream ostream) {
        for (int y = 0;  y < h; y++) {
            int base = y*w;
            for (int x = 0; x < w; x++) {
                ostream.print(fmtCell(matrix.get(base + x)));
                ostream.print((x+1 == w) ? "": ",");
            }
            ostream.println();
        }
    }

    /** @return Number of rows. */
    public int nRows() {
        return h;
    }

    /** @return Number of columns. */
    public int nCols() {
        return w;
    }

    /**
     * Get all cells at column `idx`
     */
    public Vector<String> getColCells(int idx) {
        Vector<String> vec = new Vector<>();
        for (int i = w+idx; i < w*h; i += w)
            vec.add(matrix.get(i));
        return vec;
    }

    /**
     * Get all cells in the column with header `name`.
     * 
     * @return vector of cells, or null if the name is not found.
     */
    public Vector<String> getColCells(String name) throws CSVError {
        int idx = getColIndex(name);
        if (idx < 0)
            throw new CSVError("No such name");
        return getColCells(idx);
    }

    /**
     * Get the column index given a column name.
     *
     * @return column index, or -1 if no such name is found.
     */
    public int getColIndex(String name) {
        for (int i = 0; i < w; i++)
            if (matrix.get(i).equals(name))
                return i;
        return -1;
    }

    /**
     * Get all the strings from a row.
     * 
     * @param idx The row number/index.
     * @return Vector of strings on the row.
     */
    public Vector<String> getRowCells(int idx) {
        Vector<String> vec = new Vector<>();
        for (int i = idx * w; i < (idx+1)*w; i++)
            vec.add(matrix.get(i));
        return vec;
    }

    public String getCell(int row, int col) {
        return matrix.get((row * w) + col);
    }

    public class Row {
        CSV csv;
        int idx;

        public Row(CSV csv, int idx) {
            this.csv = csv;
            this.idx = idx;
        }

        public String get(String name) throws CSVError {
            int col = csv.getColIndex(name);
            if (idx == -1)
                throw new CSVError("No such column name: '" + name + "'");
            return csv.getCell(idx, col);
        }

        public int getInt(String name) throws CSVError {
            return Integer.parseInt(get(name).trim());
        }

        public double getDouble(String name) throws CSVError {
            return Double.parseDouble(get(name).trim());
        }
    }

    @Override
    public Iterator<Row> iterator() {
        CSV csv = this;
        return new Iterator<Row>() {
            private int row = 1;

            @Override
            public boolean hasNext() { return row < nRows(); }

            @Override
            public Row next() {
                if (!hasNext()) throw new NoSuchElementException();
                return new Row(csv, row++);
            }

            @Override
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
}
