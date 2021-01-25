package qual;

import battlecode.common.*;

public class Slanderer extends Robot {

    public Slanderer(RobotController rc) throws GameActionException {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        // super.run(); // put this code back in once there's a way for EC flags to communicate to specific units
        spread(); // currently runs from all robots; could make it so that it tolerates politicians of the same team or other bots
    }

    public void spread() throws GameActionException {
        RobotInfo[] infos = rc.senseNearbyRobots();
        if (infos.length == 0) {
            if (tryMove(randomDirection()))
                System.out.println("I moved!");
        } else {
            double averageX = 0, averageY = 0;
            for (RobotInfo info : infos) {
                averageX += info.getLocation().x;
                averageY += info.getLocation().y;
            }
            averageX /= infos.length;
            averageX -= rc.getLocation().x;
            averageY /= infos.length;
            averageY -= rc.getLocation().y;
            double angle; // angle in radians clockwise with respect to North
            if (averageX == 0 && averageY != 0) {
                angle = averageY > 0 ? 0 : Math.PI;
            } else {
                angle = Math.atan(averageX / averageY);
                if (averageY > 0) {
                    angle += Math.PI;
                }
                if (angle < 0) {
                    angle += 2 * Math.PI;
                }
            }
            int angleEnum = (int) Math.round(angle / (Math.PI / 4)) % 8;
            Direction newDirection = Direction.values()[angleEnum];
            if (tryMove(newDirection))
                System.out.println("I moved!");
        }
    }
}
