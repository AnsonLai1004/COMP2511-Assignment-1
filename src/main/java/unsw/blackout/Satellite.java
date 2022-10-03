package unsw.blackout;
import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public abstract class Satellite {
    private String satelliteId;
    private String type;
    private Angle position;
    private double height;
    private ArrayList<File> files;

    public Satellite(String satelliteId, String type, Angle position, double height) {
        this.satelliteId = satelliteId;
        this.type = type;
        this.position = position;
        this.height = height;
        this.files = new ArrayList<File>();
    }

    public String getSatelliteId() {
        return satelliteId;
    }

    public void setSatelliteId(String satelliteId) {
        this.satelliteId = satelliteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public abstract void updatePosition();

    public abstract double getRange();

    public List<String> updateCommunicables(List<Satellite> satellites, List<Device> devices) {
        List<String> communicables = new ArrayList<String>();
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() != satelliteId) {
                double satDistance = MathsHelper.getDistance(sat.getHeight(), sat.getPosition(), height, position);
                boolean satVisible = MathsHelper.isVisible(sat.getHeight(), sat.getPosition(), height, position);
                if (satDistance <= getRange() && satVisible) {
                    communicables.add(sat.getSatelliteId());
                }
            }
        }
        for (Device device : devices) {
            double devDistance = MathsHelper.getDistance(height, position, device.getPosition());
            boolean devVisible = MathsHelper.isVisible(height, position, device.getPosition());
            if (devDistance <= getRange() && devVisible) {
                communicables.add(device.getDeviceId());
            }
        }
        return communicables;
    }
}
// distances are in kilometres (1km = 1000m)
// Angular velocity is in radians per min
// Linear velocity is in kilometres per min
