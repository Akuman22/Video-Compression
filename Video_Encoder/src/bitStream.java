import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

// This class will take in blocks and output a datastructure that can be placed in a file that acts as a bitstream. 
public class bitStream {

    FileOutputStream fos;
    long byteSize = 0;

	public bitStream(int blockNum, int gOP, int height, int width, int fps,
                     int frameNum, String saveName) {
		File checkFile = new File(saveName);
		try {
			boolean result = Files.deleteIfExists(checkFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
            fos = new FileOutputStream(saveName, true);

        }
        catch (FileNotFoundException e) {}
        createHeader(blockNum, gOP, height, width, fps, frameNum, saveName);
		createMap(saveName, gOP);
		try{
		    fos.close();
        }
        catch (Exception e){

        }

	}

	// Create the bitstream. 
	/*
	 * createHeader creates the header of our bit stream. It takes in the following values: 
	 * # of Blocks, GOP, Height, Width, FPS, # of Frames. 
	 */
	public void createHeader(int blockNum, int GOP, int height, int width, int fps, int frameNum, String fileName) { 
		byte [] bArray=new byte[10]; 
		bArray[0] = (byte) (blockNum & 0xFF); 
		bArray[1] = (byte) ((blockNum >> 8) & 0xFF);
		bArray[2] = (byte) GOP;
		bArray[3] = (byte) (height & 0xFF); 
		bArray[4] = (byte) ((height >> 8) & 0xFF); 
		bArray[5] = (byte) (width & 0xFF); 
		bArray[6] = (byte) ((width >> 8) & 0xFF); 
		bArray[7] = (byte) fps; 
		bArray[8] = (byte) (frameNum & 0xFF); 
		bArray[9] = (byte) ((frameNum >> 8) & 0xFF);
		byteSize+=10;
		System.out.println("Starting File write");
		try {
			fos.write(bArray);
		}catch(Exception e)
		{
			System.out.println("Could not print to file");
		}
		
	}
	
	public void createMap(String fileName, int GOP) {
		int count = 0; 
		int blockCount;
		for(Image_Object obj: Main.allFrames) { 
			// keep track of the I-Frames
			if(count % GOP == 0) {
			    System.out.println("I made it to an I frame " + byteSize );
				for(int ii = 0; ii <obj.macroBlocks.size(); ii++) {
					img_YCrCb aBlock = obj.macroBlocks.get(ii);
					blockToStream(aBlock.Y, fileName);
					blockToStream(aBlock.newCr, fileName);
					blockToStream(aBlock.newCb, fileName);
				}
			} else {
                System.out.println("I made it to a P frame" + byteSize);
                blockCount = 0;
				for(int ii = 0; ii< obj.macroBlocks.size(); ii++) {
					img_YCrCb aBlock = obj.macroBlocks.get(ii);
					int[][] yMatrix = aBlock.Y; 
					if (yMatrix[0][0] == 9999) {
						byte[] bArray; 
						bArray=new byte[3];
						bArray[0] = (byte) 1; 
						bArray[1] = (byte)(blockCount & 0xFF); 
						bArray[2] = (byte)((blockCount >> 8) & 0xFF);
                        byteSize+=3;

                        try{
							fos.write(bArray);
						}catch(Exception e)
						{
							System.out.println("Could not print to file");
						} 
					}
					else 
					{
					    try{
					        fos.write((byte) 0);
                        } catch(Exception e)
                        {
                            System.out.println("Could not print to file");
                        }
						blockToPStream(aBlock.Y, fileName); 
						blockToPStream(aBlock.newCr, fileName);
						blockToPStream(aBlock.newCb, fileName);
					}
					blockCount += 1; 
				}
			}
			count += 1; 
		}
		System.out.println("I finished running the program congrats!");
	}
	
	
	// helper function that will convert a 2d matrix to string;
	public void blockToStream(int[][] aMatrix, String fileName) {
		byte[] bArray = new byte[aMatrix.length*aMatrix.length];
		int count = aMatrix.length;
		for(int i = 0; i < aMatrix.length; i ++) {
			for(int j = 0; j < aMatrix[i].length; j++) {
				bArray[j + i*count] = (byte)aMatrix[i][j]; 
			}
		}
		byteSize+=bArray.length;
		try{
			fos.write(bArray);
		}catch(Exception e)
		{
			System.out.println("Could not print to file");
		} 
	}
	
	public void blockToPStream(int[][] aMatrix, String fileName) {
		byte[] bArray = new byte[aMatrix.length*aMatrix.length];
		int count = aMatrix.length;
		for(int i = 0; i < aMatrix.length; i ++) {
			for(int j = 0; j < aMatrix[i].length; j++) {
				bArray[j + i*count] = (byte)aMatrix[i][j]; 
			}
		}
        byteSize+=bArray.length;
        try{
			fos.write(bArray);
		}catch(Exception e)
		{
			System.out.println("Could not print to file");
		} 
	}
}