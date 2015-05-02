package StateMachine;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import Challenge.ConstructionObject;
import Challenge.GrandChallengeMap;
import Localization.ParticleFilter;
import MotionPlanning.CSpace2D;
import MotionPlanning.CSpace3D;
import MotionPlanning.GoalAdjLists;
import MotionPlanning.MultiRRT2D;
import MotionPlanning.MultiRRT3D;
//import MotionPlanning.MultiRRT3D;
import MotionPlanning.PolygonObstacle;
import MotionPlanning.RRTreeNode;
import StateMachine.FSM.msgENUM;
import StateMachine.FSM.stateENUM;

/**
 * This state initializes the robot to the starting positions engaging all
 * motors and servos
 */
public class Initialize implements FSMState {

    private FSM fsm;
    private boolean initialized;

    public Initialize(FSM stateMachine) {
        fsm = stateMachine;

        try {
            GrandChallengeMap challengeMap = null;
            try {
                challengeMap = GrandChallengeMap.parseFile(fsm.mapFileName);
            } catch (Exception e) {
                System.err.println("Unable to load map.");
                e.printStackTrace();
            }

            CSpace2D cSpace = new CSpace2D();
            ArrayList<PolygonObstacle> cSpaces = cSpace.generateCSpace(
                    challengeMap, false);
            challengeMap.cSpace = cSpaces;
            fsm.mapDrawer.displayMap(challengeMap);

            fsm.mapDrawer.displayMapCSpace(cSpaces);

            //			Initialize the particle filter
            Double robotStartPos = challengeMap.getRobotStart();

            fsm.particleFilter = new ParticleFilter(robotStartPos.x, robotStartPos.y, 0.0, 
                    fsm.PARTICLE_FILTER_RADIUS, fsm.NUM_PARTICLES, challengeMap, fsm.TRANS_NOISE, 
                    fsm.ROT_NOISE, fsm.SENSOR_NOISE);

            fsm.prevPt = robotStartPos;

            ArrayList<Point2D.Double> objectLocations = new ArrayList<Point2D.Double>();
            for (ConstructionObject cobj : challengeMap
                    .getConstructionObjects()) {
                boolean unreachable = false;
                Point2D.Double loc = cobj.getPosition();

                for (PolygonObstacle obs : cSpaces) {
                    if (obs.contains(loc)) {
                        unreachable = true;
                        break;
                    }
                }

                if (!unreachable) {
                    objectLocations.add(loc);
                }
            }

            /*
             * CSpace3D cSpace3D = new CSpace3D();
             * ArrayList<ArrayList<PolygonObstacle>> obsCSpaces =
             * cSpace3D.generateCSpace(challengeMap, false);
             * challengeMap.set3DCSpace(obsCSpaces);
             * fsm.mapDrawer.displayMap(challengeMap);
             * 
             * fsm.mapDrawer.displayMapCSpace(obsCSpaces.get(90));
             * 
             * // 2D CSpace for checking if can reach obstacles CSpace2D
             * cSpace2D = new CSpace2D(); ArrayList<PolygonObstacle>
             * obs2DCSpaces = cSpace2D.generateCSpace(challengeMap, false);
             * 
             * ArrayList<Point2D.Double> objectLocations = new
             * ArrayList<Point2D.Double>(); Point2D.Double start =
             * challengeMap.getRobotStart(); Point2D.Double end =
             * challengeMap.getRobotGoal();
             * 
             * for (ConstructionObject cobj :
             * challengeMap.getConstructionObjects()){ boolean unreachable =
             * false; Point2D.Double loc = cobj.getPosition();
             * 
             * // System.out.println(obsCSpaces.get(0).size());
             * 
             * for (PolygonObstacle obs : obs2DCSpaces) { //TODO only the
             * 0degree now obs2DCSpaces if
             * (obs.contains(loc)||loc.equals(start)){ unreachable = true;
             * break; } }
             * 
             * 
             * if (!unreachable) objectLocations.add(loc);
             * 
             * 
             * }
             */

            System.out.println("Num obs: " + objectLocations.size());

            fsm.mapDrawer.displayCObj(challengeMap.getConstructionObjects());
            Point2D.Double start = challengeMap.getRobotStart();
            Point2D.Double end = challengeMap.getRobotGoal();

            fsm.currentLocation = start;
            objectLocations.add(end);

            fsm.RRTengine = new MultiRRT2D(challengeMap);
            fsm.foundPaths = new GoalAdjLists(end);

            Point2D.Double currLocation = start;
            while (objectLocations.size() > 0) {
                System.out.println("starting loc " + currLocation);
                System.out.println("Printing locs");
                for (Point2D.Double locs : objectLocations) {
                    System.out.println(locs);
                }
                RRTreeNode[] pathEnds = fsm.RRTengine.getPaths(currLocation,
                        objectLocations, fsm.RRT_TOLERANCE);

                // Prints out the path to the MapGUI
                for (RRTreeNode r : pathEnds) {
                    if (r == null)
                        continue;
                    fsm.mapDrawer.outputPath(r.pathFromParent());
                }

                for (int i = 0; i < pathEnds.length; i++) {
                    if (pathEnds[i] == null) {
                        objectLocations.remove(i); // if path was not found,
                        // remove it from possible
                        // locations
                        continue;
                    }
                    double dist = pathEnds[i].distFromRoot;
                    ArrayList<Point2D.Double> path = pathEnds[i]
                            .pathFromParent();
                    fsm.foundPaths.addBiPath(currLocation,
                            objectLocations.get(i), path, dist);
                }

                currLocation = objectLocations.remove(0);
            }
            initialized = true;

        } catch (Exception e) {
            System.err.println("Error in Initialize.");
            e.printStackTrace();
        }

    }

    public stateENUM getName() {
        return stateENUM.INITIALIZE;
    }

    public boolean accepts(msgENUM msgType) {
        // if (msgType == msgENUM.WHEELS) return true;
        if (msgType == msgENUM.SERVO) {
            return true;
        }
        if (msgType == null && initialized) {
            return true;
        }
        return false;
    }

    public void update(GenericMessage msg) {
        // do stuff
        System.out.println("Hi. I'm a robot.");
        System.out.println("My current state is: Initialize.");


        // get gate PWM value from arm message
        ArmMsg message = (ArmMsg)msg.message;
        int gatePWM = (int) message.pwms[1]; // convert from long to int

        // if gate is not closed, close gate
        if (!fsm.gateServo.isClosed(gatePWM))
        {
            int[] messagePWMs = new int[3];
            messagePWMs[0] = (int) message.pwms[0]; // convert from long to int
            messagePWMs[1] = (int) message.pwms[1]; // convert from long to int
            messagePWMs[2] = (int) message.pwms[2]; // convert from long to int
            fsm.gateServo.close(messagePWMs);
          //  System.out.println("CurrPWM: "+gatePWM);
            System.out.println("Trying to close gate.");
        }
        else //if condition to leave state
        {
            System.out.println("Done closing gate.");
            //fsm.updateState(new MoveForward(fsm));
        }


        // if condition to leave state
        System.out.println("Initialization complete. TIME TO GO COLLECT SOME BLOCKS!");
        System.out.println("ARE YOU EXCITED?");
        System.out.println("I'M EXCITED!!!");
        fsm.updateState(new WaypointNavClose(fsm));
    }

    @Override
    public void onStart() {
    }
}
