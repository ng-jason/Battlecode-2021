package qual;

import battlecode.common.*;

public class EnlightenmentCenter extends Robot {

    static final RobotType[] spawnableRobot = {
            RobotType.POLITICIAN,
            RobotType.SLANDERER,
            RobotType.MUCKRAKER,
    };
    static int robotCount = 0;
    static int[] robotIDs; // IDs of robot can use to see if they are still alive etc using senseID
    static MapLocation[] enlightenmentCenterLocations;
    static Integer targetID;


    public EnlightenmentCenter(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        // change robot to build depending on game situation or clock yield
        RobotType toBuild = randomSpawnableRobotType();
        int influence = 50;
//        if (target != null) {
//            Direction d = rc.getLocation().directionTo(target);
//        }
        senseEnemy();
        for (Direction dir : directions) {
            if (rc.canBuildRobot(toBuild, dir, influence)) {
                rc.buildRobot(toBuild, dir, influence);
                // add robot IDs to list, not working atm
//                for (RobotInfo robot : rc.senseNearbyRobots(1, rc.getTeam())) {
//                    if (rc.adjacentLocation(dir) == robot.getLocation()) {
//                        robotIDs[robotCount++] = robot.getID();
//                    }
//                }
            } else {
                break;
            }
        }
        for (int robotID : robotIDs) {
            System.out.println(robotID);
        }
        if (rc.isReady()) {
            if (target == null) {
                // get location flag from robot if any (but need robot ID)
                //
//                MapLocation target =
            }
            sendLocation(target);
            // sendLocation(target, 1);  // for sending extra info like target type
        }
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnableRobotType() {
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }

    /**
     * Detects if there are any enemy robots within its sensor radius. If there
     * are, then the centre raises a flag telling other robots to target it
     * until it's either out of the radius, destroyed, or converted
     */
    static void senseEnemy() {
        try {
            if (targetID != null && (!rc.canSenseRobot(targetID)
            || rc.senseRobot(targetID).getTeam() == rc.getTeam())) {
                targetID = null;
            }
            if (targetID == null) {
                RobotInfo[] robots = rc.senseNearbyRobots();
                for (RobotInfo robot : robots) {
                    if (robot.getTeam() == rc.getTeam().opponent()) {
                        targetID = robot.getID();
                        sendLocation(robot.getLocation());
                        break;
                    }
                }
                rc.setFlag(0);
            }
        } catch (Exception e) {}
    }
}
