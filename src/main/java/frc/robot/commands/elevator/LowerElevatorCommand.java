package frc.robot.commands.elevator;

import frc.robot.Robot;
import frc.robot.Slot;
import frc.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class LowerElevatorCommand extends InstantCommand {
	
	public static final double OPEN_LOOP_SPEED = -0.45;
	
	public LowerElevatorCommand() {
		addRequirements(Robot.elevator);
	}
	
	// Called just before this Command runs the first time
	protected void initialize() {
		Robot.intake.open();
		if(false && Robot.elevator.isMotionMagicStatus() && Robot.elevator.isEncoderStatus()) {
			Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_MOTION_MAGIC.getSlot(), 0);
			Robot.elevator.set(ControlMode.MotionMagic, Elevator.BUFFER);
		} else if(Robot.elevator.isPositionClosedStatus() && Robot.elevator.isEncoderStatus()) {
			Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_POSITION.getSlot(), 0);
			Robot.elevator.set(ControlMode.Position, Elevator.BUFFER);
		} else {
			Robot.elevator.set(ControlMode.PercentOutput, OPEN_LOOP_SPEED);
		}
	}
}
