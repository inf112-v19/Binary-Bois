package inf112.skeleton.app;

import org.json.JSONArray;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JSONConversionTest {
    @Test
    public void convertMatrixTest() {
        int[][] orig_mat = {
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1},
        };
        final int w = 2;
        JSONArray pos_mat = new JSONArray("[[0,0], [0,1], [1,0], [1,1]]");
        int[][] mat = JSONTools.toIntMatrix(pos_mat);
        for (int y = 0; y < orig_mat.length; y++) {
            for (int x = 0; x < w; x++) {
                System.out.println(mat[y][x]);
                assertEquals(mat[y][x], orig_mat[y][x]);
            }
        }
    }
}
