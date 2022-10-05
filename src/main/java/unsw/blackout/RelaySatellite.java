package unsw.blackout;
import unsw.utils.Angle;
//import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;


import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;
import static unsw.utils.MathsHelper.CLOCKWISE;

public class RelaySatellite extends Satellite {
    private double linearVelocity = 1500;
    private double range = 300000;
    private int direction = CLOCKWISE;
    private double sendBandwidth = 0;
    private double receiveBandwidth = 0;
    public RelaySatellite(String satelliteId, String type, Angle position, double height) {
        super(satelliteId, type, position, height);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void updatePosition() {
        double currPos = super.getPosition().toDegrees();
        double change = Angle.fromRadians(linearVelocity / super.getHeight()).toDegrees();
        if (currPos <= 140 || currPos >= 345) {
            currPos +=  change;
            super.setPosition(Angle.fromDegrees(currPos));
            direction = ANTI_CLOCKWISE;
        } else if (currPos >= 190 && currPos < 345) {
            currPos -= change;
            super.setPosition(Angle.fromDegrees(currPos));
            direction = CLOCKWISE;
        } else {
            // within range 140 - 190
            if (direction == CLOCKWISE) {
                currPos -= change;
                super.setPosition(Angle.fromDegrees(currPos));
            } else {
                currPos += change;
                super.setPosition(Angle.fromDegrees(currPos));
            }
        }
    }

    @Override
    public double getRange() {
        return range;
    }

    @Override
    public int hvStorage(File file) {
        return 1;
    }

    @Override
    public double getSendBandwidth() {
        return sendBandwidth;
    }

    @Override
    public double getReceiveBandwidth() {
        return receiveBandwidth;
    }
}
