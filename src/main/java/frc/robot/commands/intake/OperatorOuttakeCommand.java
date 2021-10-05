package frc.robot.commands.intake;

import frc.robot.OI;
import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 *
 */
public class OperatorOuttakeCommand extends CommandBase {

    public OperatorOuttakeCommand() {
        addRequirements(Robot.intake);
    }

    // Called just before this Command runs the first time
    public void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    public void execute() {
    		Robot.intake.getLeftRoller().set(ControlMode.PercentOutput, -OI.OPERATOR.getLeftTrigger());
    		Robot.intake.getRightRoller().set(ControlMode.PercentOutput, -OI.OPERATOR.getRightTrigger());
    }

    // Make this return true when this Command no longer needs to run execute()
    public boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.intake.getLeftRoller().set(ControlMode.Disabled, 0);
    		Robot.intake.getRightRoller().set(ControlMode.Disabled, 0);
    }

    // Called when another command which addRequirements one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    		Robot.intake.getLeftRoller().set(ControlMode.Disabled, 0);
		Robot.intake.getRightRoller().set(ControlMode.Disabled, 0);
    }
}
