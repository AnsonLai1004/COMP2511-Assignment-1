package unsw.blackout;

import unsw.utils.Angle;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;
import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;

public class TeleportingSatellite extends Satellite {
    private double linearVelocity = 1000;
    private double range = 200000;
    private int direction = ANTI_CLOCKWISE;
    public TeleportingSatellite(String satelliteid, String type, Angle position, double height) {
        super(satelliteid, type, position, height);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void updatePosition() {
        double currPos = super.getPosition().toDegrees();
        double change = Angle.fromRadians(linearVelocity / RADIUS_OF_JUPITER).toDegrees();
        double newPos = currPos + (direction * change);
        if ((currPos < 180 && newPos >= 180) || (currPos > 180 && newPos <= 180)) {
            super.setPosition(Angle.fromDegrees(0));
            direction = direction * -1;
        } else if ((currPos < 360 && newPos >= 360) || (currPos > 0 && newPos <= 0)) {
            super.setPosition(Angle.fromDegrees(180));
            direction = direction * -1;
        } else {
            super.setPosition(Angle.fromDegrees(newPos));
        }
    }
    @Override
    public double getRange() {
        return range;
    }
}
