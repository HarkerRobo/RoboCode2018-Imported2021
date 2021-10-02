/*----------------------------------------------------------------------------*/
/*
 * Copyright (c) 2017-2018 FIRST. All Rights Reserved. Open Source Software -
 * may be modified and shared by FRC teams. The code must be accompanied by the
 * FIRST BSD license file in the root directory of the project.
 */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import frc.harkerrobolib.wrappers.PneumaticsWrapper;
import frc.robot.commands.auton.AutonomousCommand;
import frc.robot.commands.elevator.ZeroElevatorCommand;
import frc.robot.profiling.MotionProfileBuilder;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;

// import frc.robot.subsystems.Drivetrain;
// import frc.robot.subsystems.Elevator;
// import frc.robot.subsystems.Intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.sensors.PigeonIMU;

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
	
	private String gameData = null;
	
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
	
	public static final SmartEnum<Position> position = new SmartEnum<Position>(Position.MID);
	public static final SmartEnum<Goal> LLL = new SmartEnum<Goal>(Goal.SWITCH, "LLL");
	public static final SmartEnum<Goal> LRL = new SmartEnum<Goal>(Goal.SWITCH, "LRL");
	public static final SmartEnum<Goal> RLR = new SmartEnum<Goal>(Goal.SWITCH, "RLR");
	public static final SmartEnum<Goal> RRR = new SmartEnum<Goal>(Goal.SWITCH, "RRR");
	
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
		Scheduler.getInstance().run();
		position.refresh();
		LLL.refresh();
		LRL.refresh();
		RLR.refresh();
		RRR.refresh();
	}
	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		gameData = null;
		String name = DriverStation.getInstance().getEventName() + DriverStation.getInstance().getMatchType() + "Match" + DriverStation.getInstance().getMatchNumber() + ".txt";
		name = name.replaceAll(" ", "");
		try {
			pw = new PrintWriter("/home/lvuser/logs/" + name);
		} catch(Exception e) {
			e.printStackTrace();
		}
//		try {
//			Socket s = new Socket();
//			PrintWriter out = new PrintWriter(s.getOutputStream());
//			out.println("150");
//			out.close();
//			s.close();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		log("Autonomous initialized");
//		new ZeroElevatorCommand().start();
//		try {
//			Socket s = new Socket("10.10.72.84", 54);
//			PrintWriter pw = new PrintWriter(s.getOutputStream());
//			pw.println("match start!");
//			pw.close();
//			s.close();
//		} catch(UnknownHostException e) {
//			e.printStackTrace();
//		} catch(IOException e) {
//			e.printStackTrace();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			Trajectory left = OI.readTrajectory("/home/lvuser/paths/leftPath10.csv"),
//					right = OI.readTrajectory("/home/lvuser/paths/rightPath10.csv");
//			new MotionProfileBuilder(10, Robot.drivetrain)
//			.group(left, Slot.LEFT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI / 12.0/*0.31918*/, 0.945, Robot.drivetrain.getLeft())
//			.group(left, Slot.RIGHT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI / 12.0/*0.31918*/, 1.0, Robot.drivetrain.getRight()).build().start();
//		} catch (FileNotFoundException e) {
//			System.err.println("Failed to read trajectory");
//		}
		
		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */
		
		// schedule the autonomous command (example)
		rahulClock.sendTime(15);
	}
	
	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		if(gameData == null || gameData.length() != 3) {
			gameData = DriverStation.getInstance().getGameSpecificMessage();
			if(gameData != null && gameData.length() == 3) {
				String autonPath = "/home/lvuser/paths/";
				int override = (int) SmartDashboard.getNumber("Position Override", 0);
				if(override == 0){
					switch(position.get()) {
						case LEFT: autonPath += "Left"; break;
						case LEFT_SWITCH: autonPath += "LeftSwitch"; break;
						case MID: autonPath += "Mid"; break;
						case RIGHT_SWITCH: autonPath += "RightSwitch"; break;
						case RIGHT: autonPath += "Right"; break;
						default: log("No autonomous position"); return;
					}
				} else {
					autonPath += "Mid";
				}
//				autonPath += "Mid";
				autonPath += "To";
				Goal goal;
				double wait = SmartDashboard.getNumber(gameData + " Wait", 0.0);
				double height = 0.0;
				switch(gameData) {
					case "LLL": goal = LLL.get(); break;
					case "LRL": goal = LRL.get(); break;
					case "RLR": goal = RLR.get(); break;
					case "RRR": goal = RRR.get(); break;
					default: log("Invalid game data"); return;
				}
				if(override == 0){
					switch(goal) {
						case SWITCH: autonPath += gameData.charAt(0) == 'L' ? "LeftSwitch" : "RightSwitch"; height = 1.6; break;
						case SWITCH_SIDE: autonPath += gameData.charAt(0) == 'L' ? "LeftSwitchSide" : "RightSwitchSide"; height = 1.6; break;
						case SCALE: autonPath += gameData.charAt(1) == 'L' ? "LeftScale" : "RightScale"; height = 6.4; break;
						case LINE: autonPath += "Line"; break;
						default: log("No autonomous goal"); return;
					}
				} else {
					autonPath += gameData.charAt(0) == 'L' ? "LeftSwitch" : "RightSwitch"; height = 1.6;
				}
//				autonPath += gameData.charAt(1) == 'R' ? "RightScale" : (gameData.charAt(0) == 'R' ? "RightSwitchSide" : "Line");
//				height = gameData.charAt(1) == 'R' ? 6.4 : (gameData.charAt(0) == 'R' ? 1.6 : 0);
				log("Running: " + autonPath);
//				autonPath = "/home/lvuser/paths/ThreeFeet";
//				autonPath = "/home/lvuser/paths/RightToLine";
				String leftPath = autonPath + "_left_detailed.csv";
				String rightPath = autonPath + "_right_detailed.csv";
				try {
					Trajectory left = readTrajectory(leftPath);
					Trajectory right = readTrajectory(rightPath);
					AutonomousCommand auton = new AutonomousCommand(
							new MotionProfileBuilder(10, Robot.drivetrain)
							.group(left, Slot.LEFT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI
									/ 12.0/* 0.31918 */, 1.0/*0.945*/, Robot.drivetrain.getLeft())
							.group(right, Slot.RIGHT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI
									/ 12.0/* 0.31918 */, 1.0, Robot.drivetrain.getRight())
							.build(),
					height, wait);
					auton.start();
				} catch(FileNotFoundException e) {
					e.printStackTrace();
					try {
						Trajectory left = readTrajectory("/home/lvuser/paths/RightToLine_left_detailed.csv");
						Trajectory right = readTrajectory("/home/lvuser/paths/RightToLine_left_detailed.csv");
						AutonomousCommand auton = new AutonomousCommand(
								new MotionProfileBuilder(10, Robot.drivetrain)
								.group(left, Slot.LEFT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI
										/ 12.0/* 0.31918 */, 1.0/*0.945*/, Robot.drivetrain.getLeft())
								.group(right, Slot.RIGHT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI
										/ 12.0/* 0.31918 */, 1.0, Robot.drivetrain.getRight())
								.build(),
						height, wait);
						auton.start();
					} catch(FileNotFoundException e2){
						e2.printStackTrace();
					}
				}
			}
		}
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
		Scheduler.getInstance().run();
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
	
	public static Trajectory readTrajectory(String filename) throws FileNotFoundException {
		File f = new File(filename);
		if(f.exists() && f.isFile() && filename.endsWith(".csv")) {
			try {
				return Pathfinder.readFromCSV(f);
			} catch(Exception e) {
				log("Pathfinder failed to read trajectory: " + filename);
			}
		} else {
			log("Trajectory: " + filename + ", does not exist or is not a csv file");
		}
		throw new FileNotFoundException("No valid csv file by that name: " + filename);
	}
}
