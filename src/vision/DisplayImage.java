package vision;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.tables.ITable;

public class DisplayImage extends JPanel {
	//private Mat source0;
	
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    
    private static BufferedImage image;
    
	public static class JPanelOpenCV {
		
	    
		private static Mat source0;

	    //BufferedImage image;

	    public static void main (String args[]) throws InterruptedException {
	    	 UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
             camera.setResolution(640, 480);
             
             //camera.
             
             CvSink cvSink = CameraServer.getInstance().getVideo();
            //send data to dashboard //CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
             
             source0 = new Mat();
             //Mat output = new Mat();
             
             while(true) {
                 cvSink.grabFrame(source0);
                 imshow(source0);
             }       
	    }
	    
	    public static void imshow(Mat src){
	        BufferedImage bufImage = null;
	        try {
	            MatOfByte matOfByte = new MatOfByte();
	            Imgcodecs.imencode(".jpg", src, matOfByte); 
	            byte[] byteArray = matOfByte.toArray();
	            InputStream in = new ByteArrayInputStream(byteArray);
	            bufImage = ImageIO.read(in);

	            JFrame frame = new JFrame("Image");
	            frame.getContentPane().setLayout(new FlowLayout());
	            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
	            frame.pack();
	            frame.setVisible(true);
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}
