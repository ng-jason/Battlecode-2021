package qual;

import battlecode.common.*;

import java.util.Set;

public abstract class Robot {

    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    enum Status {
        NONE,
        EXPLORING,
        HASTARGET
    };

    static RobotController rc;
    static int myId;
    static MapLocation myLocation;
    static MapLocation target = null;
    static int enlightenmentCenterId = -1;
    static Direction targetDirection = null;
//    static int turnCount;
//    Team enemyTeam;
//    static Set<RobotInfo> nearbyFriendlyRobots;
    static Status status = Status.NONE;

    public Robot(RobotController robotController) throws GameActionException {
        rc = robotController;
        myId = rc.getID();
        myLocation = rc.getLocation();
//        getNearbyFriendlyRobots(); NOT WORKING RN
    }

    /**
     * General code for how robot will run
     * Specific robot actions
     * @throws GameActionException
     */
    public void run() throws GameActionException {
        // get enlightenment center ID here
        if (enlightenmentCenterId == -1) {
            for (RobotInfo robot : rc.senseNearbyRobots(-1, rc.getTeam())) {
//            for (RobotInfo robot : nearbyFriendlyRobots) {
                if (robot.type == RobotType.ENLIGHTENMENT_CENTER) {
                    enlightenmentCenterId = robot.ID;
                }
            }
        }
        // further state checks depending on STATUS

        // move to target
        // sets status flag if no status or no target
        if (status == Status.NONE || target == null) {  // no target, get target from EC flag
            System.out.println("Status is NONE");
            if (rc.canGetFlag(enlightenmentCenterId)) { // you can always get flag, just need to discern what we are getting
                if (rc.getFlag(enlightenmentCenterId) != 0) {
                    System.out.println("Getting target from EC");
                    target = getLocationFromFlag(rc.getFlag(enlightenmentCenterId));
                    status = Status.HASTARGET;
                } else {
                    System.out.println("No target. Exploring!");
                    // get direction towards EC that created us, then go in direction opposite to that direction
                    RobotInfo ec = rc.senseRobot(enlightenmentCenterId);
                    Direction directionAwayFromEC = rc.getLocation().directionTo(ec.getLocation()).opposite();
                    int targetX = rc.getLocation().x + (directionAwayFromEC.dx * 64);
                    int targetY = rc.getLocation().y + (directionAwayFromEC.dy * 64);
                    target = new MapLocation(targetX, targetY);
                    status = Status.EXPLORING;
                }
            }

        } else if (status == Status.HASTARGET || status == Status.EXPLORING) {  // we already have a target, keep going towards it
            if (!rc.canDetectLocation(target)) {
                // target not in range so move towards it
                System.out.println("I have a target. Moving towards target");
                basicBug(target);
            }
        }
        System.out.println("Moving towards direction: " + rc.getLocation().directionTo(target));
        // if target is in range, we can make more specific code in their respective files
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

//    /**
//     * Attempts to move in a given direction.
//     *
//     * @param dir The intended direction of movement
//     * @return true if a move was performed
//     * @throws GameActionException
//     */
//    boolean tryMove(Direction dir) throws GameActionException {
//        System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
//        if (rc.canMove(dir)) {
//            rc.move(dir);
//            return true;
//        } else return false;
//    }


    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    // COMMUNICATION

    static final int NBITS = 7;
    static final int BITMASK = (1 << NBITS) - 1;

    static void sendLocation(MapLocation location) throws GameActionException {
        int x = location.x, y = location.y;
        int encodedLocation = ((x & BITMASK) << NBITS) + (y & BITMASK);
        if (rc.canSetFlag(encodedLocation)) {
            rc.setFlag(encodedLocation);
        }
    }

    @SuppressWarnings("unused")
    static void sendLocation(MapLocation location, int extraInformation) throws GameActionException {
        int x = location.x, y = location.y;
        int encodedLocation = (extraInformation << (2*NBITS)) + ((x & BITMASK) << NBITS) + (y & BITMASK);
        if (rc.canSetFlag(encodedLocation)) {
            rc.setFlag(encodedLocation);
        }
    }

    static MapLocation getLocationFromFlag(int flag) {
        int y = flag & BITMASK;
        int x = (flag >> NBITS) & BITMASK;
        // int extraInformation = flag >> (2*NBITS);  EXTRA INFO could be unit type

        MapLocation currentLocation = rc.getLocation();
        int offsetX128 = currentLocation.x >> NBITS;
        int offsetY128 = currentLocation.y >> NBITS;
        MapLocation actualLocation = new MapLocation((offsetX128 << NBITS) + x, (offsetY128 << NBITS) + y);

        // You can probably code this in a neater way, but it works
        MapLocation alternative = actualLocation.translate(-(1 << NBITS), 0);
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(1 << NBITS, 0);
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, -(1 << NBITS));
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        alternative = actualLocation.translate(0, 1 << NBITS);
        if (rc.getLocation().distanceSquaredTo(alternative) < rc.getLocation().distanceSquaredTo(actualLocation)) {
            actualLocation = alternative;
        }
        return actualLocation;
    }


    ////////////////////////////////////////////////////////////////////////////
    // BASIC BUG - just follow the obstacle while it's in the way
    //             not the best bug, but works for "simple" obstacles
    //             for better bugs, think about Bug 2!

    static final double passabilityThreshold = 0.7; // make passability variable depending on surroundings
    static Direction bugDirection = null;

    static void basicBug(MapLocation target) throws GameActionException {
        Direction d = rc.getLocation().directionTo(target);
        if (rc.isReady()) {
            if (rc.canMove(d) && rc.sensePassability(rc.getLocation().add(d)) >= passabilityThreshold) {
                rc.move(d);
                bugDirection = null;
            } else {
                if (bugDirection == null) {
                    bugDirection = d;
                }
                for (int i = 0; i < 8; ++i) {
                    if (rc.canMove(bugDirection) && rc.sensePassability(rc.getLocation().add(bugDirection)) >= passabilityThreshold) {
                        rc.setIndicatorDot(rc.getLocation().add(bugDirection), 0, 255, 255);
                        rc.move(bugDirection);
                        bugDirection = bugDirection.rotateLeft();
                        break;
                    }
                    rc.setIndicatorDot(rc.getLocation().add(bugDirection), 255, 0, 0);
                    bugDirection = bugDirection.rotateRight();
                }
            }
        }
    }

//    // doesn't work rn
//    static void getNearbyFriendlyRobots() {
//        RobotInfo[] robotsInRadius = rc.senseNearbyRobots(-1, rc.getTeam());
//        for (RobotInfo robot : robotsInRadius) {
//            System.out.println("Printing friendly robot ID" + robot.getID());
//            nearbyFriendlyRobots.add(robot);
//        }
//    }
}
