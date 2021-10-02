package frc.robot.commands.intake;

import frc.robot.Robot;
import frc.robot.commands.elevator.SmallRaiseCommand;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class LiftIntakeCommand extends InstantCommand {
	
	public static final DoubleSolenoid.Value defaultLift = Robot.IS_COMP ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse;
	
	public LiftIntakeCommand() {
		requires(Robot.intake);
	}
	
	// Called just before this Command runs the first time
	protected void initialize() {
		if(Robot.intake.getRaise().get() != defaultLift) {
			Robot.intake.getRaise().set(defaultLift);
		} else {
			if(Robot.elevator.getMaster().getSelectedSensorPosition(0) < SmallRaiseCommand.DIST)
				Robot.intake.open();
			Robot.intake.getRaise().set(defaultLift == DoubleSolenoid.Value.kForward ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
		}
	}
}
