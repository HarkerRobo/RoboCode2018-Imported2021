package frc.robot.commands.intake;

import frc.robot.OI;
import frc.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 *
 */
public class IntakeCommand extends CommandBase {
	
	public static final int inSign = -1;
	public static final double INTAKE_LEFT = inSign * 0.8, INTAKE_RIGHT = inSign * 1.0;
	public static final double OUTTAKE_LEFT = -inSign * 0.6, OUTTAKE_RIGHT = -inSign * 0.6;
	public static final double SLOW_OUTTAKE_LEFT = -inSign * 0.4, SLOW_OUTTAKE_RIGHT = -inSign * 0.4;
	public static final double SLOW_HEIGHT = 25000;
	
	private boolean outtaking;

    public IntakeCommand() {
        addRequirements(Robot.intake);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    		if(OI.gamepad.getRightTriggerPressed()) {
//    			Robot.intake.open();
    			Robot.intake.getLeftRoller().set(ControlMode.PercentOutput, INTAKE_LEFT);
    			Robot.intake.getRightRoller().set(ControlMode.PercentOutput, INTAKE_RIGHT);
    		} else if (OI.gamepad.getLeftTriggerPressed()) {
    			outtaking = true;
    			if(Robot.elevator.getMaster().getSelectedSensorPosition(0) > SLOW_HEIGHT) {
    				Robot.intake.getLeftRoller().set(ControlMode.PercentOutput, SLOW_OUTTAKE_LEFT);
    				Robot.intake.getRightRoller().set(ControlMode.PercentOutput, SLOW_OUTTAKE_RIGHT);
    			} else {
    				Robot.intake.getLeftRoller().set(ControlMode.PercentOutput, OUTTAKE_LEFT);
    				Robot.intake.getRightRoller().set(ControlMode.PercentOutput, OUTTAKE_RIGHT);
    			}
    		} else {
    			if(outtaking) {
    				outtaking = false;
    				Robot.intake.open();
    			}
    			Robot.intake.getLeftRoller().set(ControlMode.PercentOutput, OI.operator.getLeftY() * Math.abs(OI.operator.getLeftY()) * 0.8);
    			Robot.intake.getRightRoller().set(ControlMode.PercentOutput, OI.operator.getRightY() * Math.abs(OI.operator.getRightY()) * 0.8);
    		}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    		Robot.intake.getLeftRoller().set(ControlMode.Disabled, 0);
    		Robot.intake.getRightRoller().set(ControlMode.Disabled, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    		Robot.intake.getLeftRoller().set(ControlMode.Disabled, 0);
    		Robot.intake.getRightRoller().set(ControlMode.Disabled, 0);
    }
}
