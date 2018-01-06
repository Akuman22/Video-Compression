package edu.bu.ec504.team1.videoplayer;

/**
 * Created by heqifan on 12/1/17.
 */

import java.util.ArrayList;

public class CrCb {
    public CrCb(String S) {

    }

    /*4:2:0 – In this variant, the chroma components are sub-sampled by a factor of 2,
    both horizontally as well as vertically, thus reducing to a factor of a quarter.*/

    // convert 8x8 matrix to 4x4 matrix by subsampling
    public int[][] compressCrCb(int[][] C){
        int[][] C_compressed = new int[4][4];

        int newRow = 0;
        int newCol = 0;

        for(int row = 0; row < 8 ; row+= 2) {
            for (int col = 0; col < 8; col += 2) {
                C_compressed[newRow][newCol] = C[row][col];
                newCol += 1;
                if(newCol >= 4)
                    newCol = 0;

            }
            if(newRow >= 4) {
                newRow = 0;
            }
            newRow += 1;

        }
        return C_compressed;
    }

    // convert 4x4 matrix to 8x8 matrix by subsampling

    //we know that newCr and new Cb are 4x4
    public int[][] decompressCrCb(int[][] C_compressed){

        int[][] C = new int[8][8];

        //loop thru all indeces of new Cr/Cb
        for(int row = 0; row < 8 ; row+= 2){
            for(int col = 0 ; col < 8 ; col+=2){
                C[row][col] = C_compressed[row][col];
                C[row+1][col] = C_compressed[row][col];
                C[row+1][col+1] = C_compressed[row][col];
                C[row][col+1] = C_compressed[row][col];
            }
        }
        return C;
    }


    public ArrayList<Integer> decompressCrCb2(ArrayList<Integer> C_compressed){

        ArrayList<Integer> C = new ArrayList<>(64);
        for(int i = 0 ; i < 64 ; i++){
            C.add(0);
        }

        Integer compress_index = 0, index = 0;

        while(index < 55){
            //loop through each row
            C.set(index,C_compressed.get(compress_index));
            C.set(index+1,C_compressed.get(compress_index));
            C.set(index+8,C_compressed.get(compress_index));
            C.set(index+9,C_compressed.get(compress_index));

            compress_index += 1;
            if(index == 6 || index == 22 || index == 38){
                index += 10;
            }
            else{
                index+= 2;
            }

        }
        return C;
    }





}//end of class
