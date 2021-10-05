/*----------------------------------------------------------------------------*/
/*
 * Copyright (c) 2017-2018 FIRST. All Rights Reserved. Open Source Software -
 * may be modified and shared by FRC teams. The code must be accompanied by the
 * FIRST BSD license file in the root directory of the project.
 */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.PrintWriter;

import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;

// import frc.robot.subsystems.Drivetrain;
// import frc.robot.subsystems.Elevator;
// import frc.robot.subsystems.Intake;

import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	
	public static int TIMEOUT;
	
	public static final boolean IS_COMP = true;
	
	
//	public static final PowerDistributionPanel pdp = new PowerDistributionPanel();
	public static final Drivetrain drivetrain = Drivetrain.getInstance();
	public static final Elevator elevator = Elevator.getInstance();
	public static final Intake intake = Intake.getInstance();
	public static final Compressor compressor = new Compressor();
	
//	public static final PigeonIMU pigeon = new PigeonIMU(intake.getRightRoller());
	
	public static PrintWriter pw = null;
	
	//Roll and Pitch are switched
	//left is positive pitch, right is negative pitch, forwards is negative roll, backwards is negative roll
	
	public static boolean falling = false;
	
	public static enum Position {
		LEFT, LEFT_SWITCH, MID, RIGHT_SWITCH, RIGHT
	}
	
	public static enum Goal {
		SWITCH, SWITCH_SIDE, SCALE, LINE
	}
	
	private ClockAdapter rahulClock = new ClockAdapter();
	
	double [] ypr;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		if(!SmartDashboard.containsKey("LLL Wait"))
			SmartDashboard.putNumber("LLL Wait", 0.0);
		if(!SmartDashboard.containsKey("LRL Wait"))
			SmartDashboard.putNumber("LRL Wait", 0.0);
		if(!SmartDashboard.containsKey("RLR Wait"))
			SmartDashboard.putNumber("RLR Wait", 0.0);
		if(!SmartDashboard.containsKey("RRR Wait"))
			SmartDashboard.putNumber("RRR Wait", 0.0);
		if(!SmartDashboard.containsKey("Position Override")){
			SmartDashboard.putNumber("Position Override", 0);
		}
//		pigeon.configTemperatureCompensationEnable(true, 100);
//		pdp.clearStickyFaults();
		compressor.setClosedLoopControl(true);
		OI.initializeCommandBindings();
		 ypr = new double [3];
//		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
//		cam.setResolution(640, 480);
		rahulClock.start();
	}
	
	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.IterativeRobotBase#robotPeriodic()
	 */
	@Override
	public void robotPeriodic() {
		if(pw != null)
			pw.flush();
//		SmartDashboard.putNumber("pigeon heading", pigeon.getFusedHeading());
//		pigeon.getYawPitchRoll(ypr);
//		SmartDashboard.putNumber("pigeon yaw", ypr[0]);
//		SmartDashboard.putNumber("pigeon pitch", ypr[1]);
//		SmartDashboard.putNumber("pigeon roll", ypr[2]);
//		SmartDashboard.putNumber("elevator current", elevator.getMaster().getOutputCurrent());
//		SmartDashboard.putNumber("elevator speed", elevator.getMaster().getSelectedSensorVelocity(0));
////		for(int i = 0; i < 4; i++) {
//			SmartDashboard.putNumber("POV", OI.operator.getPOV());
//		}
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		log("Disable");
		drivetrain.set((talon) -> talon.set(ControlMode.Disabled, 0));
		elevator.getMaster().set(ControlMode.Disabled, 0);
		intake.set((talon) -> talon.set(ControlMode.Disabled, 0));
		elevator.getMaster().configForwardSoftLimitEnable(false, 0);
		elevator.getMaster().configReverseSoftLimitEnable(false, 0);
		intake.raise();
		intake.close();
		if(pw != null)
			pw.close();
		pw = null;
	}
	
	@Override
	public void disabledPeriodic() {
		CommandScheduler.getInstance().run();
	}
	
	@Override
	public void teleopInit() {
//		pdp.clearStickyFaults();
//		new ZeroElevatorCommand().start();
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		log("Teleop initialized");
		rahulClock.sendTime(135);
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		CommandScheduler.getInstance().run();
	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
	}
	
	public static void log(String line) {
		System.out.println(line);
		if(pw != null)
			pw.println((150.0 - DriverStation.getInstance().getMatchTime()) + ": " + line);
	}
}
