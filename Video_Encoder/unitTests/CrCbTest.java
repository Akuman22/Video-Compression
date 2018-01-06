import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CrCbTest {
    @Test
    public void compressCrCb_All_ones() throws Exception {

        // intitialize expected result
        int[][] expected = new int[4][4];
        for(int row = 0 ; row < 4 ; row++){
            for(int col = 0; col < 4 ; col ++){
                expected[row][col] = 1;
            }
        }

        //initialize test input
        int[][] testInput = new int[8][8];
        for(int row = 0 ; row < 8 ; row++){
            for(int col = 0; col < 8 ; col ++){
                if(col % 2 == 0){
                    testInput[row][col] = 1;
                }
                else{
                    testInput[row][col] = 2;
                }
            }
        }


        //result should be a 4x4 matrix of only ones
        int[][] result = new int[4][4];

        CrCb x = new CrCb("test");

        result = x.compressCrCb(testInput);

        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void decompressCrCb2_All_ones() throws Exception {
        CrCb x = new CrCb("test");
        ArrayList<Integer> Compressed = new ArrayList<>(Arrays.asList(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1));
        ArrayList<Integer> expected = new ArrayList<>(64);
        for(int i = 0; i < 64 ; i++){
            expected.add(1);
        }

        ArrayList<Integer> result = new ArrayList<Integer>(64);
        result = x.decompressCrCb2(Compressed);
        assertTrue(result.equals(expected) == true);
    }

}