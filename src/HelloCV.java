import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class HelloCV{

	//Outputs
	private Mat resizeImageOutput = new Mat();
	private Mat hslThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();

	//Sources
	private Mat source0;
	private ArrayList<MatOfPoint> source1;
	static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	/**
	 * This constructor sets up the pipeline
	 */
	
	public static void main(String args[]) {
		HelloCV g = new HelloCV();
		g.process();
	}
	public HelloCV() {

		source0 = new Mat();
		source0 = Imgcodecs.imread("C:/Users/Z/Documents/Camera_Images/3ft_light.jpg");
		if (source0.empty()){
			System.out.println("Couldn't find image");
		}
		//Mat output = new Mat();
		//Imgproc.cvtColor(source0, output, Imgproc.COLOR_BGR2GRAY);
		//source0 = output;
		//Imgcodecs.imwrite("C:/Users/Z/Documents/Camera_Images/2ft_altered.jpg", source0);
	}
	
  /*  public BufferedImage Mat2BufferedImage(Mat m){
// The output can be assigned either to a BufferedImage or to an Image

    int type = BufferedImage.TYPE_BYTE_GRAY;
    if ( m.channels() > 1 ) {
        type = BufferedImage.TYPE_3BYTE_BGR;
    }
    int bufferSize = m.channels()*m.cols()*m.rows();
    byte [] b = new byte[bufferSize];
    m.get(0,0,b); // get all the pixels
    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(b, 0, targetPixels, 0, b.length);  
    return image;
}
    
    public void displayImage(Image img2) {   
        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
        ImageIcon icon=new ImageIcon(img2);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());        
        frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }*/
    
    /**
	 * This is the primary method that runs the entire pipeline and updates the outputs.
	 */
	public void process() {
		//Step  Resize_Image0:
		Mat resizeImageInput = source0;
		double resizeImageWidth = 640.0;
		double resizeImageHeight = 480;
		int resizeImageInterpolation = Imgproc.INTER_CUBIC;
		resizeImage(resizeImageInput, resizeImageWidth, resizeImageHeight, resizeImageInterpolation, resizeImageOutput);
		//Imgcodecs.imwrite("C:/Users/Z/Documents/Camera_Images/2ft_altered.jpg", resizeImageOutput);
		
		//Step  HSL_Threshold0:
		Mat hslThresholdInput = resizeImageOutput;
		double[] hslThresholdHue = {77.6978417266187, 92.90322580645162};
		double[] hslThresholdSaturation = {55.03597122302158, 125.11884550084888};
		double[] hslThresholdLuminance = {121.53776978417265, 255.0};
		hslThreshold(hslThresholdInput, hslThresholdHue, hslThresholdSaturation, hslThresholdLuminance, hslThresholdOutput);
		Imgcodecs.imwrite("C:/Users/Z/Documents/Camera_Images/3ft_light_altered.jpg", hslThresholdOutput);

		//Step  Find_Contours0:
		Mat findContoursInput = hslThresholdOutput;
		boolean findContoursExternalOnly = false;
		findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);
		System.out.println(hslThresholdOutput);
		for (int i = 0; i < findContoursOutput.size(); i++){
			System.out.println(findContoursOutput.get(i).toString());
		}
		//Step  Filter_Contours0:
		ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
		double filterContoursMinArea = 0;
		double filterContoursMinPerimeter = 0;
		double filterContoursMinWidth = 0;
		double filterContoursMaxWidth = 1000.0;
		double filterContoursMinHeight = 0;
		double filterContoursMaxHeight = 1000.0;
		double[] filterContoursSolidity = {0.0, 100.0};
		double filterContoursMaxVertices = 1000000;
		double filterContoursMinVertices = 0;
		double filterContoursMinRatio = 0.0;
		double filterContoursMaxRatio = 1000;
		filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);
		
		for (int i = 0; i < findContoursOutput.size(); i++){
			Imgproc.drawContours(resizeImageOutput, filterContoursOutput, i, new Scalar(0, 0, 0));
		}
		
		Imgcodecs.imwrite("C:/Users/Z/Documents/Camera_Images/3ft_light_altered.jpg", resizeImageOutput);
	}

	/**
	 * This method is a generated setter for source0.
	 * @param source the Mat to set
	 */
	public void setsource0(Mat source0) {
		this.source0 = source0;
	}

	/**
	 * This method is a generated setter for source1.
	 * @param source the ArrayList<MatOfPoint> to set
	 */
	public void setsource1(ArrayList<MatOfPoint> source1) {
		this.source1 = source1;
	}

	/**
	 * This method is a generated getter for the output of a Resize_Image.
	 * @return Mat output from Resize_Image.
	 */
	public Mat resizeImageOutput() {
		return resizeImageOutput;
	}

	/**
	 * This method is a generated getter for the output of a HSL_Threshold.
	 * @return Mat output from HSL_Threshold.
	 */
	public Mat hslThresholdOutput() {
		return hslThresholdOutput;
	}

	/**
	 * This method is a generated getter for the output of a Find_Contours.
	 * @return ArrayList<MatOfPoint> output from Find_Contours.
	 */
	public ArrayList<MatOfPoint> findContoursOutput() {
		return findContoursOutput;
	}

	/**
	 * This method is a generated getter for the output of a Filter_Contours.
	 * @return ArrayList<MatOfPoint> output from Filter_Contours.
	 */
	public ArrayList<MatOfPoint> filterContoursOutput() {
		return filterContoursOutput;
	}


	/**
	 * Scales and image to an exact size.
	 * @param input The image on which to perform the Resize.
	 * @param width The width of the output in pixels.
	 * @param height The height of the output in pixels.
	 * @param interpolation The type of interpolation.
	 * @param output The image in which to store the output.
	 */
	private void resizeImage(Mat input, double width, double height,
		int interpolation, Mat output) {
		Imgproc.resize(input, output, new Size(width, height), 0.0, 0.0, interpolation);
	}

	/**
	 * Segment an image based on hue, saturation, and luminance ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param lum The min and max luminance
	 * @param output The image in which to store the output.
	 */
	private void hslThreshold(Mat input, double[] hue, double[] sat, double[] lum,
		Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HLS);
		Core.inRange(out, new Scalar(hue[0], lum[0], sat[0]),
			new Scalar(hue[1], lum[1], sat[1]), out);
	}

	/**
	 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
	 * @param input The image on which to perform the Distance Transform.
	 * @param type The Transform.
	 * @param maskSize the size of the mask.
	 * @param output The image in which to store the output.
	 */
	private void findContours(Mat input, boolean externalOnly,
		List<MatOfPoint> contours) {
		Mat hierarchy = new Mat();
		contours.clear();
		int mode;
		if (externalOnly) {
			mode = Imgproc.RETR_EXTERNAL;
		}
		else {
			mode = Imgproc.RETR_LIST;
		}
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(input, contours, hierarchy, mode, method);
	}


	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param inputContours is the input list of contours
	 * @param output is the the output list of contours
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum perimeter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param maxWidth maximum width
	 * @param minHeight minimum height
	 * @param maxHeight maximimum height
	 * @param Solidity the minimum and maximum solidity of a contour
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param maxVertexCount maximum vertex Count
	 * @param minRatio minimum ratio of width to height
	 * @param maxRatio maximum ratio of width to height
	 */
	private void filterContours(List<MatOfPoint> inputContours, double minArea,
		double minPerimeter, double minWidth, double maxWidth, double minHeight, double
		maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
		minRatio, double maxRatio, List<MatOfPoint> output) {
		final MatOfInt hull = new MatOfInt();
		output.clear();
		//operation
		for (int i = 0; i < inputContours.size(); i++) {
			final MatOfPoint contour = inputContours.get(i);
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
			if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int)hull.get(j, 0)[0];
				double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
			final double ratio = bb.width / (double)bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.add(contour);
		}
	}
}
