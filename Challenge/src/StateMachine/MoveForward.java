package StateMachine;

import StateMachine.FSM.msgENUM;
import StateMachine.FSM.stateENUM;

/**
 * Capture the block after visual servo centers
 */
public class MoveForward implements FSMState {


    private FSM fsm;	
    private int count;
    
    public MoveForward(FSM stateMachine)
    {
        fsm = stateMachine;
        count = 0;

        //init any variables for this state

    }	


    public stateENUM getName()
    {return stateENUM.MOVEFORWARD;}


    public boolean accepts(msgENUM msgType)
    {
        if (msgType == msgENUM.WHEELS) return true;
        return false;
    }


    public void update(GenericMessage msg)
    {
        //do stuff
    	count++;
    	if (count > 1000)         //if condition to leave state
    		{fsm.updateState(new BlockCollected(fsm));}

    }


    @Override
    public void onStart() {
        // TODO Auto-generated method stub

    }


}