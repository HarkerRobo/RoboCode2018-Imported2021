package frc.robot.commands.elevator;

import frc.robot.Robot;
import frc.robot.Slot;
import frc.robot.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj2.command.InstantCommand;

/**
 * Command to move the elevator to a specific height
 */
public class SetElevatorCommand2 extends InstantCommand {

    private final double height;
    
	/**
	 * Initializes an elevator command
	 * @param height height (in feet) above the default position
	 */
    public SetElevatorCommand2(double height) {
        addRequirements(Robot.elevator);
        this.height = height * Elevator.FEET_TO_ENCODER;
    }

    //Called just before this Command runs the first time
    public void initialize() {
//    		Robot.elevator.getMaster().setIntegralAccumulator(0, 0, 0);
    		if(Robot.elevator.isMotionMagicStatus() && Robot.elevator.isEncoderStatus()) {
    			Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_MOTION_MAGIC.getSlot(), 0);
    			Robot.elevator.set(ControlMode.MotionMagic, height);
    		} else if(Robot.elevator.isPositionClosedStatus() && Robot.elevator.isEncoderStatus()) {
    			Robot.elevator.getMaster().selectProfileSlot(Slot.ELEVATOR_SMALL_POSITION.getSlot(), 0);
    			Robot.elevator.set(ControlMode.Position, height);
    		}
    }
}
