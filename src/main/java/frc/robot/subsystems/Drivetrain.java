package frc.robot.subsystems;

import static frc.robot.RobotMap.Drivetrain.*;
import static frc.robot.Config.Drivetrain.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.Slot;
import frc.robot.commands.drivetrain.ArcadeDriveCommand;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * 4-Cim west coast drivetrain to move the robot
 */
public class Drivetrain extends SubsystemBase {
	
	public static final double MAX_SPEED = 0;
	
	public static final int MAIN_PID = 0, AUX_PID = 1;
	
	public static final int GYRO_TALON = 0;
	
	private StickyFaults sticky = new StickyFaults();
	
	/**
	 * Singleton instance
	 */
	private static Drivetrain instance;
	/**
	 * Control only these motors, the others will copy its movement
	 */
	private TalonSRX leftMaster, rightMaster;
	/**
	 * Ignore these motors, they will simply follow the master
	 */
	private VictorSPX leftFollower, rightFollower;
	/**
	 * Are all of the parts of this elevator functioning?
	 */
	private boolean motorStatus, encoderStatus, currentLimitStatus, voltageCompensationStatus, openRampStatus,
			closedRampStatus, velocityClosedStatus, motionProfileStatus, pitchClosedStatus, yawClosedStatus, gyroStatus;
	
	/**
	 * Initialize the drivetrain subsystem
	 */
	private Drivetrain() {
		// Initialize hardware links
		try {
			leftMaster = new TalonSRX(LEFT_TALON);
			rightMaster = new TalonSRX(RIGHT_TALON);
			leftFollower = new VictorSPX(LEFT_VICTOR);
			rightFollower = new VictorSPX(RIGHT_VICTOR);
			motorStatus = true;
			// invert
			leftMaster.setInverted(true);
			leftFollower.setInverted(true);

			rightFollower.setInverted(false);
			rightMaster.setInverted(false);
			// Set following
			leftFollower.follow(leftMaster);
			rightFollower.follow(rightMaster);
			// Configure settings (on both masters)
			leftMaster.setNeutralMode(NEUTRAL_MODE);
			rightMaster.setNeutralMode(NEUTRAL_MODE);
			leftFollower.setNeutralMode(NEUTRAL_MODE);
			rightFollower.setNeutralMode(NEUTRAL_MODE);
			// Configure current limiting
			if(currentLimitStatus = ENABLE_CURRENT_LIMIT
					&& log(leftMaster.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, TIMEOUT),
							"Failed to configure left continuous current limit")
					&& log(leftMaster.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, TIMEOUT),
							"Failed to configure left peak current limit")
					&& log(leftMaster.configPeakCurrentDuration(PEAK_CURRENT_DURATION, TIMEOUT),
							"Failed to configure left peak current duration")
					&& log(rightMaster.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, TIMEOUT),
							"Failed to configure right continuous current limit")
					&& log(rightMaster.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, TIMEOUT),
							"Failed to configure right peak current limit")
					&& log(rightMaster.configPeakCurrentDuration(PEAK_CURRENT_DURATION, TIMEOUT),
							"Failed to configure right peak current duration")) {
				set((talon) -> talon.enableCurrentLimit(ENABLE_CURRENT_LIMIT));
			}
			
			if(voltageCompensationStatus = ENABLE_VOLTAGE_COMPENSATION
					&& log(leftMaster.configVoltageCompSaturation(VOLTAGE_SATURATION, TIMEOUT),
							"Failed to configure left voltage saturation")
					&& log(rightMaster.configVoltageCompSaturation(VOLTAGE_SATURATION, TIMEOUT),
							"Failed to configure right voltage saturation")) {
				set((talon) -> talon.enableVoltageCompensation(ENABLE_VOLTAGE_COMPENSATION));
			}
			// Configure output ranges
			set((talon) -> {
				talon.configNominalOutputForward(0, 0);
				talon.configNominalOutputReverse(0, 0);
				talon.configPeakOutputForward(1, 0);
				talon.configPeakOutputReverse(-1, 0);
			});
			// Configure ramping
			openRampStatus = log(leftMaster.configOpenloopRamp(RAMP_SPEED, TIMEOUT),
					"Failed to configure left open loop ramping")
					&& log(rightMaster.configOpenloopRamp(RAMP_SPEED, TIMEOUT),
							"Failed to configure right open loop ramping");
			closedRampStatus = log(leftMaster.configClosedloopRamp(RAMP_SPEED, TIMEOUT),
					"Failed to configure left closed loop ramping")
					&& log(rightMaster.configClosedloopRamp(RAMP_SPEED, TIMEOUT),
							"Failed to configure right closed loop ramping");
			// Configure encoders
			encoderStatus = log(leftMaster.configSelectedFeedbackSensor(ENCODER_MODE, MAIN_PID, TIMEOUT),
					"Left encoder not found")
					&& log(rightMaster.configSelectedFeedbackSensor(ENCODER_MODE, MAIN_PID, TIMEOUT),
							"Right encoder not found")
					&& log(leftMaster.getSensorCollection().getPulseWidthRiseToRiseUs() != 0,
							"No left encoder readings")
					&& log(rightMaster.getSensorCollection().getPulseWidthRiseToRiseUs() != 0,
							"No right encoder readings");
			rightMaster.configSelectedFeedbackCoefficient(0.94, MAIN_PID, TIMEOUT);
			leftMaster.configSelectedFeedbackCoefficient(1.0, MAIN_PID, TIMEOUT);
			rightMaster.configSelectedFeedbackCoefficient(0.25, AUX_PID, TIMEOUT);
			leftMaster.configSelectedFeedbackCoefficient(0.25, AUX_PID, TIMEOUT);
			rightMaster.configClosedLoopPeakOutput(MAIN_PID, 1.0, TIMEOUT);
			leftMaster.configClosedLoopPeakOutput(MAIN_PID, 1.0, TIMEOUT);
			rightFollower.configClosedLoopPeakOutput(AUX_PID, 0.1, TIMEOUT);
			leftFollower.configClosedLoopPeakOutput(AUX_PID, 0.1, TIMEOUT);
			// Configure pigeon
			gyroStatus = log(leftMaster.configRemoteFeedbackFilter(RobotMap.Intake.RIGHT_ROLLER, RemoteSensorSource.Off,
					0, TIMEOUT), "Pigeon IMU not found")
					&& log(rightMaster.configRemoteFeedbackFilter(RobotMap.Intake.RIGHT_ROLLER, RemoteSensorSource.Off,
							0, TIMEOUT), "Pigeon IMU not found")
					&& log(leftMaster.configSelectedFeedbackSensor(RemoteFeedbackDevice.None, AUX_PID,
							TIMEOUT), "Could not configure Pigeon IMU")
					&& log(rightMaster.configSelectedFeedbackSensor(RemoteFeedbackDevice.None, AUX_PID,
							TIMEOUT), "Could not configure Pigeon IMU");
//			log(rightMaster.configAuxPIDPolarity(false, TIMEOUT), "Could not configure auxiliary PID polarity");
//			log(leftMaster.configAuxPIDPolarity(true, TIMEOUT), "Could not configure auxiliary PID polarity");
			// Load constants
			if(!(velocityClosedStatus = false && Slot.LEFT_VELOCITY.configure(leftMaster, TIMEOUT)
					&& Slot.RIGHT_VELOCITY.configure(rightMaster, TIMEOUT))) {
				log("Drivetrain: Failed to configure velocity closed loop");
			}
			if(!(yawClosedStatus = Slot.LEFT_YAW.configure(leftMaster, TIMEOUT)
					&& Slot.RIGHT_YAW.configure(rightMaster, TIMEOUT))) {
				log("Drivetrain: Failed to configure position closed loop");
			}
			if(!(motionProfileStatus = Slot.LEFT_MOTION_PROFILE.configure(leftMaster, TIMEOUT)
					&& Slot.RIGHT_MOTION_PROFILE.configure(rightMaster, TIMEOUT))) {
				log("Drivetrain: Failed to configure motion profiling");
			}
			// Select profile zero
			set((talon) -> talon.selectProfileSlot(0, 0));
			set((talon) -> talon.selectProfileSlot(1, 1));
		} catch(Exception e) {
			log("Drivetrain: Failed to initialize motors");
		}
	}
	
	public void set(double left, double right) {
		if(encoderStatus && velocityClosedStatus) {
			if(gyroStatus && pitchClosedStatus) {
				leftMaster.set(ControlMode.Velocity, left * MAX_SPEED, DemandType.AuxPID, 0);
				rightMaster.set(ControlMode.Velocity, right * MAX_SPEED, DemandType.AuxPID, 0);
			} else {
				leftMaster.set(ControlMode.Velocity, left * MAX_SPEED);
				rightMaster.set(ControlMode.Velocity, right * MAX_SPEED);
			}
		} else {
			if(gyroStatus && pitchClosedStatus) {
				leftMaster.set(ControlMode.PercentOutput, left, DemandType.AuxPID, 0);
				rightMaster.set(ControlMode.PercentOutput, right, DemandType.AuxPID, 0);
			} else {
				leftMaster.set(ControlMode.PercentOutput, left);
				rightMaster.set(ControlMode.PercentOutput, right);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wpi.first.wpilibj.command.SubsystemBase#periodic()
	 */
	@Override
	public void periodic() {
		if(encoderStatus
				&& log(leftMaster.getSensorCollection().getPulseWidthRiseToRiseUs() != 0, "No left encoder readings")
				&& log(rightMaster.getSensorCollection().getPulseWidthRiseToRiseUs() != 0, "No right encoder readings"))
			encoderStatus = false;
		leftMaster.getStickyFaults(sticky);
		if(sticky.HardwareESDReset) {
			log("right esd reset");
		}
		if(sticky.ResetDuringEn) {
			log("right reset during enabled");
		}
		if(sticky.SensorOutOfPhase) {
			log("sensor out of phase");
		}
		if(sticky.UnderVoltage) {
			log("under voltage");
		}
		if(sticky.RemoteLossOfSignal) {
//			log("remote loss of signal");
		}
		if(sticky.hasAnyFault()) {
			leftMaster.clearStickyFaults(0);
		}
		rightMaster.getStickyFaults(sticky);
		if(sticky.HardwareESDReset) {
			log("right esd reset");
		}
		if(sticky.ResetDuringEn) {
			log("right reset during enabled");
		}
		if(sticky.SensorOutOfPhase) {
			log("sensor out of phase");
		}
		if(sticky.UnderVoltage) {
			log("under voltage");
		}
		if(sticky.RemoteLossOfSignal) {
//			log("remote loss of signal");
		}
		if(sticky.hasAnyFault()) {
			rightMaster.clearStickyFaults(0);
		}
//		leftMaster.getStickyFaults(sticky);
//		log(!sticky.hasAnyFault(), "left master sticky");
//		rightMaster.getStickyFaults(sticky);
//		log(!sticky.hasAnyFault(), "right master sticky");
//		leftFollower.getStickyFaults(sticky);
//		log(!sticky.hasAnyFault(), "left follower sticky");
//		rightFollower.getStickyFaults(sticky);
//		log(!sticky.hasAnyFault(), "right follower sticky");
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
	 * @return the encoderStatus
	 */
	public boolean isEncoderStatus() {
		return encoderStatus;
	}
	
	/**
	 * @param encoderStatus
	 *            the encoderStatus to set
	 */
	public void setEncoderStatus(boolean encoderStatus) {
		this.encoderStatus = encoderStatus;
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
	 * @return the motionProfileStatus
	 */
	public boolean isMotionProfileStatus() {
		return motionProfileStatus;
	}
	
	/**
	 * @param motionProfileStatus
	 *            the motionProfileStatus to set
	 */
	public void setMotionProfileStatus(boolean motionProfileStatus) {
		this.motionProfileStatus = motionProfileStatus;
	}
	
	public boolean log(ErrorCode err, String value) {
		return log(err == ErrorCode.OK, value);
	}
	
	public boolean log(boolean good, String value) {
		if(!good)
			log(value);
		return good;
	}
	
	public void log(String value) {
		Robot.log("Drivetrain: " + value);
	}
	
	/**
	 * Runs a consumer on both masters
	 * 
	 * @param consumer
	 *            the function (lambda?) to run on them
	 */
	public void set(Consumer<TalonSRX> consumer) {
		consumer.accept(leftMaster);
		consumer.accept(rightMaster);
	}
	
	/**
	 * Runs a consumer on both masters
	 * 
	 * @param consumer
	 *            the function (lambda?) to run on them
	 */
	public void set(BiConsumer<TalonSRX, String> consumer) {
		consumer.accept(leftMaster, "left");
		consumer.accept(rightMaster, "right");
	}
	
	/**
	 * @return the left master talon
	 */
	public TalonSRX getLeft() {
		return leftMaster;
	}
	
	/**
	 * @return the right master talon
	 */
	public TalonSRX getRight() {
		return rightMaster;
	}
	
	public VictorSPX getRightFollower() {
		return rightFollower;
	}
	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	
	public void initDefaultCommand() {
		setDefaultCommand(new ArcadeDriveCommand());
	}
	
	/**
	 * @return the pitchClosedStatus
	 */
	public boolean isPitchClosedStatus() {
		return pitchClosedStatus;
	}

	/**
	 * @param pitchClosedStatus the pitchClosedStatus to set
	 */
	public void setPitchClosedStatus(boolean pitchClosedStatus) {
		this.pitchClosedStatus = pitchClosedStatus;
	}

	/**
	 * @return the yawClosedStatus
	 */
	public boolean isYawClosedStatus() {
		return yawClosedStatus;
	}

	/**
	 * @param yawClosedStatus the yawClosedStatus to set
	 */
	public void setYawClosedStatus(boolean yawClosedStatus) {
		this.yawClosedStatus = yawClosedStatus;
	}

	/**
	 * Reference to the singleton instance
	 * 
	 * @return the singleton instance
	 */
	public static Drivetrain getInstance() {
		if(instance == null) {
			instance = new Drivetrain();
		}
		return instance;
	}
}
