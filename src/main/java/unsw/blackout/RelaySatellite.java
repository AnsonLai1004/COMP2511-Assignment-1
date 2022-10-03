package unsw.blackout;
import unsw.utils.Angle;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;


import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;
import static unsw.utils.MathsHelper.CLOCKWISE;

public class RelaySatellite extends Satellite {
    private double linearVelocity = 1500;
    private double range = 300000;
    private int direction = CLOCKWISE;
    public RelaySatellite(String satelliteId, String type, Angle position, double height) {
        super(satelliteId, type, position, height);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void updatePosition() {
        Angle currPos = super.getPosition();
        Angle change = Angle.fromRadians(linearVelocity / RADIUS_OF_JUPITER);
        if (currPos.toDegrees() <= 140 || currPos.toDegrees() >= 345) {
            super.setPosition(currPos.add(change));
            direction = CLOCKWISE;
        } else if (currPos.toDegrees() >= 190 && currPos.toDegrees() < 345) {
            super.setPosition(currPos.subtract(change));
            direction = ANTI_CLOCKWISE;
        } else {
            // within range 140 - 190
            if (direction == CLOCKWISE) {
                super.setPosition(currPos.add(change));
            } else {
                super.setPosition(currPos.subtract(change));
            }
        }
    }

    @Override
    public double getRange() {
        return range;
    }
}
