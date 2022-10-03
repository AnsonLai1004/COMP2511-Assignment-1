package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    private int range = 200000;
    public DesktopDevice(String deviceId, String type, Angle position) {
        super(deviceId, type, position);
    }

    @Override
    public double getRange() {
        return range;
    }

    @Override
    public List<String> updateCommunicables(List<Satellite> satellites) {
        List<Satellite> satList = new ArrayList<Satellite>();
        for (Satellite sat : satellites) {
            if (sat.getType() != "StandardSatellite") {
                satList.add(sat);
            }
        }
        return super.updateCommunicables(satList);
    }

}
