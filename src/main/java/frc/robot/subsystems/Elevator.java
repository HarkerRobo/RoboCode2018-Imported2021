package frc.robot.subsystems;

import static frc.robot.RobotMap.Elevator.*;

import frc.robot.Robot;
import frc.robot.Slot;
import frc.robot.commands.elevator.ElevatorDriveCommand;

import static frc.robot.Config.Elevator.*;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Two stage elevator to lift power cubes from the floor to the scale
 */
public class Elevator extends Subsystem {
	
	/**
	 * Min and max height in feet, by middle of metal bar
	 */
	public static final double MIN = 8.0 / 12.0, MAX = 7.0 + 1.5 / 12.0;
	/**
	 * Distance in encoder units from the top to the bottom of the elevator
	 */
	public static final int LENGTH = Robot.IS_COMP ? 35860 : 35300;
	/**
	 * feet to encoder values
	 */
	public static final double FEET_TO_ENCODER = LENGTH / (MAX - MIN);
	/**
	 * Space between soft and hard limits
	 */
	public static final int BUFFER = 200;
	
	/**
	 * Singleton instance
	 */
	private static Elevator instance;
	/**
	 * Control only this motor, the others will copy its movement
	 */
	private TalonSRX master;
	/**
	 * Ignore these motors, they will simply follow the master
	 */
	private VictorSPX follower1, follower2, follower3;
	/**
	 * Are all of the parts of this elevator functioning?
	 */
	private boolean motorStatus, encoderStatus, forwardLimitStatus, reverseLimitStatus, forwardSoftLimitStatus,
			reverseSoftLimitStatus, currentLimitStatus, voltageCompensationStatus, openRampStatus, closedRampStatus,
			velocityClosedStatus, positionClosedStatus, motionMagicStatus;
	/**
	 * minimum encoder value
	 */
	private double min;
	
	private ControlMode mode = ControlMode.Disabled;
	
	private double demand = 0;
	
	private StickyFaults sticky = new StickyFaults();
	
	private boolean sensorPhase = Robot.IS_COMP;
	
	/**
	 * Initialize the elevator subsystem
	 */
	private Elevator() {
		try {
			// Initialize hardware links
			master = new TalonSRX(TALON);
			follower1 = new VictorSPX(VICTOR_1);
			follower2 = new VictorSPX(VICTOR_2);
			follower3 = new VictorSPX(VICTOR_3);
			motorStatus = true;
			// Set following
			follower1.follow(master);
			follower2.follow(master);
			follower3.follow(master);
			// Invert
			if(!Robot.IS_COMP)
				follower2.setInverted(true);
			// Configure settings
			master.setNeutralMode(NEUTRAL_MODE);
			follower1.setNeutralMode(NEUTRAL_MODE);
			follower2.setNeutralMode(NEUTRAL_MODE);
			follower3.setNeutralMode(NEUTRAL_MODE);
			// Configure output ranges
			master.configNominalOutputForward(0, 0);
			master.configNominalOutputReverse(0, 0);
			master.configPeakOutputForward(1, 0);
			master.configPeakOutputReverse(-1, 0);
			// Configure ramping
			openRampStatus = log(master.configOpenloopRamp(RAMP_SPEED, TIMEOUT),
					"Failed to configure open loop ramping");
			closedRampStatus = log(master.configClosedloopRamp(RAMP_SPEED, TIMEOUT),
					"Failed to configure closed loop ramping");
			// Configure encoders
			encoderStatus = log(master.configSelectedFeedbackSensor(ENCODER_MODE, ENCODER, TIMEOUT),
					"Encoder not found")
					&& log(master.getSensorCollection().getPulseWidthRiseToRiseUs() != 0, "No encoder readings");
			master.setSensorPhase(Robot.IS_COMP);
			// Configure current limiting
			if(encoderStatus) {
				if(currentLimitStatus = log(master.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, TIMEOUT),
						"Failed to configure continuous current limit")
						&& log(master.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, TIMEOUT),
								"Failed to configure peak current limit")
						&& log(master.configPeakCurrentDuration(PEAK_CURRENT_DURATION, TIMEOUT),
								"Failed to configure peak current duration")) {
					master.enableCurrentLimit(ENABLE_CURRENT_LIMIT);
				}
			} else {
				if(currentLimitStatus = log(
						master.configContinuousCurrentLimit(BAD_ENCODER_CONTINUOUS_CURRENT_LIMIT, TIMEOUT),
						"Failed to configure continuous current limit")
						&& log(master.configPeakCurrentLimit(BAD_ENCODER_PEAK_CURRENT_LIMIT, TIMEOUT),
								"Failed to configure peak current limit")
						&& log(master.configPeakCurrentDuration(PEAK_CURRENT_DURATION, TIMEOUT),
								"Failed to configure peak current duration")) {
					master.enableCurrentLimit(ENABLE_CURRENT_LIMIT);
				}
			}
			if(voltageCompensationStatus = log(master.configVoltageCompSaturation(10, TIMEOUT),
					"Failed to configure voltage saturation")) {
				master.enableVoltageCompensation(ENABLE_VOLTAGE_COMPENSATION);
			}
			// Configure limit switches
			forwardLimitStatus = log(master.configForwardLimitSwitchSource(FORWARD_SWITCH, FORWARD_NORMAL, TIMEOUT),
					"Forward limit switch not found");
			reverseLimitStatus = log(master.configReverseLimitSwitchSource(REVERSE_SWITCH, REVERSE_NORMAL, TIMEOUT),
					"Reverse limit switch not found");
			forwardSoftLimitStatus = log(master.configForwardSoftLimitEnable(false, TIMEOUT),
					"Failed to disable forward soft limits") && encoderStatus
					&& log(master.configForwardSoftLimitThreshold(LENGTH - BUFFER, TIMEOUT),
							"Failed to configure forward soft limits");
			reverseSoftLimitStatus = log(master.configReverseSoftLimitEnable(false, TIMEOUT),
					"Failed to disable reverse soft limits") && encoderStatus
					&& log(master.configReverseSoftLimitThreshold(BUFFER, TIMEOUT),
							"Failed to configure reverse soft limits");
			log(master.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, TIMEOUT),
					"Failed to enable clearing encoder on limit");
			// Load constants
			if(!(velocityClosedStatus = Slot.ELEVATOR_VELOCITY.configure(master, TIMEOUT))) {
				System.err.println("Elevator: Failed to configure velocity closed loop");
			}
			if(!(positionClosedStatus = Slot.ELEVATOR_POSITION.configure(master, TIMEOUT))) {
				System.err.println("Elevator: Failed to configure position closed loop");
			}
			if(!(Slot.ELEVATOR_SMALL_POSITION.configure(master, TIMEOUT))) {
				System.err.println("Elevator: Failed to configure position closed loop");
			}
			if(!(motionMagicStatus = Slot.ELEVATOR_MOTION_MAGIC.configure(master, TIMEOUT))
					&& log(master.configMotionCruiseVelocity(CRUISE_SPEED, TIMEOUT),
							"Failed to configure cruise velocity")
					&& log(master.configMotionAcceleration(MAX_ACCELERATION, TIMEOUT),
							"Failed to configure motion magic acceleration")) {
				System.err.println("Elevator: Failed to configure motion magic");
			}
			master.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, TIMEOUT);
			master.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, TIMEOUT);
			/* set the peak and nominal outputs */
			master.configNominalOutputForward(0, TIMEOUT);
			master.configNominalOutputReverse(0, TIMEOUT);
			master.configPeakOutputForward(1.0, TIMEOUT);
			master.configPeakOutputReverse(-1.0, TIMEOUT);
		} catch(Exception e) {
			System.err.println("Elevator: Failed to initialize motors");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wpi.first.wpilibj.command.Subsystem#periodic()
	 */
	@Override
	public void periodic() {
		boolean encoderReadings = master.getSensorCollection().getPulseWidthRiseToRiseUs() != 0;
		if(encoderReadings && !encoderStatus) {
			log("Rediscovered encoder");
			master.configPeakOutputForward(1.0, 0);
			master.configPeakOutputReverse(-1.0, 0);
			master.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, 0);
			master.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, 0);
			encoderStatus = true;
		} else if(!encoderReadings && encoderStatus) {
			log("Lost encoder");
			master.configPeakOutputForward(0.6, 0);
			master.configPeakOutputReverse(-0.6, 0);
			master.configContinuousCurrentLimit(BAD_ENCODER_CONTINUOUS_CURRENT_LIMIT, 0);
			master.configPeakCurrentLimit(BAD_ENCODER_PEAK_CURRENT_LIMIT, 0);
			encoderStatus = false;
		}
		master.getStickyFaults(sticky);
		if(sticky.ForwardLimitSwitch || sticky.ForwardSoftLimit) {
			if((mode == ControlMode.Position || mode == ControlMode.MotionMagic)
					&& demand > master.getSelectedSensorPosition(0)) {
				set(mode, master.getSelectedSensorPosition(0));
			} else if((mode == ControlMode.PercentOutput || mode == ControlMode.Velocity) && demand > 0) {
				set(mode, 0);
			}
		}
		if(sticky.ReverseLimitSwitch || sticky.ReverseSoftLimit) {
			if((mode == ControlMode.Position || mode == ControlMode.MotionMagic)
					&& demand < master.getSelectedSensorPosition(0)) {
				set(mode, master.getSelectedSensorPosition(0));
			} else if((mode == ControlMode.PercentOutput || mode == ControlMode.Velocity) && demand < 0) {
				set(mode, 0);
			}
		}
		if(sticky.SensorOutOfPhase) {
			sensorPhase = !sensorPhase;
			master.setSensorPhase(sensorPhase);
			log("Flipped sensor phase");
		}
		if(sticky.HardwareESDReset) {
			log("Hardware ESD reset");
		}
		if(sticky.ResetDuringEn) {
			log("Reset while enabled");
		}
		if(sticky.SensorOverflow) {
			log("Sensor overflow");
		}
		if(sticky.UnderVoltage) {
			log("Under voltage");
		}
		if(sticky.hasAnyFault()) {
			master.clearStickyFaults(0);
		}
	}
	
	public void set(ControlMode mode, double demand) {
		if(this.mode != mode) {
			switch(mode) {
				case MotionMagic: master.selectProfileSlot(Slot.ELEVATOR_MOTION_MAGIC.getSlot(), 0); break;
				case Position: master.selectProfileSlot(Slot.ELEVATOR_POSITION.getSlot(), 0); break;
				case Velocity: master.selectProfileSlot(Slot.ELEVATOR_VELOCITY.getSlot(), 0); break;
			}
		}
		master.set(this.mode = mode, this.demand = demand, DemandType.ArbitraryFeedForward,
				Robot.IS_COMP ? 0.09 : 0.08);
	}
	
	public static boolean log(ErrorCode err, String value) {
		return log(err == ErrorCode.OK, value);
	}
	
	public static boolean log(boolean good, String value) {
		if(!good)
			log(value);
		return good;
	}
	
	public static void log(String value) {
		Robot.log("Elevator: " + value);
	}
	
	/**
	 * @return the master talon in the gearbox
	 */
	public TalonSRX getMaster() {
		return master;
	}
	
	public void initDefaultCommand() {
		setDefaultCommand(new ElevatorDriveCommand());
	}
	
	/**
	 * Reference to the singleton instance
	 * 
	 * @return the singleton instance
	 */
	public static Elevator getInstance() {
		if(instance == null) {
			instance = new Elevator();
		}
		return instance;
	}
	
	/**
	 * @return the encoderStatus
	 */
	public boolean isEncoderStatus() {
		return encoderStatus;
	}
	
	/**
	 * @return the forwardLimitStatus
	 */
	public boolean isForwardLimitStatus() {
		return forwardLimitStatus;
	}
	
	/**
	 * @return the reverseLimitStatus
	 */
	public boolean isReverseLimitStatus() {
		return reverseLimitStatus;
	}
	
	/**
	 * @return the forwardSoftLimitStatus
	 */
	public boolean isForwardSoftLimitStatus() {
		return forwardSoftLimitStatus;
	}
	
	/**
	 * @return the reverseSoftLimitStatus
	 */
	public boolean isReverseSoftLimitStatus() {
		return reverseSoftLimitStatus;
	}
	
	/**
	 * @param encoderStatus
	 *            the encoderStatus to set
	 */
	public void setEncoderStatus(boolean encoderStatus) {
		this.encoderStatus = encoderStatus;
	}
	
	/**
	 * @param forwardLimitStatus
	 *            the forwardLimitStatus to set
	 */
	public void setForwardLimitStatus(boolean forwardLimitStatus) {
		this.forwardLimitStatus = forwardLimitStatus;
	}
	
	/**
	 * @param reverseLimitStatus
	 *            the reverseLimitStatus to set
	 */
	public void setReverseLimitStatus(boolean reverseLimitStatus) {
		this.reverseLimitStatus = reverseLimitStatus;
	}
	
	/**
	 * @param forwardSoftLimitStatus
	 *            the forwardSoftLimitStatus to set
	 */
	public void setForwardSoftLimitStatus(boolean forwardSoftLimitStatus) {
		this.forwardSoftLimitStatus = forwardSoftLimitStatus;
	}
	
	/**
	 * @param reverseSoftLimitStatus
	 *            the reverseSoftLimitStatus to set
	 */
	public void setReverseSoftLimitStatus(boolean reverseSoftLimitStatus) {
		this.reverseSoftLimitStatus = reverseSoftLimitStatus;
	}
	
	/**
	 * @return the min
	 */
	public double getMin() {
		return min;
	}
	
	/**
	 * @param min
	 *            the min to set
	 */
	public void setMin(double min) {
		this.min = min;
	}
	
	/**
	 * @return the currentLimitStatus
	 */
	public boolean isCurrentLimitStatus() {
		return currentLimitStatus;
	}
	
	/**
	 * @param currentLimitStatus
	 *            the currentLimitStatus to set
	 */
	public void setCurrentLimitStatus(boolean currentLimitStatus) {
		this.currentLimitStatus = currentLimitStatus;
	}
	
	/**
	 * @return the voltageCompensationStatus
	 */
	public boolean isVoltageCompensationStatus() {
		return voltageCompensationStatus;
	}
	
	/**
	 * @param voltageCompensationStatus
	 *            the voltageCompensationStatus to set
	 */
	public void setVoltageCompensationStatus(boolean voltageCompensationStatus) {
		this.voltageCompensationStatus = voltageCompensationStatus;
	}
	
	/**
	 * @return the openRampStatus
	 */
	public boolean isOpenRampStatus() {
		return openRampStatus;
	}
	
	/**
	 * @param openRampStatus
	 *            the openRampStatus to set
	 */
	public void setOpenRampStatus(boolean openRampStatus) {
		this.openRampStatus = openRampStatus;
	}
	
	/**
	 * @return the closedRampStatus
	 */
	public boolean isClosedRampStatus() {
		return closedRampStatus;
	}
	
	/**
	 * @param closedRampStatus
	 *            the closedRampStatus to set
	 */
	public void setClosedRampStatus(boolean closedRampStatus) {
		this.closedRampStatus = closedRampStatus;
	}
	
	/**
	 * @return the motorStatus
	 */
	public boolean isMotorStatus() {
		return motorStatus;
	}
	
	/**
	 * @param motorStatus
	 *            the motorStatus to set
	 */
	public void setMotorStatus(boolean motorStatus) {
		this.motorStatus = motorStatus;
	}
	
	/**
	 * @return the velocityClosedStatus
	 */
	public boolean isVelocityClosedStatus() {
		return velocityClosedStatus;
	}
	
	/**
	 * @param velocityClosedStatus
	 *            the velocityClosedStatus to set
	 */
	public void setVelocityClosedStatus(boolean velocityClosedStatus) {
		this.velocityClosedStatus = velocityClosedStatus;
	}
	
	/**
	 * @return the positionClosedStatus
	 */
	public boolean isPositionClosedStatus() {
		return positionClosedStatus;
	}
	
	/**
	 * @param positionClosedStatus
	 *            the positionClosedStatus to set
	 */
	public void setPositionClosedStatus(boolean positionClosedStatus) {
		this.positionClosedStatus = positionClosedStatus;
	}
	
	/**
	 * @return the motionMagicStatus
	 */
	public boolean isMotionMagicStatus() {
		return motionMagicStatus;
	}
	
	/**
	 * @param motionMagicStatus
	 *            the motionMagicStatus to set
	 */
	public void setMotionMagicStatus(boolean motionMagicStatus) {
		this.motionMagicStatus = motionMagicStatus;
	}
}
