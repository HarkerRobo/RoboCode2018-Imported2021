package frc.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * @author joel
 *
 */
public class Slot {
	public static Slot
	LEFT_VELOCITY = new Slot(2, 0.7 * 1023.0 / 3000.0, 0, 0, 0, 0, 0, 0),
	RIGHT_VELOCITY = new Slot(2, 0.7 * 1023.0 / 3000.0, 0, 0, 0, 0, 0,0),
	LEFT_POSITION = new EmptySlot(),
	RIGHT_POSITION = new EmptySlot(),
//	LEFT_MOTION_PROFILE(2, 0.7 * 1023.0 / 3000.0, 0.04, 0.000006, 0, 4096, 0, 0), //Carpet
//	RIGHT_MOTION_PROFILE(2, 0.65 * 1023.0 / 3000.0, 0.04, 0.000006, 0, 4096, 0, 0), //Carpet
//	LEFT_MOTION_PROFILE(0, 0.3 * 1023.0 / 1080.0, 0.065, 0.001, 0.00004, 200, 800, 10), //Floor
//	RIGHT_MOTION_PROFILE(0, 0.3 * 1023.0 / 1100.0, 0.065, 0.001, 0.00004, 200, 800, 10), //Floor
//	LEFT_MOTION_PROFILE(0, 1.0 * 1023.0 / 4900.0, 0.085, 0.0015, 0.00004, 200, 800, 0), //Floor fast
//	RIGHT_MOTION_PROFILE(0, 1.0 * 1023.0 / 4800.0, 0.085, 0.0015, 0.00004, 200, 800, 0), //Floor fast
//	LEFT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 4860.0, 0.2 * 1023.0 / 4860.0, 0 * 0.001 * 1023.0 / 4860.0, 0.0000, 5000, 500000, 50), //Floor fast - practice
//	RIGHT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 4800.0, 0.2 * 1023.0 / 4800.0, 0 * 0.001 * 1023.0 / 4800.0, 0.0000, 5000, 500000, 50), //Floor fast - practice
//	LEFT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 4600.0, 0.3 * 1023.0 / 4600.0, 0.0012 * 1023.0 / 4600.0, 0.003, 5000, 500000, 50), //Carpet fast - comp
//	RIGHT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 4600.0, 0.3 * 1023.0 / 4600.0, 0.0012 * 1023.0 / 4600.0, 0.003, 5000, 500000, 50), //Carpet fast - comp
//	LEFT_MOTION_PROFILE = new Slot(0, 0.9 * 1023.0 / 4600.0, 0.4 * 1023.0 / 4600.0, 0.0012 * 1023.0 / 4600.0, 0.003, 5000, 500000, 0), //Carpet fast - practice
//	RIGHT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 4600.0, 0.4 * 1023.0 / 4600.0, 0.0012 * 1023.0 / 4600.0, 0.003, 5000, 500000, 0), //Carpet fast - practice
//	LEFT_MOTION_PROFILE = new Slot(0, 0.9 * 1023.0 / 5200.0, 0.4 * 1023.0 / 5200.0, 0.0012 * 1023.0 / 5200.0, 0.003, 5000, 500000, 0), //Floor fast - practice new
//	RIGHT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 5200.0, 0.4 * 1023.0 / 5200.0, 0.0012 * 1023.0 / 5200.0, 0.003, 5000, 500000, 0), //Floor fast - practice new
//	LEFT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 5200.0, 0.5 * 1023.0 / 5000.0, 0.0012 * 1023.0 / 5000.0, 0.0026, 5000, 500000, 10), //Floor new - practice
//	RIGHT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 5200.0, 0.6 * 1023.0 / 5000.0, 0.0012 * 1023.0 / 5000.0, 0.0026, 5000, 500000, 10), //Floor new - practice
		LEFT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 4600.0, 0.4 * 1023.0 / 4600.0, 0.0012 * 1023.0 / 4600.0, 0.003, 5000, 500000, 0), //FINAL COMP
		RIGHT_MOTION_PROFILE = new Slot(0, 1.0 * 1023.0 / 4500.0, 0.4 * 1023.0 / 4600.0, 0.0012 * 1023.0 / 4600.0, 0.003, 5000, 500000, 0), //FINAL COMP
	LEFT_YAW = new Slot(1, 0, 1.0, 0.000, 0.0, 1000, 100000, 0),
	RIGHT_YAW = new Slot(1, 0, 1.0, 0.000, 0.0, 1000, 100000, 0),
	ELEVATOR_POSITION = new Slot(3, 0, 0.07, 0.000016, 0.0001, 20000, 10000000, 0),
	ELEVATOR_SMALL_POSITION = new Slot(2, 0, 0.14, 0.000012, 0.0001, 20000, 10000000, 0),
	ELEVATOR_VELOCITY = new Slot(1, 1.02 * 1023.0 / 2500.0, 0.5 * 1023.0 / 2500.0, 0.0005 * 1023.0 / 2500.0, 0.06 * 1023 / 2500, 3000, 7500000, 0),
	ELEVATOR_MOTION_MAGIC = new Slot(0, 1.0 * 1023.0 / 2500.0, 0.47, 0.0014, 0.01, 20000, 10000000, 0);
	
	private int slot, integralZone, allowableError;
	private double kF, kP, kI, kD, maxIntegral;
	
	public static final Slot read(int slot, String filename) {
		File file = new File("/home/lvuser/tuning/" + filename + ".txt");
		if(file.exists() && file.isFile())
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				double kF = Double.parseDouble(br.readLine());
				double kP = Double.parseDouble(br.readLine());
				double kI = Double.parseDouble(br.readLine());
				double kD = Double.parseDouble(br.readLine());
				int integralZone = Integer.parseInt(br.readLine());
				double maxIntegral = Double.parseDouble(br.readLine());
				int allowableError = Integer.parseInt(br.readLine());
				br.close();
				return new Slot(slot, kF, kP, kI, kD, integralZone, maxIntegral, allowableError);
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(NumberFormatException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		return new EmptySlot();
	}
	
	public static void write(Slot slot, String filename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("/home/lvuser/tuning/" + filename + ".txt");
		pw.println(slot.kF);
		pw.println(slot.kP);
		pw.println(slot.kI);
		pw.println(slot.kD);
		pw.println(slot.integralZone);
		pw.println(slot.maxIntegral);
		pw.println(slot.allowableError);
		pw.flush();
		pw.close();
	}
	
	/**
	 * @param slot
	 * @param kF
	 * @param kP
	 * @param kI
	 * @param kD
	 * @param integralZone
	 */
	private Slot(int slot, double kF, double kP, double kI, double kD, int integralZone, double maxIntegral,
			int allowableError) {
		this.slot = slot;
		this.kF = kF;
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
		this.integralZone = integralZone;
		this.maxIntegral = maxIntegral;
		this.allowableError = allowableError;
	}
	
	public boolean configure(TalonSRX talon, int TIMEOUT) {
		return talon.config_kF(slot, kF, TIMEOUT) == ErrorCode.OK && talon.config_kP(slot, kP, TIMEOUT) == ErrorCode.OK
				&& talon.config_kI(slot, kI, TIMEOUT) == ErrorCode.OK
				&& talon.config_kD(slot, kD, TIMEOUT) == ErrorCode.OK
				&& talon.config_IntegralZone(slot, integralZone, TIMEOUT) == ErrorCode.OK
				&& talon.configMaxIntegralAccumulator(slot, maxIntegral, TIMEOUT) == ErrorCode.OK
				&& talon.configAllowableClosedloopError(slot, allowableError, TIMEOUT) == ErrorCode.OK;
	}

	/**
	 * @return the slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @return the integralZone
	 */
	public int getIntegralZone() {
		return integralZone;
	}

	/**
	 * @return the allowableError
	 */
	public int getAllowableError() {
		return allowableError;
	}

	/**
	 * @return the kF
	 */
	public double getkF() {
		return kF;
	}

	/**
	 * @return the kP
	 */
	public double getkP() {
		return kP;
	}

	/**
	 * @return the kI
	 */
	public double getkI() {
		return kI;
	}

	/**
	 * @return the kD
	 */
	public double getkD() {
		return kD;
	}

	/**
	 * @return the maxIntegral
	 */
	public double getMaxIntegral() {
		return maxIntegral;
	}
	
	static class EmptySlot extends Slot {
		public EmptySlot() {
			super(0, 0, 0, 0, 0, 0, 0, 0);
		}

		/* (non-Javadoc)
		 * @see frc.robot.Slot#configure(com.ctre.phoenix.motorcontrol.can.TalonSRX, int)
		 */
		@Override
		public boolean configure(TalonSRX talon, int TIMEOUT) {
			return false;
		}
	}
}
