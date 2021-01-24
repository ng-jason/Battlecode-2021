package qual;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class Muckraker extends Robot {

    static final double muckPassFactor = 1.5;


    public Muckraker(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        super.run();
        Team enemy = rc.getTeam().opponent();
        int actionRadius = rc.getType().actionRadiusSquared;
        if (Math.random() < muckPassFactor * rc.sensePassability(rc.getLocation())) {
            for (RobotInfo robot : rc.senseNearbyRobots(actionRadius, enemy)) {
                if (robot.type.canBeExposed()) {
                    // It's a slanderer... go get them!
                    if (rc.canExpose(robot.location)) {
                        System.out.println("e x p o s e d");
                        rc.expose(robot.location);
                        return;
                    }
                }
            }
        }
    }
}
