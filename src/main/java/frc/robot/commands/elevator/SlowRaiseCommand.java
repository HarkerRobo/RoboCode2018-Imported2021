package org.usfirst.frc.team1072.robot.commands.elevator;

import org.usfirst.frc.team1072.robot.Robot;
import org.usfirst.frc.team1072.robot.Slot;
import org.usfirst.frc.team1072.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SlowRaiseCommand extends Command {
	
	private double height, time;

    public SlowRaiseCommand(double height, double time) {
        requires(Robot.elevator);
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
