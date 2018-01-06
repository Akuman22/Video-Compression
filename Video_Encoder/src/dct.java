import java.lang.*;



/* 
 * 2-dimensional DCT algorithm 
 */
// This DCT class or function accepts a 2-dimensional array of integers and performs the 8x8 DCT on those integers. 
// The DCT helps separate the image into parts (or spectral sub-bands) of differing importance (with respect to the image's visual quality). 
// The DCT is similar to the discrete Fourier transform

// The following is the basic operation of the 2d-dct
// input image is N by M. 
// f(i,j) is the intensity of the pixel in row i and column j;
// F(u,v) is the DCT coefficient in row k1 and column k2 for the DCT matrix
// For most images, much of the signal energy lies at low frequencies; these appear in the 
// upper left corner of the DCT. 
// Compression is achieved since the lower right vlaues represent higher frequencies, and are 
// often small- small enough to be neglected with little visible distortion. 

// The DCT input is an 8 by 8 array of integers. This array contains each pixel's gray scale
// level; This DCT function doesn't care what values the inputs have as long as it is an array. 


public class dct{
	 
	public static int N = 8; // DCT block size
	public static int QUALITY = 25; // Set quality of compression 0 best 25 worst
	
	// We can implement zig-zag scanning, this is performed after non-uniform quantization of NxN DCT coefficients
	// and before Category/Run length coding of them. The results of the DCT transform are 
	// 1 DC coefficient and 63 AC coefficients. The DC coefficient represents the average color of the 
	// 8x8 region. The zig-zag scanning pattern for run-length coding of the quantized DCT coefficents was included in the MPEG standard
	
	public int zigZag[][] =  new int [64][2];

	// Image properties, Rows and columns 
	public int rows = 320; 
	public int columns = 240; 

	// Setup the cosine matrix. Which is an N*N matrix
	public static double c[][] = new double[N][N];

	// setup up the transfromed cosine matrix, N*N
	public static double cT[][] = new double[N][N];

	// DCT Result Matrix
	public int resultDCT[][] = new int[rows][columns];

	public dct(int QUALITY)
	{
		initMatrix(QUALITY);
		dctWrapper();
	}

	// initMatrix, sets up the Cosine Transform Matrix and the Transposed CT. These
	// are used by the forward and inverse DCT.
	// it is useful to initialize this matrix with the class so we only need
	// to go through this calculation once and can do it in O(1) time.
	private static void initMatrix(int quality)
	{
		int i;
		int j;

		// The code below sets-up the matrix. For all j = 0 the value of the cosine is:
		// 1/sqrt(N) where we have an NxN matrix.
		// For the cosine transform matrix the values lie on the row vs the column.
		for (j = 0; j < N; j++)
		{
			double nn = (double)(N);
			c[0][j] = 1.0 / Math.sqrt(nn);
			cT[j][0] = c[0][j];
		}

		// This calculates and places the rest of the cosine values in the matrix.
		for (i = 1; i < 8; i ++)
		{
			for(j=0; j<8; j++)
			{
				double jj = (double)j;
				double ii = (double)i;
				// c = the horizontal version of the cosine transform.
				// cT = the vertical version of the cosine transform.
				// Both of these transforms are necessary in order to preform the 2d-DCT because you need
				// to do the horizontal cosine transform then the vertical transform.

				c[i][j] = Math.sqrt(2.0/8.0) * Math.cos(((2.0* jj + 1.0) * ii * Math.PI)/ (2.0 * 8.0));
				cT[j][i] = c[i][j];
			}
		}
	}

	// The next step is to implement the DCT. The following version is not yet optimized.
	// this performs the forward 2d-DCT on an 8x8 array.
	public static int[][] forwardDCT(int input[][])
	{
		for(int i = 0; i <input.length ; i++) {
			for(int j = 0; j < input.length ; j++) {
				//System.out.print(input[i][j] + " ");
			}
			//System.out.println();
		}
		int output[][] = new int[N][N];
		double temp[][] = new double[N][N];
		double temp1;
		int i;
		int j;
		int k;

		for (i = 0; i < N; i ++)
		{
			for (j = 0; j<N; j++)
			{
				temp[i][j] = 0.0;
				for (k=0; k<N; k++)
				{
					//System.out.println(cT[k][j]);
					temp[i][j] += (((int)(input[i][k])) * cT[k][j]);
					//System.out.println(temp[i][j]);
				}
			}
		}

		for(i = 0; i < N; i++)
		{
			for(j=0; j<N; j++)
			{
				temp1 = 0.0;
				for (k = 0; k<N; k++)
				{
					temp1 += (c[i][k] * temp[k][j]);
				}
				output[i][j] = (int)Math.round(temp1);
			}
		}

/*		for(i = 0; i < output.length ; i++) {
			for(j = 0; j < output.length ; j++) {
				System.out.print(output[i][j] + " ");
			}
			System.out.println();
		}*/
		return output;
	}

	// This function takes the data structure as provided and then computes
	// the DCT on the elements of that data structure.
	public void dctWrapper(){

		int[][] dctYValues;
		// First thing I want to do is take the Y values from the each block and
		// compute the forward DCT of those values.
		for (Image_Object obj: Main.allFrames) {
			for(int ii = 0; ii<obj.macroBlocks.size(); ii++) {
				img_YCrCb aBlock = obj.macroBlocks.get(ii);
				// From here will get the property y_values and get the DCT of that.
				dctYValues = forwardDCT(aBlock.Y);
				// Return the Y Values to there original values.
                obj.macroBlocks.get(ii).Y = dctYValues;
			}
		}

	//	return dataS;
	}

    public void idctWrapper(){

        int[][] idctYValues;
        // First thing I want to do is take the Y values from the each block and
        // compute the forward DCT of those values.
        for (Image_Object obj: Main.allFrames) {
            for(int ii = 0; ii<obj.macroBlocks.size(); ii++) {
                img_YCrCb aBlock = obj.macroBlocks.get(ii);
                // From here will get the property y_values and get the DCT of that.
                idctYValues = inverseDCT(aBlock.Y);
                // Return the Y Values to there original values.
                obj.macroBlocks.get(ii).Y = idctYValues;
            }
        }

        //	return dataS;
    }
	    // The following function takes the inverse DCT on a matrix whose DCT.

    public int[][] inverseDCT(int input[][])
    {
        int output[][] = new int[N][N];
        double temp[][] = new double[N][N];
        double temp1;
        int i;
        int j;
        int k;

        for (i=0; i<N; i++)
        {
            for (j=0; j<N; j++)
            {
                temp[i][j] = 0.0;
                for (k=0; k<N; k++)
                {
                    temp[i][j] += input[i][k] * c[k][j];
                }
            }
        }
        for (i=0; i<N; i++)
        {
            for (j=0; j<N; j++)
            {
                temp1 = 0.0;
                for (k=0; k<N; k++)
                {
                    temp1 += cT[i][k] * temp[k][j];
                }

                     output[i][j] = (int)Math.round(temp1);
            }
        }

        return output;
    }
}