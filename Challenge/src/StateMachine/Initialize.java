package StateMachine;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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

		GrandChallengeMap challengeMap = GrandChallengeMap.parseFile(mapFileName);
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
				

		MultiRRT.RRTreeNode[] pathEnds = fsm.RRTengine.getPaths(start, goals, fsm.RRT_TOLERANCE);
		for (int i=0; i<pathEnds.length; i++)
			{pathEnds.
			fsm.foundPaths.addBiPath(from, to, path, dist);
			}
		}	

	
	public stateENUM getName()
		{return stateENUM.INITIALIZE;}

	
	public boolean accepts(msgENUM msgType)
		{
		//if (msgType == msgENUM.WHEELS) return true;
		if (msgType == null) return true;
		return false;
		}


	public void update(Object msg)
		{
		//do stuff

		//if condition to leave state
		//fsm.updateState(new NextState(fsm));

		}


}