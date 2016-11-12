/* Name: Jeffrey Jiang

   UID: 904255069

   Others With Whom I Discussed Things:

   Other Resources I Consulted: Piazza
   
*/

import java.io.*;
import java.util.Arrays;
import java.util.stream.*;
import java.util.concurrent.*;

// a marker for code that you need to implement
class ImplementMe extends RuntimeException {}

// an RGB triple
class RGB {
    public int R, G, B;
    
    RGB(int r, int g, int b) {
    	R = r;
	G = g;
	B = b;
    }
    
    public String toString() { return "(" + R + "," + G + "," + B + ")"; }
}


// an object representing a single PPM image
class PPMImage {
    protected int width, height, maxColorVal;
    protected RGB[] pixels;
    
    public PPMImage(int w, int h, int m, RGB[] p) {
	width = w;
	height = h;
	maxColorVal = m;
	pixels = p;
    }
    
    // parse a PPM image file named fname and produce a new PPMImage object
    public PPMImage(String fname) 
    	throws FileNotFoundException, IOException {
	FileInputStream is = new FileInputStream(fname);
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	br.readLine(); // read the P6
	String[] dims = br.readLine().split(" "); // read width and height
	int width = Integer.parseInt(dims[0]);
	int height = Integer.parseInt(dims[1]);
	int max = Integer.parseInt(br.readLine()); // read max color value
	br.close();
	
	is = new FileInputStream(fname);
	// skip the first three lines
	int newlines = 0;
	while (newlines < 3) {
	    int b = is.read();
	    if (b == 10)
		newlines++;
	}
	
	int MASK = 0xff;
	int numpixels = width * height;
	byte[] bytes = new byte[numpixels * 3];
        is.read(bytes);
	RGB[] pixels = new RGB[numpixels];
	for (int i = 0; i < numpixels; i++) {
	    int offset = i * 3;
	    pixels[i] = new RGB(bytes[offset] & MASK, 
				bytes[offset+1] & MASK, 
				bytes[offset+2] & MASK);
	}
	is.close();
	
	this.width = width;
	this.height = height;
	this.maxColorVal = max;
	this.pixels = pixels;
    }

	// write a PPMImage object to a file named fname
    public void toFile(String fname) throws IOException {
	FileOutputStream os = new FileOutputStream(fname);
	
	String header = "P6\n" + width + " " + height + "\n" 
	    + maxColorVal + "\n";
	os.write(header.getBytes());
	
	int numpixels = width * height;
	byte[] bytes = new byte[numpixels * 3];
	int i = 0;
	for (RGB rgb : pixels) {
	    bytes[i] = (byte) rgb.R;
	    bytes[i+1] = (byte) rgb.G;
	    bytes[i+2] = (byte) rgb.B;
	    i += 3;
	}
	os.write(bytes);
	os.close();
    }

    /* 
       simply a private helper function used for the purpose of 
       computing and creating a new pixel that is the negation
       of any given pixel. 
       used as part of the map from one pixel array to another. 
    */
    private RGB negateRGB(RGB pixel) {
	int nR = maxColorVal - pixel.R;
	int nG = maxColorVal - pixel.G;
	int nB = maxColorVal - pixel.B;
	return new RGB(nR, nG, nB);
    }
    
    // implement using Java 8 Streams
    public PPMImage negate() {
	RGB[] npixels = Arrays.stream(pixels)
	    .parallel()
	    .map(pixel -> negateRGB(pixel))
	    .toArray(RGB[]::new);

	return new PPMImage(width, height, maxColorVal, npixels);
    }

    /*
      private helper function that takes a pixel and returns a new
      RGB pixel that is the greyscale version of the original 
      pixel. 
      
      here we "cast" a long to an int.
      however, as the professor mentioned on a Piazza post,
      our cast is, in fact, a coercion, where we simply change the way we
      view a previously stored primitive, which shouldn't change the value
      in our case.
      he also notes that these coercions cannot create a runtime
      ClassCastException, making them more "safe".
    */
    private RGB graypixel(RGB pixel) {
	double grayVal = 0.299 * pixel.R + 0.587 * pixel.G + 0.114 * pixel.B;
	int roundedgray = (int)Math.round(grayVal);

	return new RGB(roundedgray, roundedgray, roundedgray);
    }
	
    // implement using Java 8 Streams
    public PPMImage greyscale() {
	RGB[] npixels = Arrays.stream(pixels)
	    .parallel()
	    .map(pixel -> graypixel(pixel))
	    .toArray(RGB[]::new);

	return new PPMImage(width, height, maxColorVal, npixels);
	//throw new ImplementMe();
    }    
    
    // implement using Java's Fork/Join library
    public PPMImage mirrorImage() {
	RGB[] npixels = (new MirrorTask(width, height, pixels)).compute();

	return new PPMImage(width, height, maxColorVal, npixels);
	//throw new ImplementMe();
    }

    /* takes an index and returns the index of its mirrored position. */
    private int mirrorIndex(int i) {
	int row_num = i / width; // which will remain constant. 
	int col_num = i % width; // which will be mirrored. 

	int mirror_col = width - col_num - 1; // mirror. 
	return (row_num * width + mirror_col);
    }
    
    // implement using Java 8 Streams
    public PPMImage mirrorImage2() {
	/* the idea is: 
	   [0, 1, 2, ..., width*height - 1] is equivalent to
	   [row0, row1, ..., row(height-1)] if we map to pixels. 
	   so we map this to what would be 
	   [row0', row1', ..., row(height-1)'] by using mirrorIndex.
	   then we map this back into pixels, making our mirrored image. 
	*/
	
	RGB[] npixels = IntStream.range(0, width*height)
	    .parallel()
	    .map(i -> mirrorIndex(i))
	    .mapToObj(i -> pixels[i])
	    .toArray(RGB[]::new);
	
	return new PPMImage(width, height, maxColorVal, npixels);
    }
    
    // implement using Java's Fork/Join library
    public PPMImage gaussianBlur(int radius, double sigma) {
	RGB[] npixels = (new GaussianTask(width, height, pixels, radius,
					  sigma)).compute();

	return new PPMImage(width, height, maxColorVal, npixels);
    }
    
}


// code for creating a Gaussian filter
class Gaussian {
    
    protected static double gaussian(int x, int mu, double sigma) {
	return Math.exp( -(Math.pow((x-mu)/sigma,2.0))/2.0 );
    }
    
    public static double[][] gaussianFilter(int radius, double sigma) {
	int length = 2 * radius + 1;
	double[] hkernel = new double[length];
	for(int i=0; i < length; i++)
	    hkernel[i] = gaussian(i, radius, sigma);
	double[][] kernel2d = new double[length][length];
	double kernelsum = 0.0;
	for(int i=0; i < length; i++) {
	    for(int j=0; j < length; j++) {
		double elem = hkernel[i] * hkernel[j];
		kernelsum += elem;
		kernel2d[i][j] = elem;
	    }
	}
	for(int i=0; i < length; i++) {
	    for(int j=0; j < length; j++)
		kernel2d[i][j] /= kernelsum;
	}
	return kernel2d;
    }
}

/* this class makes a new MirrorTask which allows for the easy use
   of fork / join to create and parallelize threads. 

   the main differentiator of this code is that this operates mainly on 
   rows of pixels, and not pixels themselves. This is because a row of 
   pixels has natural operation on the other pixels in the rows when
   mirroring the pixel. 
*/
class MirrorTask extends RecursiveTask<RGB[]> {
    private final int MIN_ROWS = 4;

    private RGB[] pixels;
    private int width, height;
    private int start, end;

    // this constructor offers easy use for outside sources. 
    public MirrorTask(int w, int h, RGB[] p)
    {
	this(w, h, p, 0, h);
    }

    // this is an additional constructor which indicates start and end
    // parameters, mainly for internal use. 
    public MirrorTask(int w, int h, RGB[] p, int sr, int er)
    {
	pixels = p;
	width = w;
	height = h;
	start = sr;
	end = er;
    }

    /* the compute method. splits into two parts:
       1. when we are smaller than our cutoff, we find the mirror image 
          of the rows we are working with. This is done in the other methods.
       2. else, we spawn two new MirrorTasks, each with half of the rows
          remaining. At the end, we merge the two returned arrays together. 
    */
    public RGB[] compute()
    {
	if ( end - start > MIN_ROWS ) {
	    int mid = (start + end) / 2;
	    MirrorTask top = new MirrorTask(width, height, pixels, start, mid);
	    MirrorTask bot = new MirrorTask(width, height, pixels, mid, end);

	    top.fork();
	    RGB[] rbot = bot.compute();
	    RGB[] rtop = top.join();
	    
	    // inefficient, but we'll use this for now.
	    // since it's actually very simplistic, code-wise.
	    // and see if it works. 
	    int size = rbot.length + rtop.length;
	    int count = 0;
	    RGB[] reflected = new RGB[size];
	    for (RGB pixel : rtop) {
		reflected[count] = pixel;
		count++;
	    }
	    for (RGB pixel : rbot) {
		reflected[count] = pixel;
		count++;
	    }
	    return reflected;
	} else {
	    return reflect_group();
	}
    }

    /* This helper method essentially deals with the code of 
       executing the sequential reflection on each row we have to deal with.
       It calls another helper function reflect_row, which lowers the
       efficiency of this program, but separates the logic flow 
       a little bit. 
    */
    private RGB[] reflect_group()
    {
	int size = (end - start) * width;
	int count = 0;
	RGB[] reflected = new RGB[size];
	for ( int i = start; i < end; i++ ) {
	    RGB[] rrow = reflect_row(i);
	    for (RGB pixel : rrow) {
		reflected[count] = pixel;
		count++;
	    }
	}
	return reflected;  
    }

    /* This helper method is not actually helping in terms of efficiency.
       (it's probaby actually detrimental to both time and space efficiency).
       It does help separate the different logic in the code though.

       One way to fix this issue is to pass in the array from reflect_group
       and fill in the values there, but this is not functional and 
       requires another parameter of which row in the array of reflect_group, 
       the row we are dealing with is in (i in the loop), which adds 
       additional complexity and confusion in this function, if added. 

       The other way to fix this issue is to simply not have this function
       and do all the work in reflect_row.
    */
    private RGB[] reflect_row(int row_num)
    {
	int count = width - 1;
	RGB[] rrow = new RGB[width];
	for( int i = row_num * width; i < (row_num + 1) * width; i++ ) {
	    rrow[count] = pixels[i];
	    count--;
	}
	return rrow;
    }
}

/* this class is created to allow for parallel computing of Gaussian blur. */
class GaussianTask extends RecursiveTask<RGB[]> {
    private final int CUTOFF = 10;

    private RGB[] pixels;
    private int width, height;
    private int start, end;
    private int radius;
    private double sigma;

    // again this is a constructor more useful for creating this class
    // from an outside source, since it doesn't require knowledge of
    // how the thread will work.
    public GaussianTask(int w, int h, RGB[] p, int r, double sig) {
	this(w, h, p, r, sig, 0, w*h);
    }

    // this constructor is more useful internally, in creating new threads.
    public GaussianTask(int w, int h, RGB[] p, int r, double sig, int s, int e)
    {
	pixels = p;
	width = w;
	height = h;
	radius = r;
	sigma = sig;
	start = s;
	end = e;
    }

    public RGB[] compute() {
	if (end - start > CUTOFF) {
	    int mid = (start + end) / 2;

	    // not really top / bot as was in MirrorTask, but couldn't think
	    // of better names. 
	    GaussianTask top = new
		GaussianTask(width, height, pixels, radius, sigma, start, mid);
	    GaussianTask bot = new
		GaussianTask(width, height, pixels, radius, sigma, mid, end);

	    // fork / join threading. 
	    top.fork();
	    RGB[] bbot = bot.compute();
	    RGB[] btop = top.join();

	    // merges the two arrays together. (inefficient)
	    int size = bbot.length + btop.length;
	    int count = 0;
	    RGB[] blurred = new RGB[size];
	    for (RGB pixel : btop) {
		blurred[count] = pixel;
		count++;
	    }
	    for (RGB pixel : bbot) {
		blurred[count] = pixel;
		count++;
	    }
	    return blurred;
	} else {
	    // simply apply the Gaussian to all the pixels required.
	    
	    RGB[] npixels = new RGB[end-start];
	    for(int i = 0; i < (end - start); i++) {
		npixels[i] = applyGaussian(i + start);
	    }
	    return npixels;
	}
    }

    /* applies the Gaussian filter on a single pixel, denoted by their
       position in the pixels array.
    */
    private RGB applyGaussian(int pixel_num) {
	/* while this recalculation of the filter every time we run 
	   this function seems inefficient, any alternatives pose
	   problems as well. 
	   
	   note, this filter is only computed in the threads where 
	   we are actually applying the Gaussian. The other two 
	   ideas were: 
	   1. compute the filter in every thread. Clearly this 
	      solution was lower efficiency. 
	   2. compute the filter in the top level and pass the filter
	      down through all the levels of threading. 
	      This seemed a little bit too complex, and it creates 
	      situations where the user can pass their own filter
	      into the thread, which violates security. 
	*/
	double[][] filter = Gaussian.gaussianFilter(radius, sigma);

	int row = pixel_num / width;
	int col = pixel_num % width;

	double Rsum = 0;
	double Gsum = 0;
	double Bsum = 0;
	for(int i = 0; i <= 2*radius; i++) {
	    // computes the "clamped" row. 
	    int r = row - radius + i;
	    if ( r < 0 )
		r = 0;
	    if ( r >= height )
		r = height - 1;
	    
	    for (int j = 0; j <= 2*radius; j++) {
		// computes the "clamped" column. 
		int c = col - radius + j;
		if ( c < 0 )
		    c = 0;
		if ( c >= width )
		    c = width - 1;

		// applies the Guassian filter. 
		Rsum += pixels[width*r + c].R * filter[i][j];
		Gsum += pixels[width*r + c].G * filter[i][j];
		Bsum += pixels[width*r + c].B * filter[i][j];
	    }
	}

	// casting long -> int deemed okay by professor on Piazza. 
	int nR = (int)Math.round(Rsum);
	int nG = (int)Math.round(Gsum);
	int nB = (int)Math.round(Bsum);
	
	return new RGB(nR, nG, nB);
    }
}

/* the easiest set of test cases, just to see if things look similar */
class ImageTest {

    public static void main( String[] args ) {
	try {
	    PPMImage orig = new PPMImage("florence.ppm");

	    PPMImage negated = orig.negate();
	    negated.toFile("negated.ppm");

	    PPMImage gray = orig.greyscale();
	    gray.toFile("gray.ppm");

	    PPMImage reflect1 = orig.mirrorImage();
	    reflect1.toFile("reflect1.ppm");

	    PPMImage reflect2 = orig.mirrorImage();
	    reflect2.toFile("reflect2.ppm");
	    	    
	    PPMImage blurred = orig.gaussianBlur(15, 5.0);
	    blurred.toFile("blurred.ppm");

	    orig.toFile("florence2.ppm"); // should look the same as before.
	}
	catch (Exception e) {
	    System.out.println("Exception occurred. Try again.");
	    e.printStackTrace();
	}
    }
}
