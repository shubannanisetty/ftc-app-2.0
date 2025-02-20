//package org.firstinspires.ftc.teamcode.opmodes;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.arcrobotics.ftclib.geometry.Pose2d;
//import com.qualcomm.hardware.lynx.LynxModule;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.hardware.VoltageSensor;
//import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.Range;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.teamcode.hardware.Arm;
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Horizontal;
//import org.firstinspires.ftc.teamcode.hardware.Intake;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.hardware.navigation.Odometry;
//import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
//import org.firstinspires.ftc.teamcode.opmodes.LoggingOpMode;
//import org.firstinspires.ftc.teamcode.util.LoopTimer;
//import org.firstinspires.ftc.teamcode.vision.AprilTagDetectionPipeline;
//import org.openftc.apriltag.AprilTagDetection;
//import org.openftc.easyopencv.OpenCvCamera;
//import org.openftc.easyopencv.OpenCvCameraFactory;
//import org.openftc.easyopencv.OpenCvCameraRotation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Disabled
//@Config
//@Autonomous(name = "!  !! High Pole Auto !!  !")
//public class HighPoleAuto extends LoggingOpMode {
//
//    private Lift lift;
//    private Horizontal horizontal;
//    private Arm arm;
//    private Intake intake;
//    private Drivetrain drivetrain;
//    private Odometry odometry;
//
//    private String result = "Nothing";
//
//    private int main_id = 0;
//    private int cs_id = 0;
//    private int park_id = 0;
//
//    private OpenCvCamera camera;
//    private AprilTagDetectionPipeline aprilTagDetectionPipeline;
//
////    private static final double FEET_PER_METER = 3.28084;
//
//    private final double fx = 578.272;
//    private final double fy = 578.272;
//    private final double cx = 402.145;
//    private final double cy = 221.506;
//
//    private final double tagsize = 0.166;
//    public static double exponent = 0.75;
//    public static double multiplier = 2.1;
//
//    private final PID arm_PID = new PID(0.009, 0, 0, 0.1, 0, 0);
//    private final PID horizontal_PID = new PID(0.008, 0, 0, 0, 0, 0);
//    private final PID lift_PID = new PID(0.02, 0, 0, 0.015, 0, 0);
//
//    private final ElapsedTime timer = new ElapsedTime();
//    private final ElapsedTime auto_timer = new ElapsedTime();
//
//    private final ElapsedTime lift_trapezoid = new ElapsedTime();;
//    private final double lift_accel = 0.39;
//
//    private double lift_target = 0;
//    private double horizontal_target = 0;
//    private double arm_target = 0;
//
//    private double lift_power;
//    private double horizontal_power;
//    private double arm_power;
//
//    public static double y1 = -49.85;
//    public static double x1 = -16.51;
//    public static double t1 = 90.0;
//    public static double y2 = -49.85;
//
//    public static double t_cs_1 = 90;
//    public static double t_cs_2 = 90;
//    public static double t_cs_3 = 90;
//    public static double t_cs_4 = 90;
//    public static double t_cs_5 = 90;
//
//    private double t_cs = t_cs_1;
//
//    public static double x_cs_1 = 11.28;
//    public static double x_cs_2 = 11.18;
//    public static double x_cs_3 = 10.78;
//    public static double x_cs_4 = 10.88;
//    public static double x_cs_5 = 11.08;
//
//    private double x_cs = x_cs_1;
//
//    public static double y_cs_1 = -49.85;
//    public static double y_cs_2 = -49.85;
//    public static double y_cs_3 = -49.85;
//    public static double y_cs_4 = -49.85;
//    public static double y_cs_5 = -49.85;
//
//    private double y_cs = y_cs_1;
//
//    public static double arm_target_cs_1 = -94;
//    public static double arm_target_cs_2 = -100;
//    public static double arm_target_cs_3 = -104.5;
//    public static double arm_target_cs_4 = -109.5;
//    public static double arm_target_cs_5 = -112;
//
//    private double arm_target_cs = arm_target_cs_1;
//
//    private double voltage_cofficient;
//
//    private boolean motion_profile = false;
//    private double lift_clip = 1;
//
//    private boolean rise = false;
//    private boolean fall = false;
//
//    //TODO Change the parkings stuff
//
//    private double voltage;
//
//    @Override
//    public void init() {
//
//        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
//
//        for (LynxModule hub : allHubs) {
//            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
//        }
//
//        voltage = getBatteryVoltage();
//        voltage_cofficient = Math.pow(((12.4/voltage) * multiplier),exponent);
//
//        super.init();
//        Robot robot = Robot.initialize(hardwareMap);
//        lift = robot.lift;
//        horizontal = robot.horizontal;
//        arm = robot.arm;
//        intake = robot.intake;
//        drivetrain = robot.drivetrain;
//        odometry = robot.odometry;
//
//        odometry.Down();
//        lift.setLatchPosition(0.08);
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
//        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
//
//        camera.setPipeline(aprilTagDetectionPipeline);
//        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
//        {
//            @Override
//            public void onOpened()
//            {
//                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
//            }
//
//            @Override
//            public void onError(int errorCode)
//            {
//
//            }
//        });
//
////        telemetry.setMsTransmissionInterval(50);
//
//        odometry.resetEncoders();
//
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//
////        dashboard = FtcDashboard.getInstance();
//
//        intake.setWristPosition(0.019);
//        intake.setClawPosition(0.37);
//
//        arm.setPower(0.5);
//        lift.setPower(-0.2);
//        horizontal.setPower(0.3);
//    }
//
//    @Override
//    public void init_loop() {
//        super.init_loop();
//
//        ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();
//
//        if(currentDetections.size() != 0) {
//
//            for (AprilTagDetection tag : currentDetections) {
//                if (tag.id == 107) {
//                    result = "FTC8813: 1";
//                    break;
//                }
//                else if (tag.id == 350) {
//                    result = "FTC8813: 2";
//                    break;
//                }
//                else if (tag.id == 25) {
//                    result = "FTC8813: 3";
//                    break;
//                }
//                else {
//                    result = "Nothing";
//                }
//
//            }
//        }
//
//
//        telemetry.addData("Detected", result);
//
//        telemetry.update();
//
//        if(arm.getLimit()){
//            arm.resetEncoders();
//            arm.setPower(0);
//        }
//
//        if(lift.getLimit()){
//            lift.resetEncoders();
//            lift.setPower(0);
//        }
//
//        if(horizontal.getLimit()){
//            horizontal.resetEncoders();
//            horizontal.setPower(0);
//        }
//
//        lift.setHolderPosition(0.12);
//
//        arm.resetEncoders();
//        lift.resetEncoders();
//        horizontal.resetEncoders();
//        odometry.resetEncoders();
//    }
//
//
//    @Override
//    public void start() {
//        super.start();
////        drivetrain.resetEncoders();
//        auto_timer.reset();
//    }
//
//    @Override
//    public void loop() {
//
//        arm.updatePosition();
//        lift.updatePosition();
//        horizontal.updatePosition();
//        drivetrain.updateHeading();
//
//        rise = false;
//        fall = false;
//
//        odometry.updatePose(-drivetrain.getHeading());
//        Pose2d odometryPose = odometry.getPose();
//
//        motion_profile = false;
//
//        switch (main_id) {
//            case 0:
//                drivetrain.autoMove(-10, 3, 0, 1.5, 1.5, 2, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                    lift_target = 400;
//                    lift_trapezoid.reset();
//                }
//                break;
//            case 1:
//                drivetrain.autoMove(-42.48, 4, 0, 1.5, 2, 2, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 2:
//                drivetrain.autoMove(-42.48, 4, 111.8125, 1.5, 1.5, 1.5, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                    horizontal_target = -680;
//                    lift.setHolderPosition(0.39);
//                    lift_target = 440;
//                    lift_trapezoid.reset();
//                }
//                break;
//            case 3:
//                drivetrain.autoMove(-42.48, 2.209, 111.8125, 0.6, 0.6, 1, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                    lift.setLatchPosition(0.356);
//                    lift_target = 0;
//                }
//                break;
//            case 4:
//                if (lift.getCurrentPosition() < 200) {
//                    lift_clip = 0.17;
//                    lift.setHolderPosition(0.095);
//                    main_id += 1;
//                }
//            case 5:
//                if (lift.getCurrentPosition() < 150) {
//                    main_id += 1;
//                    lift_clip = 1;
//                }
//                break;
//            case 6:
//                drivetrain.autoMove(-45, 2.209, 111.8125, 1.2, 1.2, 2, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//
//            case 7:
//                drivetrain.autoMove(-45.573, 9.2, 100, 0.7, 0.7, 0.5, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                    arm_target = arm_target_cs;
//                    timer.reset();
//                }
//                break;
//            case 8:
//                if (arm.getCurrentPosition() < (arm_target_cs + 5) && arm.getCurrentPosition() > (arm_target_cs - 15) && timer.seconds() > 0.7) {
//                    timer.reset();
//                    main_id += 1;
//                }
//                break;
//            case 9:
//                if (horizontal_target >= -800) {
//                    horizontal_target -= 1;
//                }
//                if (intake.getDistance() < 17) {
//                    intake.setClawPosition(0.1);
//                    timer.reset();
//                    main_id += 1;
//                }
//                break;
//            case 10:
//                if (timer.seconds() > 0.8) {
//                    horizontal_target = -400;
//                    arm_target = -30;
//                    main_id += 1;
//                }
//                break;
//            case 11:
//                if (arm.getCurrentPosition() > -70) {
//                    intake.setWristPosition(0.678);
//                    timer.reset();
//                    main_id += 1;
//                }
//                break;
//            case 12:
//                if (timer.seconds() > 0.5) {
//                    horizontal_target = 0;
//                    arm_target = 50;
//                    main_id += 1;
//                }
//                break;
//            case 13:
//                if (arm.getCurrentPosition() > -5 && horizontal.getCurrentPosition() > -50) {
//                    lift.setLatchPosition(0.08);
//                    intake.setClawPosition(0.37);
//                    arm_target = -28;
//                    horizontal_target = -680;
//                    lift.setHolderPosition(0.39);
//                    lift_target = 450;
//                    lift_trapezoid.reset();
//                    main_id += 1;
//                }
//                break;
//            case 14:
//                drivetrain.autoMove(-42.48, 2.209, 111.8125, 0.75, 0.65, 1, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                    lift_target = 0;
//                    lift.setLatchPosition(0.356);
//                }
//                break;
//            case 15:
//                if (lift.getCurrentPosition() < 200) {
//                    lift_clip = 0.17;
//                    lift.setHolderPosition(0.095);
//                    main_id += 1;
//                }
//            case 16:
//                if (lift.getCurrentPosition() < 150) {
//                    main_id += 1;
//                    lift_clip = 1;
//                }
//                break;
//            case 17:
//                drivetrain.autoMove(-45, 2.209, 111.3125, 1.5, 1.5, 1.5, odometryPose, telemetry);
//                if (drivetrain.hasReached()) {
//                    cs_id += 1;
//                    if (cs_id > 4) {
//                        main_id += 1;
//                    }
//                    else {
//                        main_id = 7;
//                        intake.setWristPosition(0.019);
//                    }
//                }
//                break;
//        }
//
//        switch (cs_id) {
//            case 0:
//                arm_target_cs = arm_target_cs_1;
//                break;
//            case 1:
//                arm_target_cs = arm_target_cs_2;
//                break;
//            case 2:
//                arm_target_cs = arm_target_cs_3;
//                break;
//            case 3:
//                arm_target_cs = arm_target_cs_4;
//                break;
//            case 4:
//                arm_target_cs = arm_target_cs_5;
//                break;
//        }
//
//        lift_power = Range.clip((lift_PID.getOutPut(lift_target, lift.getCurrentPosition(), 1) * Math.min(lift_trapezoid.seconds() * lift_accel, 1)), -lift_clip, lift_clip); //change
//        horizontal_power = horizontal_PID.getOutPut(horizontal_target,horizontal.getCurrentPosition(),0); //change
//        arm_power = Range.clip(arm_PID.getOutPut(arm_target, arm.getCurrentPosition(), Math.cos(Math.toRadians(arm.getCurrentPosition() + 0))), -1, 0.5); //change
//
//        lift.setPower(lift_power);
//        horizontal.setPower(horizontal_power);
//        arm.setPower((arm_power*voltage_cofficient));
//
//        drivetrain.update(odometryPose, telemetry,motion_profile, main_id, rise, fall, voltage);
//
//        telemetry.addData("Main ID", main_id);
////        telemetry.addData("Voltage", getBatteryVoltage());
////        telemetry.addData("Coefficient", voltage_cofficient);
//        telemetry.addData("Distance", intake.getDistance());
//        telemetry.addData("Time", auto_timer.seconds());
//        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
//        telemetry.update();
//
//        LoopTimer.resetTimer();
//    }
//
//    @Override
//    public void stop() {
//        super.stop();
//    }
//
//    double getBatteryVoltage() {
//        double result = Double.POSITIVE_INFINITY;
//        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
//            double voltage = sensor.getVoltage();
//            if (voltage > 0) {
//                result = Math.min(result, voltage);
//            }
//        }
//        return result;
//    }
//
//}