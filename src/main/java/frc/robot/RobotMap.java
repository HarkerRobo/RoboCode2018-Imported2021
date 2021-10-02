/*----------------------------------------------------------------------------*/
/*
 * Copyright (c) 2017-2018 FIRST. All Rights Reserved. Open Source Software -
 * may be modified and shared by FRC teams. The code must be accompanied by the
 * FIRST BSD license file in the root directory of the project.
 */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	public static class Drivetrain {
		/**
		 * 4 Cim motors, 2 per gearbox run the drivetrain
		 */
		public static final int LEFT_TALON = 5, RIGHT_TALON = 2, LEFT_VICTOR = 4, RIGHT_VICTOR = 3;
		/**
		 * 1 VersaPlanetary encoder is in each gearbox, is it connected through
		 * #0 or #1
		 */
		public static final int ENCODER = 0;
	}
	
	public static class Elevator {
		/**
		 * 4 775 Pros lift raise and lower the elevator
		 */
		public static final int TALON = 9, VICTOR_1 = 7, VICTOR_2 = 8, VICTOR_3 = 10;
		/**
		 * Two limit switches prevent the elevator from going too far
		 */
		public static final LimitSwitchSource FORWARD_SWITCH = LimitSwitchSource.FeedbackConnector,
				REVERSE_SWITCH = LimitSwitchSource.FeedbackConnector;
		/**
		 * 1 VersaPlanetary encoder measures elevator position
		 */
		public static final int ENCODER = 0;
	}
	
	public static class Intake {
		/**
		 * On each side of the intake, one motor runs two compliant wheels
		 */
		public static final int LEFT_ROLLER = 6, RIGHT_ROLLER = 1;
		/**
		 * On each side of the intake, a solenoid can extend to shrink the
		 * intake area
		 */
		public static final int EXPANSION_SOLENOID_A = 0, EXPANSION_SOLENOID_B = 2, RAISING_SOLENOID_A = 1,
				RAISING_SOLENOID_B = 3;
	}
	
	public static final int PIGEON_IMU = 11;
	
	// public static class GearIntake {
	// /**
	// * Compliant wheels bring gears into the intake
	// */
	// public static final int ROLLERS = 7;
	// /**
	// * one talon controls whether the gear is being held upright or intaking
	// * from the floor
	// */
	// public static final int ORIENTATION = 1;
	// }
}
