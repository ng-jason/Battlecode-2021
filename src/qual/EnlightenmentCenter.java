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
    static Direction currentSpawnDirection = Direction.NORTH;


    public EnlightenmentCenter(RobotController rc) throws GameActionException {
        super(rc);
        if (rc.getRoundNum() < 1) {
            rc.setFlag(0);
        }
    }

    @Override
    public void run() throws GameActionException {
        // change robot to build depending on game situation or clock yield
        RobotType toBuild = randomSpawnableRobotType();
        int influence = 50;
//        if (target != null) {
//            Direction d = rc.getLocation().directionTo(target);
//        }
        System.out.println("Current spawn dir" + currentSpawnDirection.toString());
        if (rc.canBuildRobot(toBuild, currentSpawnDirection, influence)) {
            rc.buildRobot(toBuild, currentSpawnDirection, influence);
            // add robot IDs to list, not working atm
//                for (RobotInfo robot : rc.senseNearbyRobots(1, rc.getTeam())) {
//                    if (rc.adjacentLocation(dir) == robot.getLocation()) {
//                        robotIDs[robotCount++] = robot.getID();
//                    }
//                }
        }
        currentSpawnDirection = currentSpawnDirection.rotateRight();
//        for (int robotID : robotIDs) {
//            System.out.println(robotID);
//        }
        if (target != null) {
            sendLocation(target);
        } else {
            // get a location target from a random friendly robot

        }
        // sendLocation(target, 1);  // for sending extra info like target type
    }

    /**
     * Returns a random spawnable RobotType
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnableRobotType() {
        return spawnableRobot[(int) (Math.random() * spawnableRobot.length)];
    }
}
