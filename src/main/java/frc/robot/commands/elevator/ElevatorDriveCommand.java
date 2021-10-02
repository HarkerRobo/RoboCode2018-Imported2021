package org.usfirst.frc.team1072.robot.commands.elevator;

import org.usfirst.frc.team1072.robot.Config;
import org.usfirst.frc.team1072.robot.OI;
import org.usfirst.frc.team1072.robot.Robot;
import org.usfirst.frc.team1072.robot.Slot;
import org.usfirst.frc.team1072.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.StickyFaults;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ElevatorDriveCommand extends Command {
	
	public static final double START_THRESHOLD = 0.3, THRESHOLD = 0.15;
	
	public static final int MANUAL_CURRENT_LIMIT = 5, MANUAL_CURRENT_PEAK = 10, MANUAL_CURRENT_PEAK_LENGTH = 50;
	
	private boolean started;
//	
//	private StickyFaults sticky = new StickyFaults();

    public ElevatorDriveCommand() {
        requires(Robot.elevator);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    		started = false;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
//    		Robot.elevator.getMaster().getStickyFaults(sticky);
    		if(!started && (Math.abs(OI.gamepad.getRightY()) > START_THRESHOLD)/* || sticky.ForwardLimitSwitch || sticky.ForwardSoftLimit*/) {
    			started = true;
//    			Robot.elevator.getMaster().configContinuousCurrentLimit(MANUAL_CURRENT_LIMIT, 0);
//    			Robot.elevator.getMaster().configPeakCurrentLimit(MANUAL_CURRENT_PEAK, 0);
//    			Robot.elevator.getMaster().configPeakCurrentDuration(MANUAL_CURRENT_PEAK_LENGTH, 0);
    			if(Robot.elevator.isVelocityClosedStatus() && Robot.elevator.isEncoderStatus())
    				Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_VELOCITY.getSlot(), 0);
    		}
    		if(started) {
    			double speed = OI.gamepad.getRightY();
    			speed = Math.min(speed, (Elevator.LENGTH - Robot.elevator.getMaster().getSelectedSensorPosition(0)) / 12000.0);
    			speed = Math.max(speed, -Robot.elevator.getMaster().getSelectedSensorPosition(0) / 12000.0);
    			if(Robot.elevator.isVelocityClosedStatus() && Robot.elevator.isEncoderStatus()) {
    				Robot.elevator.getMaster().set(ControlMode.Velocity, Math.abs(OI.gamepad.getRightY()) > THRESHOLD ? speed * 2000.0 : 0, DemandType.ArbitraryFeedForward, Robot.IS_COMP ? 0.11: 0.08);
    			} else {
				Robot.elevator.set(ControlMode.PercentOutput, Math.abs(OI.gamepad.getRightY()) > THRESHOLD ? speed : 0);
    			}
    		}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.elevator.set(ControlMode.Disabled, 0);
//    		Robot.elevator.getMaster().configContinuousCurrentLimit(Config.Elevator.CONTINUOUS_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentLimit(Config.Elevator.PEAK_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentDuration(MANUAL_CURRENT_PEAK_LENGTH, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    		Robot.elevator.set(ControlMode.Disabled, 0);
//    		Robot.elevator.getMaster().configContinuousCurrentLimit(Config.Elevator.CONTINUOUS_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentLimit(Config.Elevator.PEAK_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentDuration(MANUAL_CURRENT_PEAK_LENGTH, 0);
    }
}
