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
    static Direction currentSpawnDirection = Direction.NORTH;
    static int votesLastRound;
    static int currentTeamVotes;
    static int bidAmount;


    public EnlightenmentCenter(RobotController rc) throws GameActionException {
        super(rc);
        if (rc.getRoundNum() < 1) {
            rc.setFlag(0);
        }
        bidAmount = rc.getInfluence() / 100;
        votesLastRound = 0;
        currentTeamVotes = 0;
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
        // voting algo

        currentTeamVotes = rc.getTeamVotes();
        if (currentTeamVotes > votesLastRound) {
            bidAmount -= rc.getInfluence() / 100;
        } else {
            bidAmount += rc.getInfluence() / 105;
        }
        if (rc.isReady()) {
            rc.bid(bidAmount);
        }
        votesLastRound = currentTeamVotes;
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
                        break;
                    }
                }
                rc.setFlag(0);
            }
            if (targetID != null) {
                sendLocation(rc.senseRobot(targetID).getLocation()); // wastes a lot of bytecode, might optimise later
            }
        } catch (Exception e) {}
    }
}
