package unsw.blackout;


import unsw.utils.Angle;

public class LaptopDevice extends Device {
    private int range = 100000;
    public LaptopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position);
    }

    @Override
    public double getRange() {
        return range;
    }

}
