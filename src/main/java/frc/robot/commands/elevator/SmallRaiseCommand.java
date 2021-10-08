package frc.robot.commands.elevator;

import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj2.command.InstantCommand;

/**
 *
 */
public class SmallRaiseCommand extends InstantCommand {

    public SmallRaiseCommand() {
        addRequirements(Robot.elevator);
    }

    // Called just before this Command runs the first time
    public void initialize() {
    		Robot.elevator.set(ControlMode.PercentOutput, 0.3);
    }

    // Called repeatedly when this Command is scheduled to run
    public void execute() {
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.elevator.set(ControlMode.Velocity, 0);
    		Robot.intake.close();
    }

    // Called when another command which addRequirements one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
