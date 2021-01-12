package sprint;

import battlecode.common.*;

public class Slanderer extends Robot {

    public Slanderer(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        if (tryMove(randomDirection()))
            System.out.println("I moved!");
    }
}
