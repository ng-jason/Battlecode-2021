package sprint;

import battlecode.common.*;
public abstract class Robot {
    // from player/RobotPlayer
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

    static int turnCount;

    // using https://github.com/mvpatel2000/Battlecode2020
    RobotController rc;
    int myId;
    MapLocation myLocation;

//    Team enemyTeam;



    public Robot(RobotController robotController) throws GameActionException {
        rc = robotController;
        myId = rc.getID();
    }

    public void run() throws GameActionException {
        myLocation = rc.getLocation();
    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMove(Direction dir) throws GameActionException {
        System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }
}
