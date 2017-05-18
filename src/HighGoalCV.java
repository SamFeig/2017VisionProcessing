import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class HighGoalCV {
	//Outputs
	private Mat rgbThresholdOutput = new Mat();
	private Mat hslThresholdOutput = new Mat();
	private Mat cvBitwiseAndOutput = new Mat();
	private Mat cvBitwiseOrOutput = new Mat();
	private Mat cvDilateOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();

	//Sources
    VideoCapture camera;
    private Mat source0;
	static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	//NetworkTables
	public static NetworkTable table;
	public static NetworkTable auton;
	private CameraMath m;
	private static int i = 0;

	/**
	 * Loop continuously.
	 * Update heartbeat and execution time to network tables.
	 */
	public static void main(String args[]) {
		HighGoalCV g = new HighGoalCV();
		double count = 0;
		while(true){
			double startTime = System.currentTimeMillis();
			updateNT("Heartbeat", count % 1000);
			count = (count + 0.001) % 1000;
			
//			updateNT("Start", startTime);
			boolean ignoreFrame = table.getBoolean("Discard", false);
//			boolean ignoreFrame = false;
			if(!ignoreFrame){
			g.process();
			}
			double endTime = (System.currentTimeMillis() - startTime);
			updateNT("Excution time", endTime);
		}
	}
	
	/**
	 * Set network table IP and name.
	 * Initialize the camera and image size.
	 */
	public HighGoalCV() {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress("roboRIO-503-FRC.local");//roboRIO-503-FRC.local //roboRIO-502-FRC.local
		table = NetworkTable.getTable("HG_Camera");
		auton = NetworkTable.getTable("SmartDashboard");
		auton = (NetworkTable)auton.getSubTable("Choose Alliance");
		
		source0 = new Mat();
		
		m = new CameraMath();
		
		try {
			camera = new VideoCapture(0);
			updateNT("Camera Found", true);
			camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 240);//720
        	camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 320);//1280
        	camera.set(Videoio.CAP_PROP_BRIGHTNESS, 0);
        	//camera.set(Videoio.CAP_PROP_EXPOSURE, 25);
//        	camera.set(Videoio.CAP_PROP_CONTRAST, 100);
//        	camera.set(Videoio.CAP_PROP_SATURATION, 25);
		}	
		catch(NullPointerException e) {
			System.out.println("Camera Not Found");
		}   
	}
	
	/**
	 * Read image and apply filters.
	 * Math on image at the end of process. 
	 */
	public void process() {
		try {
			if(!camera.isOpened()) {
				camera = new VideoCapture(0);
				updateNT("Camera Found", true);
				camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 240);
	        	camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 320);
	        	camera.set(Videoio.CAP_PROP_BRIGHTNESS, 0);
				print("Reinitialized Camera");
			}
		}
		catch(CvException e) {
			print("Camera unable to be initialized.");
			return;
		}
		try {
			camera.read(source0);
			
			//source0 = Imgcodecs.imread("/Users/Sam/Desktop/Competition_Images/States/Q2/HG_503.jpg");
			//source0 = Imgcodecs.imread("res/High/H_14ft.jpg");
			
//			String side = auton.getString("selected", "[R] Red");
//			if(side.equals("[R] Red")) {
//				table.putString("Color", "Red");			
//				//COMPETITION-RED
			
			//COMPETITION
			//Step  RGB_Threshold0:
			Mat rgbThresholdInput = source0;
			double[] rgbThresholdRed = {0.0, 87.71276595744679};
			double[] rgbThresholdGreen = {93.64406779661017, 255.0};
			double[] rgbThresholdBlue = {0.0, 255.0};
			rgbThreshold(rgbThresholdInput, rgbThresholdRed, rgbThresholdGreen, rgbThresholdBlue, rgbThresholdOutput);

			//Step  CV_dilate0:
			Mat cvDilateSrc = rgbThresholdOutput;
			Mat cvDilateKernel = new Mat();
			Point cvDilateAnchor = new Point(-1, -1);
			double cvDilateIterations = 1.0;
			int cvDilateBordertype = Core.BORDER_CONSTANT;
			Scalar cvDilateBordervalue = new Scalar(-1);
			cvDilate(cvDilateSrc, cvDilateKernel, cvDilateAnchor, cvDilateIterations, cvDilateBordertype, cvDilateBordervalue, cvDilateOutput);

			//Step  Find_Contours0:
			Mat findContoursInput = cvDilateOutput;
			boolean findContoursExternalOnly = false;
			findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

			//Step  Filter_Contours0:
			ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
			double filterContoursMinArea = 50.0;
			double filterContoursMinPerimeter = 0.0;
			double filterContoursMinWidth = 0.0;
			double filterContoursMaxWidth = 100.0;
			double filterContoursMinHeight = 0.0;
			double filterContoursMaxHeight = 145.0;
			double[] filterContoursSolidity = {0.0, 100.0};
			double filterContoursMaxVertices = 1.0E8;
			double filterContoursMinVertices = 0.0;
			double filterContoursMinRatio = 0.0;
			double filterContoursMaxRatio = 1000000.0;
			filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);

//			}
//			else if(side.equals("[B] Blue")) {
//				table.putString("Color", "Blue");
//				//COMPETITION-BLUE
//			}
			
//			//LIVONIA
//			//Step  RGB_Threshold0:
//			Mat rgbThresholdInput = source0;
//			double[] rgbThresholdRed = {0.0, 54.0};
//			double[] rgbThresholdGreen = {86.0, 255.0};
//			double[] rgbThresholdBlue = {0.0, 255.0};
//			rgbThreshold(rgbThresholdInput, rgbThresholdRed, rgbThresholdGreen, rgbThresholdBlue, rgbThresholdOutput);
//
//			//Step  CV_dilate0:
//			Mat cvDilateSrc = rgbThresholdOutput;
//			Mat cvDilateKernel = new Mat();
//			Point cvDilateAnchor = new Point(-1, -1);
//			double cvDilateIterations = 1.0;
//			int cvDilateBordertype = Core.BORDER_CONSTANT;
//			Scalar cvDilateBordervalue = new Scalar(-1);
//			cvDilate(cvDilateSrc, cvDilateKernel, cvDilateAnchor, cvDilateIterations, cvDilateBordertype, cvDilateBordervalue, cvDilateOutput);
//
//			//Step  Find_Contours0:
//			Mat findContoursInput = cvDilateOutput;
//			boolean findContoursExternalOnly = false;
//			findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);
//
//			//Step  Filter_Contours0:
//			ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
//			double filterContoursMinArea = 50.0;
//			double filterContoursMinPerimeter = 0.0;
//			double filterContoursMinWidth = 0.0;
//			double filterContoursMaxWidth = 100.0;
//			double filterContoursMinHeight = 0.0;
//			double filterContoursMaxHeight = 145.0;
//			double[] filterContoursSolidity = {0.0, 100.0};
//			double filterContoursMaxVertices = 1.0E8;
//			double filterContoursMinVertices = 0.0;
//			double filterContoursMinRatio = 0.0;
//			double filterContoursMaxRatio = 1000000.0;
//			filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);

			}
		catch(CvException e) {
			System.out.println("Camera Not Found");
			updateNT("Camera Found", false);
			return;
		}
		
		/**
		 * Draw contours on image. Disable for increased speed.
		 */
		for(int i = 0; i < filterContoursOutput.size(); i++){
			Imgproc.drawContours(source0, filterContoursOutput, i, new Scalar(0, 0, 0));
		}
				
		ContourFilter c = new ContourFilter(source0);
		c.getBoundingBoxes(filterContoursOutput);
		ArrayList<Rect> r = c.checkContours();
			
		/**
		 * Draw bounding rectangles on image. Disable for increased speed.
		 */
		c.drawRects();
			
		/**
		 * Calculate center, width, height, and offset of target.
		 */		
		double degree = m.math(r, true);
			
		/**
		 * Calculate distance to target.
		 */		
		double distance = m.calcDistanceToGoalHG();
			
		/**
		 * Update network tables values if ignore value is false.
		 */
		boolean ignoreFrame = table.getBoolean("Discard", false);
		if(!ignoreFrame){
			System.out.println("Degrees: " + degree);
			updateNT("Degrees", degree);
			System.out.println("Distance: " + distance);
			updateNT("Distance", distance);
		}
			
		/**
		 * Write final processed image with filters to a file.
		 */
		if(i < 5000) {
			Imgcodecs.imwrite("/home/pi/Desktop/Images/HG_" + i + ".jpg", source0);
			//Imgcodecs.imwrite("res/GRIP_output.jpg", source0);
			i++;
		}
		else if(i == 5000) {
			i = 0;
		}
	}

	/**
	 * This method is a generated setter for source0.
	 * @param source the Mat to set
	 */
	public void setsource0(Mat source0) {
		this.source0 = source0;
	}

	/**
	 * This method is a generated getter for the output of a RGB_Threshold.
	 * @return Mat output from RGB_Threshold.
	 */
	public Mat rgbThresholdOutput() {
		return rgbThresholdOutput;
	}

	/**
	 * This method is a generated getter for the output of a HSL_Threshold.
	 * @return Mat output from HSL_Threshold.
	 */
	public Mat hslThresholdOutput() {
		return hslThresholdOutput;
	}

	/**
	 * This method is a generated getter for the output of a CV_bitwise_or.
	 * @return Mat output from CV_bitwise_or.
	 */
	public Mat cvBitwiseOrOutput() {
		return cvBitwiseOrOutput;
	}
	
	/**
	 * This method is a generated getter for the output of a CV_bitwise_and.
	 * @return Mat output from CV_bitwise_and.
	 */
	public Mat cvBitwiseAndOutput() {
		return cvBitwiseAndOutput;
	}

	/**
	 * This method is a generated getter for the output of a CV_dilate.
	 * @return Mat output from CV_dilate.
	 */
	public Mat cvDilateOutput() {
		return cvDilateOutput;
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
	 * Segment an image based on color ranges.
	 * @param input The image on which to perform the RGB threshold.
	 * @param red The min and max red.
	 * @param green The min and max green.
	 * @param blue The min and max blue.
	 * @param output The image in which to store the output.
	 */
	private void rgbThreshold(Mat input, double[] red, double[] green, double[] blue,
		Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2RGB);
		Core.inRange(out, new Scalar(red[0], green[0], blue[0]),
			new Scalar(red[1], green[1], blue[1]), out);
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
	 * Computes the per channel or of two images.
	 * @param src1 The first image to use.
	 * @param src2 The second image to use.
	 * @param dst the result image when the or is performed.
	 */
	private void cvBitwiseOr(Mat src1, Mat src2, Mat dst) {
		Core.bitwise_or(src1, src2, dst);
	}
	
	/**
	 * Computes the per channel and of two images.
	 * @param src1 The first image to use.
	 * @param src2 The second image to use.
	 * @param dst the result image when the and is performed.
	 */
	private void cvBitwiseAnd(Mat src1, Mat src2, Mat dst) {
		Core.bitwise_and(src1, src2, dst);
	}

	/**
	 * Expands area of higher value in an image.
	 * @param src the Image to dilate.
	 * @param kernel the kernel for dilation.
	 * @param anchor the center of the kernel.
	 * @param iterations the number of times to perform the dilation.
	 * @param borderType pixel extrapolation method.
	 * @param borderValue value to be used for a constant border.
	 * @param dst Output Image.
	 */
	private void cvDilate(Mat src, Mat kernel, Point anchor, double iterations,
	int borderType, Scalar borderValue, Mat dst) {
		if (kernel == null) {
			kernel = new Mat();
		}
		if (anchor == null) {
			anchor = new Point(-1,-1);
		}
		if (borderValue == null){
			borderValue = new Scalar(-1);
		}
		Imgproc.dilate(src, dst, kernel, anchor, (int)iterations, borderType, borderValue);
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
	
	public static void updateNT(String id, double a) {
		table.putNumber(id, a);
	}
	public static void updateNT(String id, boolean a) {
		table.putBoolean(id, a);
	}
	
	private static void print(Object a) {
		System.out.println(a);
	}
	
//	private void printInfo() {
//	print("");
//	double distance = m.distanceCalculation(filterContoursOutput);
//	print("Distance: " + distance);
//	updateNT("Distance", distance);
//	
//	double width = m.widthtoFeet(filterContoursOutput);
//	print("Width: " + width);
//	updateNT("Width", width);
//	
//	double length = Math.sqrt(Math.pow(distance, 2) + Math.pow(width, 2));
//	print("Length: " + length);
//	updateNT("Lenght", length);
//	
//	double alpha = Math.atan(width / distance);
//	print("alpha: " + alpha);
//	updateNT("Alpha", alpha);
//	
//	print("");
//}
}
