package frc.robot.commands.elevator;

import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.Slot;
import frc.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 *
 */
public class ElevatorDriveCommand extends CommandBase {
	
	public static final double START_THRESHOLD = 0.3, THRESHOLD = 0.15;
	
	public static final int MANUAL_CURRENT_LIMIT = 5, MANUAL_CURRENT_PEAK = 10, MANUAL_CURRENT_PEAK_LENGTH = 50;
	
	private boolean started;
//	
//	private StickyFaults sticky = new StickyFaults();

    public ElevatorDriveCommand() {
        addRequirements(Robot.elevator);
    }

    // Called just before this Command runs the first time
    public void initialize() {
    		started = false;
    }

    // Called repeatedly when this Command is scheduled to run
    public void execute() {
//    		Robot.elevator.getMaster().getStickyFaults(sticky);
    		if(!started && (Math.abs(OI.DRIVER.getRightY()) > START_THRESHOLD)/* || sticky.ForwardLimitSwitch || sticky.ForwardSoftLimit*/) {
    			started = true;
//    			Robot.elevator.getMaster().configContinuousCurrentLimit(MANUAL_CURRENT_LIMIT, 0);
//    			Robot.elevator.getMaster().configPeakCurrentLimit(MANUAL_CURRENT_PEAK, 0);
//    			Robot.elevator.getMaster().configPeakCurrentDuration(MANUAL_CURRENT_PEAK_LENGTH, 0);
    			if(Robot.elevator.isVelocityClosedStatus() && Robot.elevator.isEncoderStatus())
    				Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_VELOCITY.getSlot(), 0);
    		}
    		if(started) {
				double speed = OI.DRIVER.getRightY();
				speed = Math.abs(OI.DRIVER.getRightY()) > THRESHOLD ? speed : 0;
				if(Elevator.LENGTH * 12000.0 - Robot.elevator.getMaster().getSelectedSensorPosition(0) < 0.5)
					speed = speed > 0 ? 0 : speed;
				if(Robot.elevator.getMaster().getSelectedSensorPosition(0)/12000.0 < 0.5)
					speed = speed < 0 ? 0 : speed;
				if(Math.abs(speed) > 0.5) speed = 0.5 * ((speed < 0) ? -1 : 1);
				SmartDashboard.putNumber("Elevator Speed", speed);
				SmartDashboard.putNumber("Sensor Position", Robot.elevator.getMaster().getSelectedSensorPosition());
				speed *= OI.DEMO_MODE_SPEED_MULTIPLIER;
				if(Robot.elevator.isVelocityClosedStatus() && Robot.elevator.isEncoderStatus()) {
    				Robot.elevator.getMaster().set(ControlMode.Velocity, speed * 2000.0, DemandType.ArbitraryFeedForward, Robot.IS_COMP ? 0.11: 0.08);
    			} else {
				Robot.elevator.set(ControlMode.PercentOutput, speed);
    			}
    		}
    }

    // Make this return true when this Command no longer needs to run execute()
    public boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.elevator.set(ControlMode.Disabled, 0);
//    		Robot.elevator.getMaster().configContinuousCurrentLimit(Config.Elevator.CONTINUOUS_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentLimit(Config.Elevator.PEAK_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentDuration(MANUAL_CURRENT_PEAK_LENGTH, 0);
    }

    // Called when another command which addRequirements one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    		Robot.elevator.set(ControlMode.Disabled, 0);
//    		Robot.elevator.getMaster().configContinuousCurrentLimit(Config.Elevator.CONTINUOUS_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentLimit(Config.Elevator.PEAK_CURRENT_LIMIT, 0);
//    		Robot.elevator.getMaster().configPeakCurrentDuration(MANUAL_CURRENT_PEAK_LENGTH, 0);
    }
}
