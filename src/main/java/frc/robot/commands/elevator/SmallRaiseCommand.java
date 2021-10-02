package frc.robot.commands.elevator;

import frc.robot.Robot;
import frc.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SmallRaiseCommand extends InstantCommand {
	
	public static final double DIST = 0.2 * Elevator.FEET_TO_ENCODER;

    public SmallRaiseCommand() {
        addRequirements(Robot.elevator);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    		Robot.elevator.set(ControlMode.PercentOutput, 0.3);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.elevator.getMaster().getSelectedSensorPosition(0) > DIST;
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.elevator.set(ControlMode.Velocity, 0);
    		Robot.intake.close();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
