package unsw.blackout;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    //private final int linearVelocity = 2500;
    // supports handhelds and laptops only (along with other satellites)
    private int linearVelocity;
    private int range;
    public StandardSatellite(String satelliteId, String type, Angle position, double height) {
        super(satelliteId, type, position, height);
        this.linearVelocity = 2500;
        this.range = 150000;
    }
}
