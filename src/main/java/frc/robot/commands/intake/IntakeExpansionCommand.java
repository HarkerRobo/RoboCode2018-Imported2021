package frc.robot.commands.intake;

import frc.robot.Robot;
import frc.robot.commands.elevator.SmallRaiseCommand;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 *
 */
public class IntakeExpansionCommand extends CommandBase {
	
	public IntakeExpansionCommand() {
		addRequirements(Robot.intake);
	}
	
	// Called just before this Command runs the first time
	public void initialize() {
		if(Robot.intake.isOpen()) {
			if(Robot.elevator.getMaster().getSelectedSensorPosition(0) > SmallRaiseCommand.DIST) {
				Robot.intake.close();
			} else {
				CommandScheduler.getInstance().schedule(new SmallRaiseCommand());
				CommandScheduler.getInstance().run();
			}
		} else  {
			Robot.intake.open();
		}
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.command.Command#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return true;
	}
}
