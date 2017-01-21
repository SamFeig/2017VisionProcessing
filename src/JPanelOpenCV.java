import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class JPanelOpenCV /*extends JPanel*/{

    BufferedImage image;
    static VideoCapture camera;

    public static void main (String args[]) throws InterruptedException, org.bytedeco.javacv.FrameGrabber.Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //JPanelOpenCV t = new JPanelOpenCV();
        
        //FrameRecorder recorder = null;
        try {
        camera = new VideoCapture(0);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        //FrameGrabber grabber = FrameGrabber.createDefault(0);
        //grabber.start();
        //System.out.println(grabber.grab());

        //Mat frame = new Mat();
        //while(true) {
         
        //OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        
        long i = 0;
        while(i < 100) {
        	i++;
        	camera.grab();
        //IplImage grabbedImage = converter.convert(grabber.grab());
        //IplImage iplImage= cvLoadImage("image.png");

       /* Mat matImage = new Mat(grabbedImage, true);
        //matImage = grabbedImage;
        //IplImage img;
        */ 
        	/*
        bmp = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888);

        bmp.copyPixelsFromBuffer(img.getByteBuffer());

        Mat mROI = new Mat(new Size(img.width(), img.height()), CV_8UC4);

        Utils.bitmapToMat(bmp, mROI);
        */
        //BufferedImage image = IplImageToBufferedImage(grabbedImage);
        //int width  = grabbedImage.width();
        //int height = grabbedImage.height();
        
        /*
		try {
			recorder = FrameRecorder.createDefault("output.avi", width, height);
			recorder.start();
		} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
		}*/
        
        //CanvasFrame display = new CanvasFrame("Camera", CanvasFrame.getDefaultGamma()/grabber.getGamma());
		//t.window(image, "Original Image", 0, 0);
        //while (t.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            //cvClearMemStorage(storage);
        	//t.repaint();
            
       // }
        
        }
           /* while(true){        
            		camera.read(frame);
                    BufferedImage image = t.MatToBufferedImage(frame);
                    
                    t.window(image, "Original Image", 0, 0);
                    t.repaint();
                    //t.window(t.grayscale(image), "Processed Image", 40, 60);

                    //t.window(t.loadImage("ImageName"), "Image loaded", 0, 0);
                    
                }  */
           /* try {
				//recorder.stop();
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
           // grabber.stop();
        //camera.release();
    }
    
  /*  public static BufferedImage IplImageToBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }*/
/*
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public JPanelOpenCV() {
    }

    public JPanelOpenCV(BufferedImage img) {
        image = img;
    }   

    //Show image on window
    public void window(BufferedImage img, String text, int x, int y) {
        JFrame frame0 = new JFrame();
        frame0.getContentPane().add(new JPanelOpenCV(img));
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame0.setTitle(text);
        frame0.setSize(img.getWidth(), img.getHeight() + 30);
        frame0.setLocation(x, y);
        frame0.setVisible(true);
       /*try {
		wait(100000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}*/
    /*}

    //Load an image
    public BufferedImage loadImage(String file) {
        BufferedImage img;

        try {
            File input = new File(file);
            img = ImageIO.read(input);

            return img;
        } catch (Exception e) {
            System.out.println("erro");
        }

        return null;
    }

    //Save an image
    public void saveImage(BufferedImage img) {        
        try {
            File outputfile = new File("Images/new.png");
            ImageIO.write(img, "png", outputfile);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    public BufferedImage MatToBufferedImage(Mat frame) {
        //Mat() to BufferedImage
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }
*/
}
