
import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ContourFilter {

	private Mat picture;
	private ArrayList<Rect> boundingRectangles = new ArrayList<Rect>();
	
	//area of strips = 20 in
	//area of target = 51.25 in
	
	ContourFilter(Mat picture) {
		this.picture = picture;
	}
	/**
	 * Convert contours into bounding rectangles.
	 * @param ArrayList<MatOfPoint>contours 
	 */
	void getBoundingBoxes(ArrayList<MatOfPoint> contours){
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		 
		for(int i = 0; i < contours.size(); i++) {
			//Convert contours(i) from MatOfPoint to MatOfPoint2f
			MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
	        
	        //Processing on mMOP2f1 which is in type MatOfPoint2f
	        double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
	        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

	        //Convert back to MatOfPoint
	        MatOfPoint points = new MatOfPoint(approxCurve.toArray());
	        
	        //Get bounding rect of contour
	        Rect rect = Imgproc.boundingRect(points);
	        
	        //Add rectangle to list of possible targets
	        boundingRectangles.add(rect);
	        
		}
		//System.out.println(boundingRectangles.toString());
        //System.out.println();
	}
	
	/**
	 * Filter all bounding rectangles by ratio of width to height.
	 * @return boundingRectangles
	 */
	ArrayList<Rect> filterByRatio() {
		//each rectangle should have an ratio of 5:2 (2.5 +- .1) for peg
		//2:15 or 4:15 for strips
		int i = 0;
		while(i < boundingRectangles.size()) {
			float rectWidth = boundingRectangles.get(i).width;
			float rectHeight = boundingRectangles.get(i).height;
			
			float ratio = rectHeight / rectWidth;
			
			//System.out.println("Rectangle (" + boundingRectangles.get(i).x + "," + boundingRectangles.get(i).y + ") ratio: " + ratio);
			
			if(ratio < 2 || ratio > 3) {
				boundingRectangles.remove(i);
			}
			else {
				i++;
			}
		}
		return boundingRectangles;
	}
	
	/**
	 * Draw rectangles on image file.
	 */
	void drawRects() {
		for(int i = 0; i < boundingRectangles.size(); i++){
			Rect rect = boundingRectangles.get(i);
			// draw enclosing rectangle (all same color, but you could use variable i to make them unique)
	        Imgproc.rectangle(picture, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255, 0, 0, 255), 1);
		}
		//System.out.println(boundingRectangles.toString());
	}
	
	/**
	 * High Goal
	 * Check that bounding rectangles are within set range of each other. Remove all rectangles that are not.
	 * @return boundingRectangles
	 */
	ArrayList<Rect> checkContours() {
		try {
			ArrayList<Rect[]> pairs = new ArrayList<Rect[]>();
			for(int i = 0; i < boundingRectangles.size(); i++) {
				Rect rect = boundingRectangles.get(i);
				double minY = rect.y - (rect.height/2);
			
					for(int j = i+1; j < boundingRectangles.size(); j++) {
						Rect rect2 = boundingRectangles.get(j);
						double minY2 = rect2.y - (rect2.height/2);
						
						if((minY - 10 <= minY2) && (minY + 10 >= minY2)) {
							if((rect.height - 15 <= rect2.height) && (rect.height + 15 >= rect2.height)) {
								pairs.add(new Rect[] {rect, rect2});
							}
							pairs.add(new Rect[] {rect, rect2});
						}
					}
			}
			boundingRectangles.clear();
			
			for(int i = 0; i < pairs.size(); i++) {
				Rect[] r = pairs.get(i);
				//this might need to be changed because r[0].x is top left corner not center
				double minX = r[0].x - (r[0].width/2);
				double minX2 = r[1].x - (r[1].width/2);
				
				double maxX = r[0].x + r[0].width;
				double maxX2 = r[1].x + r[1].width;
				
				if((Math.abs(maxX2 - minX)) <= 20 || (Math.abs(minX2 - maxX)) <= 20) {
					boundingRectangles.add(r[0]);
					boundingRectangles.add(r[1]);
					break;
				}
			}
			return boundingRectangles;
		}
		catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Low Goal
	 * Check that bounding rectangles are within set range of each other. Remove all rectangles that are not.
	 * @return boundingRectangles
	 */
	ArrayList<Rect> checkContoursLG() {
		//filterByRatio();
		try {
			ArrayList<Rect[]> pairs = new ArrayList<Rect[]>();
			for(int i = 0; i < boundingRectangles.size(); i++) {
				Rect rect = boundingRectangles.get(i);
				double minY = rect.y - (rect.height/2);
			//System.out.println("minY:"+minY);
				for(int j = i+1; j < boundingRectangles.size(); j++) {
					Rect rect2 = boundingRectangles.get(j);
					double minY2 = rect2.y - (rect2.height/2);
					//System.out.println("minY2:"+minY2);
					if((minY - 5 <= minY2) && (minY + 5 >= minY2)) {
						if((rect.height - 10 <= rect2.height) && (rect.height + 10 >= rect2.height)) {
							pairs.add(new Rect[] {rect, rect2});
						}
						//pairs.add(new Rect[] {rect, rect2});
					}
				}
			}
			boundingRectangles.clear();
			
			for(int i = 0; i < pairs.size(); i++) {
				Rect[] r = pairs.get(i);
				//this might need to be changed because r[0].x is top left corner not center
				double minX = r[0].x - (r[0].width/2);
				double minX2 = r[1].x - (r[1].width/2);
				
				double maxX = r[0].x + r[0].width;
				double maxX2 = r[1].x + r[1].width;
				
				if((Math.abs(maxX2 - minX)) <= 150 || (Math.abs(minX2 - maxX)) <= 150) {
					boundingRectangles.add(r[0]);
					boundingRectangles.add(r[1]);
					break;
				}
			}
			return boundingRectangles;
		}
		catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
//	void filterByRatioHigh(){
//		//each rectangle should have an ratio of 5:2 (2.5 +- .1) for peg
//		//15:2 or 15:4 for strips
//		int i = 0;
//		while (i < boundingRectangles.size()){
//			float rectWidth = boundingRectangles.get(i).width;
//			float rectHeight = boundingRectangles.get(i).height;
//			
//			float ratio = rectHeight / rectWidth;
//			
//			System.out.println("Rectangle (" + boundingRectangles.get(i).x + "," + boundingRectangles.get(i).y + ") ratio: " + ratio);
//			
//			//if ((ratio > 2.55 && ratio < 2.65) || (ratio > 3.5 && ratio < 3.8)){
//			//if ((ratio > 2.3 && ratio < 2.8) || (ratio > 3.2 && ratio < 3.8)){
//			if (ratio > 2){
//				i++;
//			}
//			else{
//				boundingRectangles.remove(i);
//			}
//		}
//	}
}
