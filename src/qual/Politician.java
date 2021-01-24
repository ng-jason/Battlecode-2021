package qual;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class Politician extends Robot {

    static final double polPassFactor = 1;


    public Politician(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        super.run();
        // from example player
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        RobotInfo[] attackable = rc.senseNearbyRobots(actionRadius, enemy);
        if (attackable.length != 0 && rc.canEmpower(actionRadius)) {
            System.out.println("empowering...");
            rc.empower(actionRadius);
            System.out.println("empowered");
        }
    }
}
