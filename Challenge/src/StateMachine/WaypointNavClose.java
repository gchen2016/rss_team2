package StateMachine;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import StateMachine.FSM.msgENUM;
import StateMachine.FSM.stateENUM;

/**
 * This state chooses the closest reachable here->goal->deposit path and goes starts waypoint navigation to it. 
 * If no such goal exists, it starts navigation directly to the deposit site by yielding to the waypoint nav deposit state
 */
public class WaypointNavClose implements FSMState {


	private FSM fsm;
	public ArrayList<Point2D.Double> waypoints;
	public int atWaypoint = 0;

	public WaypointNavClose(FSM stateMachine)
		{
		fsm = stateMachine;
		
		double maxDist = fsm.ROBOTVEL * (fsm.TIME_LIMIT - (System.currentTimeMillis() - fsm.startTime));
		
		waypoints = fsm.foundPaths.getClosestFeasiblePathFrom(fsm.currentLocation, maxDist);
		
		if (waypoints == null)	//no path to goal point left, go to deposit site
			{
			fsm.updateState(new WaypointNavDeposit(fsm));
			}

		}	

	
	public stateENUM getName()
		{return stateENUM.WNCLOSE;}

	
	public boolean accepts(msgENUM msgType)
		{
		if (msgType == msgENUM.WHEELS) return true;
		return false;
		}


	public void update(Object msg)
		{
		//do waypoint nav stuff

		//if condition to leave state (one waypoint away)
		if (atWaypoint >= waypoints.size()-2)
			{fsm.updateState(new ApproachBlock(fsm));}		//apprach until visual servo

		}


}