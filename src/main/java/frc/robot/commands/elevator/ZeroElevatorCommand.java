package org.usfirst.frc.team1072.robot.commands.elevator;

import org.usfirst.frc.team1072.robot.Config;
import org.usfirst.frc.team1072.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ZeroElevatorCommand extends Command {
	
	public static final double STALL_CURRENT = 7;
	
	public static final double SPEED = -0.3 - 0.1;
	
	public ZeroElevatorCommand() {
		requires(Robot.elevator);
	}
	
	// Called just before this Command runs the first time
	protected void initialize() {
		Robot.log("began zeroing elevator");
		Robot.elevator.set(ControlMode.PercentOutput, SPEED);
		Robot.elevator.getMaster().configForwardSoftLimitEnable(false, 0);
		Robot.elevator.getMaster().configReverseSoftLimitEnable(false, 0);
		Robot.elevator.getMaster().overrideSoftLimitsEnable(true);
	}
	
	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if(Robot.elevator.getMaster().getSensorCollection()
				.isRevLimitSwitchClosed() == (Config.Elevator.REVERSE_NORMAL == LimitSwitchNormal.NormallyOpen)) {
			return true;
		} else if(Robot.elevator.getMaster().getOutputCurrent() > STALL_CURRENT) {
			Robot.elevator.getMaster().setSelectedSensorPosition(0, 0, 0);
			return true;
		}
		return false;
	}
	
	// Called once after isFinished returns true
	protected void end() {
		Robot.elevator.set(ControlMode.Disabled, 0);
		if(Robot.elevator.isEncoderStatus()) {
			Robot.log("Correctly zeroed elevator");
			Robot.elevator.getMaster().configForwardSoftLimitEnable(true, 0);
			Robot.elevator.getMaster().configReverseSoftLimitEnable(true, 0);
			Robot.elevator.getMaster().overrideSoftLimitsEnable(false);
		}
	}
	
	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		Robot.elevator.set(ControlMode.Disabled, 0);
	}
}
