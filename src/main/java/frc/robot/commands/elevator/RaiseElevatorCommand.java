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
public class RaiseElevatorCommand extends InstantCommand {
	
	public static final double OPEN_LOOP_SPEED = 0.45;
	
	public RaiseElevatorCommand() {
		addRequirements(Robot.elevator);
	}
	
	// Called just before this Command runs the first time
	protected void initialize() {
		Robot.elevator.getMaster().clearStickyFaults(0);
		Robot.intake.raise();
//		Robot.elevator.getMaster().setIntegralAccumulator(0, 0, 0);
		if(false && Robot.elevator.isMotionMagicStatus() && Robot.elevator.isEncoderStatus()) {
			Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_MOTION_MAGIC.getSlot(), 0);
			Robot.elevator.set(ControlMode.MotionMagic, Elevator.LENGTH - Elevator.BUFFER * 4.0);
		} else if(Robot.elevator.isPositionClosedStatus() && Robot.elevator.isEncoderStatus()) {
			Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_POSITION.getSlot(), 0);
			Robot.elevator.set(ControlMode.Position, Elevator.LENGTH - Elevator.BUFFER * 4.0);
		} else {
			Robot.elevator.set(ControlMode.PercentOutput, OPEN_LOOP_SPEED);
		}
	}
}
