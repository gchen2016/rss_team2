package StateMachine;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Challenge.Fiducial;
import MotionPlanning.RRT;
import Servoing.ServoController;
import StateMachine.FSM.msgENUM;
import StateMachine.FSM.stateENUM;
import VisualServo.FiducialObject;
import VisualServo.Image;
import VisualServo.MultipleBlobTracking;
import VisualServo.client;

import org.ros.message.rss_msgs.*;
import org.ros.message.lab5_msgs.*;
import org.ros.message.lab6_msgs.*;
import org.ros.message.Challenge_msgs.*;

/**
 * This state turns the neck all around taking snapshots of the world at
 * different angles
 */
public class NeckScan implements FSMState {

	// TODO: make servos rotate

	private FSM fsm;
	private double neckAngleTarget = 0;
	private final int NECKSTATES = 6;
	protected MultipleBlobTracking blobTrack = null;
	client cl = null;

	private ArrayList<Point2D.Double> newGoals;

	public NeckScan(FSM stateMachine, MultipleBlobTracking blobTrack_in) {
		fsm = stateMachine;
		newGoals = new ArrayList<Point2D.Double>();
		blobTrack = blobTrack_in;
		cl = new client();

		// init any variables for this state

	}

	public stateENUM getName() {
		return stateENUM.SCAN;
	}

	public boolean accepts(msgENUM msgType) {
		if (msgType == msgENUM.SERVO)
			return true;
		return false;
	}

	public void update(GenericMessage msg) {
		Image src = null;
		float[] depth_array = null;
		List<FiducialObject> detectedFids = new ArrayList<FiducialObject>();

		ArmMsg message = (ArmMsg) msg.message;
		int[] pwms = ServoController.messageConvert((message).pwms);

		if (fsm.neckServo.atAngle(neckAngleTarget, pwms)) { //process image
			System.out.println("Processing image at: "+neckAngleTarget);
			try{
				Thread.sleep(1000); //update fiducial and goal lists 
			}catch(Exception e){
				
			}
			double newTarget = neckAngleTarget + 360/NECKSTATES; 
			if (newTarget < 360){
				neckAngleTarget = newTarget; 
			} else fsm.updateState(new RRTUpdate(fsm, newGoals)); //TODO: insert arraylist of new goalpoint 2ds 
		} else
		{
			fsm.neckServo.goToAngle(neckAngleTarget, pwms);
		}

		//        TODO Most likely want to be running the below in each of the neck servo positions
		//        after done taking all the shots and processing them, want to update the particle filter

		//		Get the fiducials
//		try {
//			src = cl.getImage();
//			depth_array = cl.getDepthImage();
//			detectedFids.addAll(blobTrack.getFiducials(src, depth_array));
//		} catch (Exception e) {
//			System.err.println("Unable to get fiducials");
//			e.printStackTrace();
//		}
//
//
//		//        Determining which fiducials are in the robot's field of view
//		for (FiducialObject fid : detectedFids) {
//
//		}

		//          
		//          
		//          // Particle Filter Measurement Update // TODO: determine which
		//          fiducials are in the fov and get the distances to them // //
		//         Determining which fiducials are in the FOV of the robot //
		//          ArrayList<Integer> measuredFiducials = getFidsInFOV(pt); //
		//          System.out.println("Num of fids in FOV: " +
		//          measuredFiducials.size()); // // Determining the distances to the
		//          fiducials that are in the FOV of the robot // HashMap<Integer,
		//          java.lang.Double> measuredDists = getFidsDists(fsm.prevPoint,
		//          measuredFiducials); // //
		//          fsm.particleFilter.measurementUpdate(measuredFiducials,
		//          measuredDists);
		//          
		//          //TODO: actually update odometery to reflect the updated
		//          localization??
		//          
		//          //if condition to leave state //
		//          System.out.println("Updating state...");
		//          fsm.updateState(new NextState(fsm));

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
	}
}