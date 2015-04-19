package StateMachine;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import Challenge.ConstructionObject;
import Challenge.GrandChallengeMap;
import MotionPlanning.CSpace;
import MotionPlanning.GoalAdjLists;
import MotionPlanning.MultiRRT;
import MotionPlanning.PolygonObstacle;
import MotionPlanning.RRT;
import StateMachine.FSM.msgENUM;
import StateMachine.FSM.stateENUM;

/**
 * This state initializes the robot to the starting positions engaging all motors and servos
 */
public class Initialize implements FSMState {


	private FSM fsm;	

	public Initialize(FSM stateMachine)
	{
		fsm = stateMachine;

		GrandChallengeMap challengeMap;
		try {
			challengeMap = GrandChallengeMap.parseFile(fsm.mapFileName);
			CSpace cSpace = new CSpace(); 
			ArrayList<ArrayList<PolygonObstacle>> obsCSpaces = cSpace.generateCSpace(challengeMap, false);			//this was adding the robot as an OBSTACLE!!! was true TODO
			challengeMap.set3DCSpace(obsCSpaces);

			ArrayList<Point2D.Double> objectLocations = new ArrayList<Point2D.Double>();
			for (ConstructionObject cs : challengeMap.getConstructionObjects())
			{objectLocations.add(cs.getPosition());}

			Point2D.Double start = challengeMap.getRobotStart();
			Point2D.Double end = challengeMap.getRobotGoal();

			fsm.RRTengine =  new MultiRRT(challengeMap);
			fsm.foundPaths = new GoalAdjLists(end);


			//			fsm.RRTengine.getPaths(start, goals, fsm.RRT_TOLERANCE);
			//			fsm.foundPaths.addBiPath(from, to, path, dist);
		} catch (IOException | ParseException e) {
			System.err.println("Unable to load map.");
			e.printStackTrace();
		}
	}	


	public stateENUM getName()
	{return stateENUM.INITIALIZE;}


	public boolean accepts(msgENUM msgType)
	{
		if (msgType == msgENUM.WHEELS) return true;
		return false;
	}


	public void update(Object msg)
	{
		//do stuff

		//if condition to leave state
		//fsm.updateState(new NextState(fsm));

	}
}