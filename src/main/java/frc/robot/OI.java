/*----------------------------------------------------------------------------*/
/*
 * Copyright (c) 2017-2018 FIRST. All Rights Reserved. Open Source Software -
 * may be modified and shared by FRC teams. The code must be accompanied by the
 * FIRST BSD license file in the root directory of the project.
 */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.commands.*;
import frc.robot.commands.elevator.LowerElevatorCommand;
import frc.robot.commands.elevator.RaiseElevatorCommand;
import frc.robot.commands.elevator.SetElevatorCommand;
import frc.robot.commands.elevator.SetElevatorCommand2;
import frc.robot.commands.elevator.SmallRaiseCommand;
import frc.robot.commands.elevator.ZeroElevatorCommand;
import frc.robot.commands.intake.IntakeExpansionCommand;
import frc.robot.commands.intake.JiggleCommand;
import frc.robot.commands.intake.LiftIntakeCommand;
import frc.robot.commands.intake.ShootCommand;
import frc.robot.commands.intake.WeirdEjecteyCommand;
import frc.robot.profiling.MotionProfileBuilder;
import frc.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.InstantCommand;

import java.io.File;
import java.io.FileNotFoundException;

import frc.harkerrobolib.wrappers.DPadButtonWrapper;
import frc.harkerrobolib.wrappers.GamepadWrapper;
import frc.harkerrobolib.wrappers.JoystickButtonWrapper;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);
	
	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.
	
	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:
	
	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());
	
	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());
	
	// Start the command when the button is released and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());
	
	public static final GamepadWrapper gamepad = new GamepadWrapper(0);
	
	public static final GamepadWrapper operator = new GamepadWrapper(1, GamepadWrapper.SETTING_LOGITECH);
	
	public static void initializeCommandBindings() {
		
		// Standard controls
		
		gamepad.getButtonBumperLeft().whenPressed(new SetElevatorCommand(2.00)); // Elevator to switch position
		gamepad.getButtonBumperRight().whenPressed(new SetElevatorCommand2(4.0 / 12.0)); // Elevator to vault position
		gamepad.getButtonX().whenPressed(new IntakeExpansionCommand());
		gamepad.getButtonA().whenPressed(new SetElevatorCommand(4.5));
//		gamepad.getButtonA().whenPressed(new CANTestingCommand());
		gamepad.getButtonY().whenPressed(new RaiseElevatorCommand());
		gamepad.getButtonB().whenPressed(new LiftIntakeCommand());
		gamepad.getButtonStickRight().whenPressed(new LowerElevatorCommand());
		gamepad.getButtonStart().whilePressed(new JiggleCommand());
		gamepad.getButtonSelect().whenPressed(new ZeroElevatorCommand());
		
//		operator.getButtonBumperLeft().whilePressed(new OperatorIntakeCommand());
//		operator.getButtonBumperRight().whilePressed(new OperatorOuttakeCommand());
		JoystickButtonWrapper leftTrigger = new JoystickButtonWrapper(operator, GamepadWrapper.LOGITECH_TRIGGER_LEFT);
		JoystickButtonWrapper rightTrigger = new JoystickButtonWrapper(operator, GamepadWrapper.LOGITECH_TRIGGER_RIGHT);
		DPadButtonWrapper dpadUp = new DPadButtonWrapper(operator, 0);
		DPadButtonWrapper dpadDown = new DPadButtonWrapper(operator, 180);
		dpadUp.whenPressed(new InstantCommand() {
			@Override
			public void initialize() {
				Robot.intake.raise();
			}
		});
		dpadDown.whenPressed(new InstantCommand() {
			@Override
			public void initialize() {
				if(Robot.elevator.getMaster().getSelectedSensorPosition(0) < SmallRaiseCommand.DIST)
					Robot.intake.open();
				Robot.intake.lower();
			}
		});
		leftTrigger.whenPressed(new InstantCommand() {
			@Override
			public void initialize() {
				if(Robot.elevator.getMaster().getSelectedSensorPosition(0) > SmallRaiseCommand.DIST) {
					Robot.intake.close();
				} else {
					new SmallRaiseCommand().start();
				}
			}
		});
		rightTrigger.whenPressed(new InstantCommand() {
			@Override
			public void initialize() {
				Robot.intake.open();
			}
		});
		operator.getButtonY().whilePressed(new ShootCommand());
		operator.getButtonA().whilePressed(new WeirdEjecteyCommand());
		// gamepad.getButtonBumperLeft().whenPressed(new TestIntakeCommand());
		// gamepad.getButtonBumperRight().whenPressed(new EjectCommand());
		// gamepad.getButtonA().whenPressed(new SlowRaiseCommand(3.0));
		// gamepad.getButtonB().whenPressed(new ClosedLoopCommand(1920.0));
		// gamepad.getButtonX().whenPressed(new ClosedLoopCommand(-3400.0));
		// Trajectory traj = Pathfinder.readFromCSV(new
		// File("/home/lvuser/path.csv"));
		// System.out.println("Reading trajectories");
		// SmartDashboard.putData(new ZeroEncoders());
//		try {
//			Trajectory left = readTrajectory("/home/lvuser/MidToLeftSwitchLeft.csv"),
//					right = readTrajectory("/home/lvuser/MidToLeftSwitchRight.csv");
//			gamepad.getButtonA()
//					.whenPressed(
//							new AutonomousCommand(
//									new MotionProfileBuilder(10, Robot.drivetrain)
//											.group(left, Slot.LEFT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI
//													/ 12.0/* 0.31918 */, 1.0/*0.945*/, Robot.drivetrain.getLeft())
//											.group(right, Slot.RIGHT_MOTION_PROFILE.getSlot(), 4.0 * Math.PI
//													/ 12.0/* 0.31918 */, 1.0, Robot.drivetrain.getRight())
//											.build(),
//									1.6));
//		} catch(FileNotFoundException e) {
//			System.err.println("Failed to read trajectory");
//		}
		// gamepad.getButtonA().whenPressed(new SetElevatorCommand(20000));
		// 96.5 inches = 8.0417 feet = 31387 ticks = 7.66284 rotations ->
		// 1.0494412 ft/rot
		// System.out.println("Read trajectories");
		// gamepad.getButtonA().whenPressed(new ElevatorCommand());
		// gamepad.getButtonY().whenPressed(new ElevatorCommand(1.0));
		// gamepad.getButtonB().whenPressed(new ClosedLoopCommand(2000));
		// gamepad.getButtonA().whenPressed(new IntakeCommand());
		// gamepad.getButtonB().whenPressed(new EjectCommand());
		// gamepad.getButtonB().whenPressed(new DriveStraight());
		//
		// gamepad.getButtonA().whenPressed(new UpDown());
		// gamepad.getButtonB().whenPressed(new InOut());
		// gamepad.getButtonX().whilePressed(new VariableSpeedCommand(1.0,
		// "Button X"));
		// gamepad.getButtonY().whilePressed(new VariableSpeedCommand(-1.0,
		// "Button Y"));
		
		// gamepad.getButtonX().whenPressed(new IntakeCommand());
		// gamepad.getButtonY().whenPressed(new EjectCommand());
		// System.out.println("Built command");
		// gamepad.getButtonY().whenPressed(new
		// LoadMotionProfile(Robot.gearIntake.getOrientation(), traj, 10, 2, new
		// RunMotionProfile(Robot.gearIntake.getOrientation(), 10)::start));
		// gamepad.getButtonY().whenPressed(new
		// MotionProfileCommand(Robot.gearIntake.getOrientation(), traj,
		// MPSettings.ORIENTATION, 10));
		// gamepad.getButtonY().whenPressed(new PathfinderCommand(traj));
		// gamepad.getButtonY().whenPressed(new IntakeMotionProfileCommand());
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static Trajectory readTrajectory(String filename) throws FileNotFoundException {
		File f = new File(filename);
		if(f.exists() && f.isFile() && filename.endsWith(".csv")) {
			try {
				return Pathfinder.readFromCSV(f);
			} catch(Exception e) {
				System.err.println("Pathfinder failed to read trajectory");
			}
		} else {
			System.err.println("Trajectory does not exist or is not a csv file");
		}
		throw new FileNotFoundException("No valid csv file by that name");
	}
}
