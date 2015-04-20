package StateMachine;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import StateMachine.FSM.msgENUM;
import StateMachine.FSM.stateENUM;

/**
 * This state completely navigates to the deposit site using the existing path from our current location
 */
public class WaypointNavDeposit implements FSMState {

	private FSM fsm;
	public ArrayList<Point2D.Double> waypoints;
	public int atWaypoint = 0;


	public WaypointNavDeposit(FSM stateMachine)
		{
		fsm = stateMachine;
		
		waypoints = fsm.foundPaths.getPathToGoal(fsm.currentLocation);

		}	

	
	public stateENUM getName()
		{return stateENUM.WNDEPOSIT;}

	
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