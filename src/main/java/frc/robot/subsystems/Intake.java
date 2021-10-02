package frc.robot.subsystems;

import static frc.robot.RobotMap.Intake.*;
import static frc.robot.Config.Intake.*;

import java.util.function.Consumer;

import frc.robot.Robot;
import frc.robot.commands.intake.IntakeCommand;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Intakes cubes in front of the robot, then ejects them onto the switch or the
 * scale
 */
public class Intake extends SubsystemBase {
	/**
	 * Singleton instance
	 */
	private static Intake instance;
	/**
	 * Control the intake wheels
	 */
	private final TalonSRX leftRoller, rightRoller;
	/**
	 * Expand and contract the intake, as well as raise and lower it
	 */
	private DoubleSolenoid expansion, raise;
	/**
	 * Are all of the parts of this intake functioning?
	 */
	private boolean solenoidStatus;
	
	/**
	 * Initialize the intake subsystem
	 */
	private Intake() {
		// Initialize hardware links
		leftRoller = new TalonSRX(LEFT_ROLLER);
		rightRoller = new TalonSRX(RIGHT_ROLLER);
		try {
			expansion = new DoubleSolenoid(EXPANSION_SOLENOID_A, EXPANSION_SOLENOID_B);
			raise = new DoubleSolenoid(RAISING_SOLENOID_A, RAISING_SOLENOID_B);
			solenoidStatus = true;
		} catch (Exception e) {
			System.err.println("Intake: Could not find intake solenoids");
		}
		
		leftRoller.setInverted(!Robot.IS_COMP);
		rightRoller.setInverted(Robot.IS_COMP);
		
		set((talon) -> {
			talon.setNeutralMode(NEUTRAL_MODE);
			talon.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, TIMEOUT);
			talon.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, TIMEOUT);
			talon.configPeakCurrentDuration(PEAK_CURRENT_DURATION, TIMEOUT);
			talon.enableCurrentLimit(ENABLE_CURRENT_LIMIT);
			talon.setNeutralMode(NEUTRAL_MODE);
		});
	}
	
	/**
	 * Runs a consumer on both motors
	 * 
	 * @param consumer
	 *            the function (lambda?) to run on them
	 */
	public void set(Consumer<TalonSRX> consumer) {
		consumer.accept(leftRoller);
		consumer.accept(rightRoller);
	}
	
	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	
	public void initDefaultCommand() {
		setDefaultCommand(new IntakeCommand());
	}
	
	/**
     * @return the leftRoller
     */
    public TalonSRX getLeftRoller()
    {
        return leftRoller;
    }

    /**
     * @return the rightRoller
     */
    public TalonSRX getRightRoller()
    {
        return rightRoller;
    }

    /**
	 * @return the expanding and contracting solenoid
	 */
	public DoubleSolenoid getExpansion() {
		return expansion;
	}

	/**
	 * @return the raising and lowering solenoid
	 */
	public DoubleSolenoid getRaise() {
		return raise;
	}

	/**
	 * Reference to the singleton instance
	 * 
	 * @return the singleton instance
	 */
	public static Intake getInstance() {
		if(instance == null) {
			instance = new Intake();
		}
		return instance;
	}
	
	public void close() {
		expansion.set(Robot.IS_COMP ? Value.kReverse : Value.kForward);
	}
	
	public boolean isClosed() {
		return expansion.get() == (Robot.IS_COMP ? Value.kReverse : Value.kForward);
	}
	
	public void open() {
		expansion.set(Robot.IS_COMP ? Value.kForward : Value.kReverse);
	}
	
	public boolean isOpen() {
		return expansion.get() == (Robot.IS_COMP ? Value.kForward : Value.kReverse);
	}
	
	public void raise() {
		raise.set(Robot.IS_COMP ? Value.kForward : Value.kReverse);
	}
	
	public boolean isRaised() {
		return raise.get() == (Robot.IS_COMP ? Value.kForward : Value.kReverse);
	}
	
	public void lower() {
		raise.set(Robot.IS_COMP ? Value.kReverse : Value.kForward);
	}
	
	public boolean isLowered() {
		return raise.get() == (Robot.IS_COMP ? Value.kReverse : Value.kForward);
	}
}
