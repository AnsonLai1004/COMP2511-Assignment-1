package unsw.blackout;

import unsw.utils.Angle;
//import unsw.utils.MathsHelper;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.ArrayList;
import java.util.List;

public class StandardSatellite extends Satellite {
    //private final int linearVelocity = 2500;
    // supports handhelds and laptops only (along with other satellites)
    private double linearVelocity = 2500;
    private double range = 150000;
    public StandardSatellite(String satelliteId, String type, Angle position, double height) {
        super(satelliteId, type, position, height);
    }

    @Override
    public void updatePosition() {
        Angle currPos = super.getPosition();
        Angle change = Angle.fromRadians(linearVelocity / RADIUS_OF_JUPITER);
        super.setPosition(currPos.add(change));
    }

    @Override
    public List<String> updateCommunicables(List<Satellite> satellites, List<Device> devices) {
        List<Device> devList = new ArrayList<Device>();
        for (Device dev : devices) {
            if (dev.getType() != "DesktopDevice") {
                devList.add(dev);
            }
        }
        return super.updateCommunicables(satellites, devList);
    }

    @Override
    public double getRange() {
        return range;
    }
}
