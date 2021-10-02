package frc.robot.commands.elevator;

import frc.robot.Robot;
import frc.robot.Slot;
import frc.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 *
 */
public class SlowRaiseCommand extends CommandBase {
	
	private double height, time;

    public SlowRaiseCommand(double height, double time) {
        addRequirements(Robot.elevator);
        this.height = height * Elevator.FEET_TO_ENCODER;
        this.time = time; //ms
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    		double cruise = height * 100.0 / (time - 500);
    		Robot.elevator.getMaster().configMotionCruiseVelocity((int) cruise, 0);
    		Robot.elevator.getMaster().configMotionAcceleration((int) (cruise * 2), 0);
		Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_MOTION_MAGIC.getSlot(), 0);
		Robot.elevator.set(ControlMode.MotionMagic, height);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    		
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
