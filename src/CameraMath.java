import java.util.ArrayList;
import java.util.List;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class CameraMath {
	private static final double TOP_TARGET_HEIGHT = 82.0;//in
	private static final double TOP_CAMERA_HEIGHT = 15;//22.75;//in
	private static final double CAMERA_ANGLE = 61.0;//55.0//degrees
	private final double PHYSICAL_HEIGHT = 0.416666;//ft
	private final double PHYSICAL_WIDTH = 0.83333;//ft
	private final int PHYSICAL_WIDTH_HG = 10;//in
	private final int PHYSICAL_HEIGHT_HG = 14;//in
	private final double IMAGE_WIDTH = 320;//640;//FOVpixel
	private final double IMAGE_WIDTH_HG = 320;//480;//FOVpixel
	private final int IMAGE_HEIGHT = 240;//Pixels
	private final double IMAGE_CENTER_HG = 120;//240//Pixels
	private final double IMAGE_CENTER_LG = 160;//320//Pixels
	private final double FOV = 60;
	private double apparentHeight;
	private double apparentWidth;
	private double apparentCenter;
	private final double ANGLE = 21.2;

	private static double kHG = 6.78;//constant
	private static double k = 5.905;//4.27;//constant
	private static double dpp = 63.0/4.0;//21.3333333 deg/pix
	
	public CameraMath() {
	}
	
	public double calcViewAngle() {
		double viewAngle = Math.acos(Math.toRadians(apparentHeight/calcActualHight()));
		return viewAngle;
	}
	
	public double calcViewAngleHG() {
		double viewAngle = Math.acos(Math.toRadians(apparentHeight/calcActualHightHG()));
		return viewAngle;
	}
	
	public double calcActualHight() {
		double actualHight = apparentHeight * (PHYSICAL_HEIGHT/PHYSICAL_WIDTH);
		return actualHight;
	}
	
	public double calcActualHightHG() {
		double actualHight = apparentHeight * (PHYSICAL_HEIGHT_HG/PHYSICAL_WIDTH_HG);
		return actualHight;
	}
	
	public double calcDistanceToGoal() {
		double distanceToGoal = PHYSICAL_WIDTH/Math.tan(Math.toRadians(calcTheta()));
		return distanceToGoal;
	}
	
	public double calcDistanceToGoalHG() {
		//double y = -((2 * (apparentHeight / IMAGE_HEIGHT)) - 1);
		//double distanceToGoal = (TOP_TARGET_HEIGHT - TOP_CAMERA_HEIGHT) /
				//Math.tan(Math.toRadians((y * FOV / 2.0 + CAMERA_ANGLE)));
		//double distanceToGoal = PHYSICAL_WIDTH_HG/Math.tan(Math.toRadians(calcThetaHG()));
		double distanceToGoal = (6846.6 * Math.pow(apparentHeight, -1.068)) / 12.0;
;
		return distanceToGoal;
	}
	
	public double calcThetaHG() {
		double theta = kHG*(apparentWidth/IMAGE_WIDTH_HG)*FOV;
		return theta;
	}
	
	public double calcTheta() {
		double theta = k*(apparentWidth/IMAGE_WIDTH)*FOV;
		return theta;
	}
	
	public double calcGroundDistance() {
		//double groundDistance = calcDistanceToGoal()*Math.cos(Math.toRadians(calcViewAngle()));
		double groundDistance = (PHYSICAL_WIDTH*IMAGE_WIDTH)/(2*apparentWidth*Math.tan(Math.toRadians(ANGLE)));
		return groundDistance;
	}
	
	public double calcOffset() {
		double offset = IMAGE_CENTER_HG - apparentCenter;
		return offset;
	}
	
	public double calcOffsetLG() {
		double offset = IMAGE_CENTER_LG - apparentCenter;
		return offset;
	}
	
	public double offsetToDegrees(double offset) {
		double degrees = offset / dpp;
		return degrees ;
	}
	
	/**
	 * 
	 * @param rect
	 * @param isHighGoal
	 * @return
	 */
	public double math(ArrayList<Rect> rect, boolean isHighGoal) {
		try {
			double pixelHeight;
			double pixelWidth;
			double pixelWidthY;
			if(rect.get(0).height > rect.get(1).height) {
				pixelHeight = rect.get(0).height;
			}
			else {
				pixelHeight = rect.get(1).height;
			}
			
			double minX = rect.get(0).x;// - (rect.get(0).width/2);
			double minX2 = rect.get(1).x;// - (rect.get(1).width/2);
			
			double maxX = rect.get(0).x + rect.get(0).width;//+ (rect.get(0).width/2);
			double maxX2 = rect.get(1).x + rect.get(1).width;//+ (rect.get(1).width/2);
			
			pixelWidth = Math.max(maxX2 - minX, maxX - minX2);
			double maxVal = Math.max(maxX, maxX2);
			//System.out.println(maxVal);
			
//			double maxY = rect.get(0).y + rect.get(0).height;//+ (rect.get(0).height/2);
//			double minY = rect.get(0).y;//- (rect.get(0).height/2);
			
			double minY = rect.get(0).y;// - (rect.get(0).width/2);
			double minY2 = rect.get(1).y;// - (rect.get(1).width/2);
			
			double maxY = rect.get(0).y + rect.get(0).height;//+ (rect.get(0).width/2);
			double maxY2 = rect.get(1).y + rect.get(1).height;//+ (rect.get(1).width/2);
			
			pixelWidthY = Math.max(maxY2 - minY, maxY - minY2);
			double maxValY = Math.max(maxY, maxY2);
//			System.out.println("rectX: " + rect.get(0).x);
//			System.out.println("rectX2: " + rect.get(1).x);
//			System.out.println("maxX: " + maxX);
//			System.out.println("minX: " + minX);
//			System.out.println("maxY: " + maxY);
//			System.out.println("minY: " + minY);
			//double center = pixelHeight / 2;
			//Must use Y because image is rotated
			
			double offset;
			if(isHighGoal) {
				apparentCenter = maxValY - (pixelWidthY / 2);
				offset = calcOffset();
			}
			else {
				apparentCenter = maxVal - (pixelWidth / 2);
				offset = calcOffsetLG();
			}
//			System.out.println("Y: " + pixelHeight);
//			System.out.println("X: " + pixelWidth);
//			//print("MaxX: " + maxX);
//			//print("MinX: " + minX);
//			//print("CenterOfTarget: " + center);
//			System.out.println("C: " + apparentCenter);
			
			//CameraMath a = new CameraMath(pixelWidth, pixelHeight, apparentCenter);
			
			//print("Offset: " + offset);
			//System.out.println("pixels:"+ offset);
			//System.out.println(offset);
			double degrees = offsetToDegrees(offset);
			apparentWidth = pixelWidth;
			apparentHeight = pixelHeight;
	
			//updateNT("Found", found);
			degrees = -degrees;
			//System.out.println("Offset_degrees: " + degrees);
			//updateNT("Offset", offset);
			//updateNT("Degrees", degrees);
			
			//double distance = calcDistanceToGoal();
			
			return degrees;
		}
		catch(IndexOutOfBoundsException e) {
			//e.printStackTrace();
			return 0.0;
		}
	}
	
	//OLD DISTANCE CALC
//	  public double distanceCalculation(ArrayList<MatOfPoint> filterContoursOutput) {
//		for (int i = 0; i < filterContoursOutput.size(); i++){
//			//Imgproc.drawContours(source0, filterContoursOutput, i, new Scalar(0, 0, 0));
//			
//			List<Point> contour = filterContoursOutput.get(i).toList();
//			for (int j = 0; j < contour.size(); j++){
//				if (minX > contour.get(0).x){
//					minX = contour.get(0).x;
//				}
//				
//				if (maxX < contour.get(0).x){
//					maxX = contour.get(0).x;
//				}
//			}
//		}
//		
//		double pixelWidth = maxX - minX;
//		double distance = TARGET_FEET * IMAGE_WIDTH / (2 * pixelWidth * Math.tan(ANGLE));
//		return distance;
//	}
	
//	public double widthtoFeet(ArrayList<MatOfPoint> filterContoursOutput) {
//		double distance = distanceCalculation(filterContoursOutput);
//		//print((maxX - minX) / 2));
//		double widthPix = Math.abs((IMAGE_WIDTH / 2) - ((maxX + minX) / 2));
//		
//		//print("WidthPIX: " + widthPix);
//		//print("Distance: " + distance);
//		
//		return (2 * distance * Math.tan(ANGLE) * widthPix) / IMAGE_WIDTH;
//	}
}
