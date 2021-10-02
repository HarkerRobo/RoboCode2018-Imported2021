package frc.robot.commands.elevator;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * @author joel
 *
 */
public class LowLevelCommand extends SetElevatorCommand {

	/**
	 * @param height
	 */
	public LowLevelCommand(double height) {
		super(height);
	}

	/* (non-Javadoc)
	 * @see frc.robot.commands.v2.SetElevatorCommand#initialize()
	 */
	@Override
	protected void initialize() {
		Robot.intake.getRaise().set(Robot.IS_COMP ? Value.kReverse : Value.kForward);
		super.initialize();
	}
	
}
