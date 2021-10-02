package frc.robot.commands.intake;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetSolenoidCommand extends InstantCommand {
	
	private DoubleSolenoid sol;
	private Value val;

    public SetSolenoidCommand(DoubleSolenoid sol, Value val) {
        this.sol = sol;
        this.val = val;
        addRequirements(Robot.intake);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    		sol.set(val);
    }
}
