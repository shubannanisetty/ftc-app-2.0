package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Status;

import static com.qualcomm.robotcore.hardware.DigitalChannel.Mode.INPUT;

public class FourBar {
    private final DcMotor arm;
    private final Servo dropper;
    private final DigitalChannel limit_checker;

    private double target_pos;


    public FourBar(DcMotor arm, Servo dropper, DigitalChannel limit_checker){
        this.arm = arm; // Encoder and motor on same port
        this.dropper = dropper;
        this.limit_checker = limit_checker;
        this.limit_checker.setMode(INPUT);
    }


    public double getCurrentArmPos(){
        return arm.getCurrentPosition();
    }

    public double getCurrentDropperPos(){
        return dropper.getPosition();
    }


    public void dropperExtendLeft(){
        this.dropper.setPosition(Status.DEPOSIT_EXTEND_LEFT);
    }


    public void dropperExtendRight(){
        this.dropper.setPosition(Status.DEPOSIT_EXTEND_LEFT);
    }


    public void dropperRetract(){
        this.dropper.setPosition(Status.DEPOSIT_RETRACT);
    }


    public void rotate(double delta_ticks){
        double curr_pos = arm.getCurrentPosition();
        if (Status.LOWER_LIMIT < (curr_pos + delta_ticks) && (curr_pos + delta_ticks) < Status.UPPER_LIMIT){
            target_pos += delta_ticks;
        }
    }


    public void update(){
        double curr_pos = arm.getCurrentPosition();

        if (Status.LOWER_LIMIT < curr_pos && curr_pos < Status.UPPER_LIMIT) {
            double ratio = (target_pos - curr_pos) / Status.UPPER_LIMIT;
            arm.setPower(Status.kP * ratio);
        }
    }
}
