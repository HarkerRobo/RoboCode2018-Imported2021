/*----------------------------------------------------------------------------*/
/*
 * Copyright (c) 2017-2018 FIRST. All Rights Reserved. Open Source Software -
 * may be modified and shared by FRC teams. The code must be accompanied by the
 * FIRST BSD license file in the root directory of the project.
 */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/**
 * The Config keeps track of the initial states of any hardware on the robot
 */
public class Config {
	/**
	 * Timeout (ms) for operations that set motor controller states
	 */
	public static int TIMEOUT = 10;
	
	public static class Drivetrain {
		/**
		 * Timeout (ms) for operations that set motor controller states
		 */
		public static int TIMEOUT = 10;
		/**
		 * Default motor state (brake or coast)
		 */
		public static final NeutralMode NEUTRAL_MODE = NeutralMode.Brake;
		/**
		 * Maximum standard current per motor (A)
		 */
		public static final int CONTINUOUS_CURRENT_LIMIT = 30;
		/**
		 * A motor may deviate to currents up to peak current limit (A), for a
		 * short peak current duration of time (ms)
		 */
		public static final int PEAK_CURRENT_LIMIT = 35, PEAK_CURRENT_DURATION = 100;
		/**
		 * Controls whether current limiting is active
		 */
		public static final boolean ENABLE_CURRENT_LIMIT = false;
		/**
		 * Controls encoder mode (absolute or relative)
		 */
		public static final FeedbackDevice ENCODER_MODE = FeedbackDevice.CTRE_MagEncoder_Absolute;
		/**
		 * Voltage compensation saturation
		 */
		public static double VOLTAGE_SATURATION = 10;
		/**
		 * Use voltage compensation
		 */
		public static boolean ENABLE_VOLTAGE_COMPENSATION = true;
		/**
		 * Seconds to ramp from neutral to full
		 */
		public static double RAMP_SPEED = 0.3;
		
		public static class Carpet {
			
			public static final double kF_LEFT = 0.7 * 1023.0 / 3640.0, kF_RIGHT = 0.7 * 1023.0 / 3880.0;
			
			// Autonomous public static final double kP = 0.8, kI = 0.0023, kD = 0.64;
			public static final double kP = 0.3, kI = 0.0010, kD = 0.34;
			
		}
	}
	
	public static class Elevator {
		/**
		 * Timeout (ms) for operations that set motor controller states
		 */
		public static int TIMEOUT = 10;
		/**
		 * Default motor state (brake or coast)
		 */
		public static final NeutralMode NEUTRAL_MODE = NeutralMode.Brake;
		/**
		 * Maximum standard current per motor (A)
		 */
		public static final int CONTINUOUS_CURRENT_LIMIT = 20, BAD_ENCODER_CONTINUOUS_CURRENT_LIMIT = 10;
		/**
		 * A motor may deviate to currents up to peak current limit (A), for a
		 * short peak current duration of time (ms)
		 */
		public static final int PEAK_CURRENT_LIMIT = 25, PEAK_CURRENT_DURATION = 50, BAD_ENCODER_PEAK_CURRENT_LIMIT = 14;
		/**
		 * Controls whether current limiting is active
		 */
		public static final boolean ENABLE_CURRENT_LIMIT = true;
		/**
		 * Normal state of the elevator limit switches
		 */
		public static final LimitSwitchNormal FORWARD_NORMAL = LimitSwitchNormal.NormallyOpen,
				REVERSE_NORMAL = LimitSwitchNormal.NormallyOpen;
		/**
		 * Controls encoder mode (absolute or relative)
		 */
		public static final FeedbackDevice ENCODER_MODE = FeedbackDevice.CTRE_MagEncoder_Absolute;
		/**
		 * Voltage compensation saturation
		 */
		public static double VOLTAGE_SATURATION = 10;
		/**
		 * Use voltage compensation
		 */
		public static boolean ENABLE_VOLTAGE_COMPENSATION = true;
		/**
		 * Seconds to ramp from neutral to full
		 */
		public static double RAMP_SPEED = 0.4;
		/**
		 * Ratio of Encoder counter to feet, need to test
		 */
		public static final int ENCODERTOFEET = 1;
		/**
		 * Motion magic constants
		 */
		public static final int CRUISE_SPEED = 3000/*4 * (int) frc.robot.subsystems.Elevator.FEET_TO_ENCODER / 10*/, MAX_ACCELERATION = 120000/*2 * (int) frc.robot.subsystems.Elevator.FEET_TO_ENCODER / 10*/;
	}
	
	public static class Intake {
		/**
		 * Timeout (ms) for operations that set motor controller states
		 */
		public static int TIMEOUT = 10;
		/**
		 * Default motor state (brake or coast)
		 */
		public static final NeutralMode NEUTRAL_MODE = NeutralMode.Coast;
		/**
		 * Maximum standard current per motor (A)
		 */
		public static final int CONTINUOUS_CURRENT_LIMIT = 10;
		/**
		 * A motor may deviate to currents up to peak current limit (A), for a
		 * short peak current duration of time (ms)
		 */
		public static final int PEAK_CURRENT_LIMIT = 15, PEAK_CURRENT_DURATION = 100;
		/**
		 * Controls whether current limiting is active
		 */
		public static final boolean ENABLE_CURRENT_LIMIT = false;
	}
}
