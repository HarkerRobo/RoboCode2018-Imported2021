package frc.robot.commands.elevator;

import frc.robot.Robot;
import frc.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

/**
 *
 */
public class SmallRaiseCommand extends InstantCommand {
	
	public static final double DIST = 0.2 * Elevator.FEET_TO_ENCODER;

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
