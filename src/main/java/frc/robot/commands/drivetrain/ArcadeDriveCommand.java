package frc.robot.commands.drivetrain;

import frc.robot.*;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;

/**
 * Drive in either arcade or tank drive
 */
public class ArcadeDriveCommand extends CommandBase {
	
	public static final double THRESHOLD = 0.05, MAX_SPEED = 5000;

	private double MIN_ACC = 10; // in feet per seconds squared
	private double TOP_MAX_SPEED = 0.4;

	public ArcadeDriveCommand() {
        addRequirements(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    public void initialize() {
    		if(Robot.drivetrain.isEncoderStatus() && Robot.drivetrain.isVelocityClosedStatus()) {
    			Robot.drivetrain.getLeft().selectProfileSlot(Slot.LEFT_VELOCITY.getSlot(), RobotMap.Drivetrain.ENCODER);
    			Robot.drivetrain.getRight().selectProfileSlot(Slot.RIGHT_VELOCITY.getSlot(), RobotMap.Drivetrain.ENCODER);
    		}
    }

    // Called repeatedly when this Command is scheduled to run
    public void execute() {
    		if(Robot.drivetrain.isMotorStatus()) {

				double elevPos = Elevator.getInstance().getMaster().getSelectedSensorPosition(0) / ((double) Elevator.LENGTH);
				// elevPos 0 at bottom, 1 at top

				double speedMod = elevPos * (TOP_MAX_SPEED - 1) + 1;

				double TOP_MAX_SPEED_FEET = speedMod * 46000 / 4096.0 * 4.0 * Math.PI / 12.0; // in feet per seconds
				double MIN_RAMP_TIME = TOP_MAX_SPEED_FEET / MIN_ACC;
 
				double rampTime = Config.Drivetrain.RAMP_SPEED;
				rampTime += (MIN_RAMP_TIME - rampTime) * elevPos;

				Drivetrain.getInstance().getLeft().configOpenloopRamp(rampTime, 0);
				Drivetrain.getInstance().getRight().configOpenloopRamp(rampTime, 0);

    			double x = OI.DRIVER.getLeftX();
    			double y = OI.DRIVER.getLeftY();
    			double k = Math.max(1.0, Math.max(Math.abs(y + x * x), Math.abs(y - x * x)));
    			double left = (y + x * Math.abs(x)) / k;
    			double right = (y - x * Math.abs(x)) / k;
    			if(Math.abs(left) < THRESHOLD && Math.abs(right) < THRESHOLD) {
    				left = 0;
    				right = 0;
    			}
	    		if(Robot.drivetrain.isEncoderStatus() && Robot.drivetrain.isVelocityClosedStatus()) {
	    			Robot.drivetrain.getLeft().set(ControlMode.Velocity, MAX_SPEED * left * speedMod);
					Robot.drivetrain.getRight().set(ControlMode.Velocity, MAX_SPEED * right * speedMod);
	    		} else {
	    			Robot.drivetrain.getLeft().set(ControlMode.PercentOutput, left * speedMod);
					Robot.drivetrain.getRight().set(ControlMode.PercentOutput, right * speedMod);
					System.out.println(Robot.drivetrain.getRightFollower().getMotorOutputPercent());
	    		}
    		}
    }

    // Make this return true when this Command no longer needs to run execute()
    public boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.drivetrain.set((talon) -> talon.set(ControlMode.Disabled, 0));
    }

    // Called when another command which addRequirements one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    		Robot.drivetrain.set((talon) -> talon.set(ControlMode.Disabled, 0));
    }
}
