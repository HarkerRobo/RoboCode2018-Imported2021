package frc.robot.commands.intake;

import frc.robot.OI;
import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class OperatorIntakeCommand extends Command {

    public OperatorIntakeCommand() {
        requires(Robot.intake);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    		Robot.intake.getLeftRoller().set(ControlMode.PercentOutput, OI.operator.getLeftTrigger());
    		Robot.intake.getRightRoller().set(ControlMode.PercentOutput, OI.operator.getRightTrigger());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.intake.getLeftRoller().set(ControlMode.Disabled, 0);
    		Robot.intake.getRightRoller().set(ControlMode.Disabled, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    		Robot.intake.getLeftRoller().set(ControlMode.Disabled, 0);
		Robot.intake.getRightRoller().set(ControlMode.Disabled, 0);
    }
}
