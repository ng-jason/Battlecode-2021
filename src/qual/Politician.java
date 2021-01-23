package qual;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Politician extends Robot {

    static final double polPassFactor = 1;


    public Politician(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        if (Math.random() < polPassFactor * rc.sensePassability(rc.getLocation())) {
            if (rc.senseNearbyRobots().length * 2 >= rc.getConviction()
                    && rc.canEmpower(actionRadius)) {
                System.out.println("empowering...");
                rc.empower(actionRadius);
                System.out.println("empowered");
                return;
            }
        }
        if (tryMove(randomDirection()))
            System.out.println("I moved!");
    }
}
