package frc.robot.commands.intake;

import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *
 */
public class JiggleCommand extends TimedCommand {

	public static final double TIMEOUT = 0.4;
	public static final double IN_SPEED = 0.7, OUT_SPEED = -0.3;
	
    public JiggleCommand() {
        super(TIMEOUT);
        requires(Robot.intake);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    		Robot.intake.getLeftRoller().set(ControlMode.PercentOutput, OUT_SPEED);
    		Robot.intake.getRightRoller().set(ControlMode.PercentOutput, IN_SPEED);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Called once after timeout
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
