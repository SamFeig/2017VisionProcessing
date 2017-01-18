package Vision;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Camera extends Subsystem {
	public static final double T_FT = 0.85416667; //10.5 in
	public static final double FOV_PIXEL = 1280; //Camera resolution width
	public static double T_PIXEL = 292; //dependent on GRIP processing
    public static final double ANGLE = Math.toRadians(43); //degrees converted to radians
    
    public static void main(String args[]) {
    	
    		double distance = (T_FT * FOV_PIXEL) / (2.0 * T_PIXEL * Math.tan(ANGLE));
    		//System.out.println("Angle: " + angle);
    		System.out.println("Distance: " + distance);
    		
    }
    
	@Override
	protected void initDefaultCommand() {
		
	}

}
