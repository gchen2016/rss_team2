package Grasping;

import org.ros.node.NodeMain;
import org.ros.namespace.GraphName;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.message.rss_msgs.*;
import org.ros.message.MessageListener;
import org.ros.node.Node;

import java.util.Arrays;

//import org.ros.message.lab7_msgs.*;

//import VisualServo.Image;
//import VisualServo.MotionMsg;
//import VisualServo.*;

public class Grasping implements NodeMain {
    private Publisher<ArmMsg> armPWMPub;
    private Subscriber<ArmMsg> armStatusSub;
    private Subscriber<BumpMsg> bumpersSub;
    private Publisher<MotionMsg> motionPub;
    private Subscriber<OdometryMsg> odometrySub;

    // PART 4
    // public Subscriber<org.ros.message.sensor_msgs.Image> vidSub;
    // public Publisher<org.ros.message.sensor_msgs.Image> vidPub;
    // END PART 4

    private State currState;
    private ArmGymState gymState;
    private ArmGraspState graspState;
    private ShoulderController shoulderServo;
    private WristController wristServo;
    private GripperController gripperServo;
    protected boolean objDetected = false;
    protected boolean objGrasped = false;

    // Robot Odometry
    private double robotX = 0.0; // in meters
    private double robotY = 0.0; // in meters
    private double robotTheta = 0.0; // in radians

    // Start Positions of the Move
    private double startX = 0.0; // in meters
    private double startY = 0.0; // in meters
    private double startTheta = 0.0; // in radians
    // Goal Positions of the Move
    private double goalX = 0.0; // in meters
    private double goalY = 0.0; // in meters
    private double goalTheta = 0.0; // in radians

    private double MOVE_DIST = 0.25; // in meters

    double FWD_GAIN = .5;
    double ROT_GAIN = .2;
    double DIST_TOL = 0.05; // tolerance to move MOVE_DIST in meters

    public enum ArmGraspState {
        INITIALIZE, OPEN_GRIPPER, FIND_OBJ, GRASP, LIFT, MOVE, DEPOSIT_WRIST, DEPOSIT_SHOULDER, DEPOSIT_GRIPPER, RETURN
    }

    // States for Arm Gymnastics
    public enum ArmGymState {
        INITIALIZE, OPEN_GRIPPER, CLOSE_GRIPPER, MOVE_UP, BEND_ELBOW, MOVE_TO_GROUND
    }

    // States dividing up space so that no servo can move more than 1 radian per
    // iteration
    public enum State {
        UP, DOWN
    }

    public Grasping() {
        currState = State.DOWN;
        gymState = ArmGymState.INITIALIZE; // gymnastics
        graspState = ArmGraspState.INITIALIZE; // gymnastics
        shoulderServo = new ShoulderController(525, 2375, Math.PI, 1500, 525);
        wristServo = new WristController(350, 2250, Math.PI, 1250, 2025);
        gripperServo = new GripperController(1700, 2450, Math.PI, 1700, 2450);
    }

    /*
     * @Override public void run() { while (true) { Image src = null; try { src
     * = new Image(visionImage.take(), width, height); } catch
     * (InterruptedException e) { e.printStackTrace(); continue; }
     * 
     * Image dest = new Image(src);
     * 
     * blobTrack.apply(src, dest);
     * 
     * // update newly formed vision message gui.setVisionImage(dest.toArray(),
     * width, height);
     * 
     * // Begin Student Code
     * 
     * // publish velocity messages to move the robot towards the target
     * MotionMsg msg = new MotionMsg(); // (Solution) msg.translationalVelocity
     * = blobTrack.translationVelocityCommand; // (Solution)
     * msg.rotationalVelocity = blobTrack.rotationVelocityCommand; // (Solution)
     * publisher.publish(msg); // (Solution)
     * 
     * // End Student Code } }
     */

    @Override
    public void onStart(Node node) {
        armPWMPub = node.newPublisher("command/Arm", "rss_msgs/ArmMsg");
        armStatusSub = node.newSubscriber("rss/ArmStatus", "rss_msgs/ArmMsg");
        bumpersSub = node.newSubscriber("/rss/BumpSensors", "rss_msgs/BumpMsg");
        motionPub = node.newPublisher("command/Motors", "rss_msgs/MotionMsg");
        odometrySub = node.newSubscriber("/rss/odometry",
                "rss_msgs/OdometryMsg");

        // Initialization State

        // For debugging
        /*
         * System.out.println("Shoulder Servo Slope: " +
         * shoulderServo.LINE_SLOPE);
         * System.out.println("Shoulder Servo Intercept: " +
         * shoulderServo.LINE_THETA_INTERCEPT);
         * System.out.println("Wrist Servo Slope: " + wristServo.LINE_SLOPE);
         * System.out.println("Wrist Servo Intercept: " +
         * wristServo.LINE_THETA_INTERCEPT);
         */
        // PART 4

        // blobTrack = new BlobTracking(width, height);

        // blobTrack.targetHueLevel = target_hue_level;

        // vidPub = node.newPublisher("/rss/blobVideo", "sensor_msgs/Image");
        // vidSub = node.newSubscriber("/rss/video","sensor_msgs/Image");
        // Image dest = new Image(src);
        // ;
        /*
         * vidSub.addMessageListener(new
         * MessageListener<org.ros.message.sensor_msgs.Image>() {
         * 
         * @Override public void onNewMessage(org.ros.message.sensor_msgs.Image
         * message) {
         * 
         * 
         * org.ros.message.sensor_msgs.Image pubImage = new
         * org.ros.message.sensor_msgs.Image(); pubImage.width = width;
         * pubImage.height = height; pubImage.encoding = "rgb8";
         * pubImage.is_bigendian = 0; pubImage.step = width*3; pubImage.data =
         * dest.toArray(); vidPub.publish(pubImage); } });
         */

        // END PART 4

        armStatusSub.addMessageListener(new MessageListener<ArmMsg>() {

            int[] pwm_stat = new int[3];
            int wrist = 0;
            int shoulder = 0;
            int gripper = 0;

            public void sendCommands() {
                pwm_stat[0] = shoulder;
                pwm_stat[1] = wrist;
                pwm_stat[2] = gripper;

                ArmMsg msg = new ArmMsg();
                msg.pwms[0] = pwm_stat[0];
                msg.pwms[1] = pwm_stat[1];
                msg.pwms[2] = pwm_stat[2];
                armPWMPub.publish(msg);

                //System.out.println("message sent");
                //System.out.println(Arrays.toString(pwm_stat));
            }

            @Override
            public void onNewMessage(ArmMsg msg) {

                // long[] pwmVals = msg.pwms;
                int[] pwmVals = pwm_stat;
                int shoulderPWM = (int) pwmVals[0];
                int wristPWM = (int) pwmVals[1];
                int gripperPWM = (int) pwmVals[2];

                System.out.println(Arrays.toString(pwmVals));

                // try {
                // Thread.sleep(1000);
                // } catch (Exception e) {
                //
                // }

                // BEGIN GYMNASTICS
                // TO DO MAKE INTO STATIC METHOD IF IT PLEASES YOU
                // /////////////////////////////////////////////////////////////////////////////////////////////
                // // Arm Gymnastics COMPLETED 3/17/15. If you do not like the
                // // video, adjust the ranges to the appropriate section and
                // rerun
                // System.out.println("Current State: " + gymState);
                // if (gymState == ArmGymState.INITIALIZE) {
                // System.out.println("going to initialization state...");
                // int shoulder_init_value = shoulderServo.GYM_GROUND_PWM;
                // int wrist_init_value = wristServo.MIN_PWM;
                // int gripper_init_value = gripperServo.MIN_PWM;
                // initializeServos(shoulder_init_value, wrist_init_value,
                // gripper_init_value);
                // try {
                // Thread.sleep(1000);
                // } catch (Exception e) {
                //
                // }
                // System.out.println("at initilization state...");
                // gymState = ArmGymState.OPEN_GRIPPER;
                //
                // } else if (gymState == ArmGymState.OPEN_GRIPPER) {
                // // #1
                // System.out.println("Opening Gripper");
                // System.out.println(gripperServo.isOpen(gripperPWM));
                // if (gripperServo.isOpen(gripperPWM)) {
                // gymState = ArmGymState.CLOSE_GRIPPER;
                // } else {
                // writeGripperPWM(2450);
                // }
                // }
                //
                // else if (gymState == ArmGymState.CLOSE_GRIPPER) {
                // // #2
                // System.out.println("Close Gripper");
                // if (gripperServo.isClosed(gripperPWM)) {
                // gymState = ArmGymState.MOVE_UP;
                // } else {
                // writeGripperPWM(1700);
                // }
                // }
                //
                // else if (gymState == ArmGymState.MOVE_UP) {
                // // #3
                // System.out.println("Move Arm Up");
                // if (shoulderServo.isGymUp(shoulderPWM)) {
                // gymState = ArmGymState.BEND_ELBOW;
                // } else {
                // writeShoulderPWM(shoulderServo.moveGymUp(shoulderPWM));
                // }
                // }
                //
                // else if (gymState == ArmGymState.BEND_ELBOW) {
                // // #4
                // System.out.println("Bend Elbow");
                // if (wristServo.isGymBent(wristPWM)) {
                // gymState = ArmGymState.MOVE_TO_GROUND;
                // } else {
                // writeWristPWM(wristServo.bendGym(wristPWM));
                // }
                // }
                //
                // else if (gymState == ArmGymState.MOVE_TO_GROUND) {
                // // #5
                // System.out.println("Move to Ground");
                // if (shoulderServo.onGround(shoulderPWM)) {
                // gymState = ArmGymState.OPEN_GRIPPER;
                // } else {
                // writeShoulderPWM(shoulderServo
                // .moveToGround(shoulderPWM));
                // }
                // }

                // // Grasp and Transport
                // if (graspState == ArmGraspState.INIT_WRIST) {
                // if (wristServo.isGymBent(wristPWM)) {
                // graspState = ArmGraspState.INIT_GRIPPER;
                // } else {
                // writeWristPWM(wristServo.bendGym(wristPWM));
                // }
                // }
                //
                // if (graspState == ArmGraspState.INIT_GRIPPER) {
                // if (gripperServo.isOpen(gripperPWM)) {
                // graspState = ArmGraspState.GRASP;
                // } else {
                // writeGripperPWM(gripperServo.open(gripperPWM));
                // }
                // }
                //
                // if (graspState == ArmGraspState.INIT_SHOULDER) {
                // if (shoulderServo.onGround(shoulderPWM)) {
                // graspState = ArmGraspState.GRASP;
                // } else {
                // writeShoulderPWM(shoulderServo
                // .moveToGround(shoulderPWM));
                // }
                // }
                // END OF
                // GYNMNASTICS///////////////////////////////////////////////////////////////////////////////////////////

                System.out.println("Current State: " + graspState);
                if (graspState == ArmGraspState.INITIALIZE) {
                    int shoulder_init_value = shoulderServo.GYM_GROUND_PWM;
                    int wrist_init_value = wristServo.COLLECT_PWM;
                    int gripper_init_value = gripperServo.MIN_PWM;

                    gripper = gripper_init_value;
                    wrist = wrist_init_value;
                    shoulder = shoulder_init_value;

                    System.out.println("Gripper Init Value: " + gripper);

                    graspState = ArmGraspState.OPEN_GRIPPER;
                    System.out.println("1 " + wristPWM);

                } else if (graspState == ArmGraspState.OPEN_GRIPPER) {
                    // Opens gripper
                    System.out.println("2 " + gripperPWM);
                    if (!gripperServo.isOpen(gripperPWM)) {
                        gripper = gripperServo.open(gripperPWM);
                    } else {
                        graspState = ArmGraspState.FIND_OBJ;
                    }
                } else if (graspState == ArmGraspState.FIND_OBJ) {
                    if (objDetected) { // Bump sensor
                        System.out.println("Object Detected!");
                        graspState = ArmGraspState.GRASP;
                    }
                } else if (graspState == ArmGraspState.GRASP) {
                    if (!gripperServo.isClosed(gripperPWM)) {
                        System.out.println("closing gripper" + wristPWM
                                + " of " + gripperServo.MIN_PWM);
                        gripper = gripperServo.close(gripperPWM);
                    } else {
                        graspState = ArmGraspState.LIFT;
                    }
                } else if (graspState == ArmGraspState.LIFT) {
                    if (!shoulderServo.isGymUp(shoulderPWM)) {
                        shoulder = shoulderServo.moveGymUp(shoulderPWM);
                    } else {
                        //                        System.out.println("Arm should be at 45 degrees and now switching into move");
                        graspState = ArmGraspState.MOVE;
                        startX = robotX;
                        startY = robotY;
                        startTheta = robotTheta;
                        goalX = robotX + MOVE_DIST*Math.cos(robotTheta);
                        goalY = robotY + MOVE_DIST*Math.sin(robotTheta);
                        goalTheta = robotTheta;
                    }
                } else if (graspState == ArmGraspState.MOVE) {
                    double remDist = getDist(robotX, robotY, goalX, goalY);

                    if (remDist > DIST_TOL) {
                        MotionMsg moveMsg = new MotionMsg();
                        moveMsg.translationalVelocity = Math.min(FWD_GAIN * remDist, 0.25);
                        moveMsg.rotationalVelocity = 0.0;
                        System.out.println("Rem Dist: " + remDist);

                        System.out.println("Trans Vel: " + moveMsg.translationalVelocity);
                        motionPub.publish(moveMsg);
                    } else {
                        MotionMsg moveMsg = new MotionMsg();
                        moveMsg.translationalVelocity = 0.0;
                        moveMsg.rotationalVelocity = 0.0;                        
                        motionPub.publish(moveMsg);
                        graspState = ArmGraspState.DEPOSIT_WRIST;
                    }
                } else if (graspState == ArmGraspState.DEPOSIT_WRIST) {
                    if (!wristServo.isCollect(wristPWM)) {
                        wrist = wristServo.collect(wristPWM);
                    } else {
                        graspState = ArmGraspState.DEPOSIT_SHOULDER;
                    }
                } else if (graspState == ArmGraspState.DEPOSIT_SHOULDER) {
                    if (!shoulderServo.onGround(shoulderPWM)) {
                        shoulder = shoulderServo.moveToGround(shoulderPWM);
                    } else {
                        graspState = ArmGraspState.DEPOSIT_SHOULDER;
                    }
                } else if (graspState == ArmGraspState.DEPOSIT_GRIPPER) {
                    // Opens gripper
                    if (!gripperServo.isOpen(gripperPWM)) {
                        gripper = gripperServo.open(gripperPWM);
                    } else {
                        graspState = ArmGraspState.RETURN;
                        startX = robotX;
                        startY = robotY;
                        startTheta = robotTheta;
                        goalX = robotX - MOVE_DIST*Math.cos(robotTheta);
                        goalY = robotY - MOVE_DIST*Math.sin(robotTheta);
                        goalTheta = robotTheta;
                    }
                } else if (graspState == ArmGraspState.RETURN) {
                    double remDist = getDist(robotX, robotY, goalX, goalY);

                    if (remDist > DIST_TOL) {
                        System.out.println("Rem Dist: " + remDist);
                        MotionMsg moveMsg = new MotionMsg();
                        moveMsg.translationalVelocity = -Math.min(FWD_GAIN * remDist, 0.25);
                        moveMsg.rotationalVelocity = 0.0;

                        System.out.println("Trans Vel: " + moveMsg.translationalVelocity);
                        motionPub.publish(moveMsg);
                    } else {
                        MotionMsg moveMsg = new MotionMsg();
                        moveMsg.translationalVelocity = 0.0;
                        moveMsg.rotationalVelocity = 0.0;                        
                        motionPub.publish(moveMsg);
                        System.out.println("DONE with Grasp and Transport");
                    }
                } 

                sendCommands();
            }
        });

        bumpersSub.addMessageListener(new MessageListener<BumpMsg>() {
            @Override
            public void onNewMessage(BumpMsg msg) {
                // System.out.println("msg.left state: " + msg.left);
                // System.out.println("msg.right state: " + msg.right);
                // System.out.println("msg.gripper state: " + msg.gripper);
                objDetected = msg.gripper;

                if (objDetected) {
                    System.out.println("objDetected in bumper sub");
                }

            }
        });

        odometrySub.addMessageListener(new MessageListener<OdometryMsg>() {
            @Override
            public void onNewMessage(OdometryMsg msg) {
                robotX = msg.x;
                robotY = msg.y;
                robotTheta = msg.theta;
            }
        });
    }

    /*
     * protected void initializeServos(int shoulder_value, int wrist_value, int
     * gripper_value) { ArmMsg msg = new ArmMsg(); msg.pwms[0] = shoulder_value;
     * msg.pwms[1] = wrist_value; msg.pwms[2] = gripper_value;
     * armPWMPub.publish(msg); }
     */

    /**
     * Moves the arm to the desired x, z position in the robot frame
     * 
     * @param desX
     *            the desired x coordinate for the end effector
     * @param desZ
     *            the desired z coordinate for the end effector
     */
    public void moveArm(double desX, double desZ, int currShoulderPWM,
            int currWristPWM) {

        double[] angles = InverseKinematics.getThetaPhi(desX, desZ,
                shoulderServo.getThetaRad(currShoulderPWM),
                wristServo.getThetaRad(currWristPWM));

        int desShoulderPWM = shoulderServo.getPWM(angles[0]);
        int desWristPWM = wristServo.getPWM(angles[1]);

        ArmMsg msg = new ArmMsg();
        msg.pwms[0] = shoulderServo.getSafePWM(currShoulderPWM, desShoulderPWM);
        msg.pwms[1] = wristServo.getSafePWM(currWristPWM, desWristPWM);
        armPWMPub.publish(msg);
    }

    /**
     * Rotates all of the servos concurrently (the handle(ArmMsg msg) method)
     */
    private void rotateAllServos(int shoulderPWM, int wristPWM, int gripperPWM) {

        int SHIFT_AMOUNT = 100;
        if (currState == State.UP) {
            // If all servos are at the UP state
            if (shoulderServo.atMax(shoulderPWM) && wristServo.atMax(wristPWM)
                    && gripperServo.isClosed(gripperPWM)) {
                System.out.println("Switching to Down");
                currState = State.DOWN;
            } else {
                ArmMsg msg = new ArmMsg();

                // msg.pwms[0] = Math.min(shoulderPWM + (shoulderServo.MAX_PWM -
                // shoulderServo.MIN_PWM)/SHIFT_AMOUNT, shoulderServo.MAX_PWM);
                // msg.pwms[1] = Math.min(wristPWM + (wristServo.MAX_PWM -
                // wristServo.MIN_PWM)/SHIFT_AMOUNT, wristServo.MAX_PWM);
                // msg.pwms[2] = Math.min(gripperPWM + (gripperServo.MAX_PWM -
                // gripperServo.MIN_PWM)/SHIFT_AMOUNT, gripperServo.MAX_PWM);

                // System.out.println("Shoulder Theta: " +
                // shoulderServo.getThetaDeg(msg.pwms[0]));
                // System.out.println("Wrist Theta: " +
                // wristServo.getThetaDeg(msg.pwms[1]));

                msg.pwms[0] = shoulderServo.fullRotation(shoulderPWM, true);
                msg.pwms[1] = wristServo.fullRotation(wristPWM, true);
                msg.pwms[2] = gripperServo.fullRotation(gripperPWM, true);
                // System.out.println("Shoulder PWM: " + msg.pwms[0]);
                armPWMPub.publish(msg);
            }
        } else if (currState == State.DOWN) {

            // If all servos are at the DOWN state
            if (shoulderServo.atMin(shoulderPWM) && wristServo.atMin(wristPWM)
                    && gripperServo.isOpen(gripperPWM)) {
                System.out.println("Switching to Up");
                currState = State.UP;
            } else {
                ArmMsg msg = new ArmMsg();
                // msg.pwms[0] = Math.max(shoulderPWM - (shoulderServo.MAX_PWM -
                // shoulderServo.MIN_PWM)/SHIFT_AMOUNT, shoulderServo.MIN_PWM);
                // msg.pwms[1] = Math.max(wristPWM - (wristServo.MAX_PWM -
                // wristServo.MIN_PWM)/SHIFT_AMOUNT, wristServo.MIN_PWM);
                // msg.pwms[2] = Math.max(gripperPWM - (gripperServo.MAX_PWM -
                // gripperServo.MIN_PWM)/SHIFT_AMOUNT, gripperServo.MIN_PWM);
                msg.pwms[0] = shoulderServo.fullRotation(shoulderPWM, false);
                msg.pwms[1] = wristServo.fullRotation(wristPWM, false);
                msg.pwms[2] = gripperServo.fullRotation(gripperPWM, false);

                // System.out.println("Shoulder Theta: " +
                // shoulderServo.getThetaDeg(msg.pwms[0]));
                // System. System.out.println("2 " + wristPWM);
                // out.println("Wrist Theta: " +
                // wristServo.getThetaDeg(msg.pwms[1]));
                armPWMPub.publish(msg);
            }
        }
    }

    /**
     * Writes the provided PWM value for the shoulder servo
     * 
     * @param value
     *            the PWM value to write to the shoulder servo
     */
    /*
     * private void writeShoulderPWM(int value) { ArmMsg msg = new ArmMsg();
     * msg.pwms[0] = value; armPWMPub.publish(msg); }
     */

    /**
     * Writes the provided PWM value for the wrist servo
     * 
     * @param value
     *            the PWM value to write to the wrist servo
     */
    /*
     * private void writeWristPWM(int value) { ArmMsg msg = new ArmMsg();
     * msg.pwms[1] = value; armPWMPub.publish(msg); }
     */

    /**
     * Writes the provided PWM value for the gripper servo
     * 
     * @param value
     *            the PWM value to write to the gripper servo
     */
    /*
     * private void writeGripperPWM(int value) { ArmMsg msg = new ArmMsg();
     * msg.pwms[2] = value; armPWMPub.publish(msg); }
     */

    /**
     * Returns the distance between two points
     * 
     * @param pt1X
     *            the x coordinate of point 1
     * 
     * @param pt1Y
     *            the y coordinate of point 1
     * 
     * @param pt2X
     *            the x coordinate of point 2
     * 
     * @param pt2Y
     *            the y coordinate of point 2
     */
    public double getDist(double pt1X, double pt1Y, double pt2X, double pt2Y) {
        return Math.sqrt((pt1X - pt2X) * (pt1X - pt2X) + (pt1Y - pt2Y)
                * (pt1Y - pt2Y));
    }

    @Override
    public void onShutdown(Node node) {
        if (node != null) {
            node.shutdown();
        }
    }

    @Override
    public void onShutdownComplete(Node node) {
    }

    @Override
    public GraphName getDefaultNodeName() {
        return new GraphName("rss/grasping");
    }
}