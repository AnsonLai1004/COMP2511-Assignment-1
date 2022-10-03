package unsw.blackout;

import java.util.ArrayList;
import java.util.List;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public abstract class Device {
    private String deviceId;
    private String type;
    private Angle position;
    private ArrayList<File> files;

    public Device(String deviceId, String type, Angle position) {
        this.deviceId = deviceId;
        this.type = type;
        this.position = position;
        this.files = new ArrayList<File>();
    }
    public ArrayList<File> getFileList() {
        return files;
    }
    public void addFile(File file) {
        files.add(file);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public abstract double getRange();

    public List<String> updateCommunicables(List<Satellite> satellites) {
        List<String> communicables = new ArrayList<String>();
        for (Satellite sat : satellites) {
            double distance = MathsHelper.getDistance(sat.getHeight(), sat.getPosition(), getPosition());
            boolean visible = MathsHelper.isVisible(sat.getHeight(), sat.getPosition(), getPosition());
            if (distance <= getRange() && visible) {
                communicables.add(sat.getSatelliteId());
            }
        }
        return communicables;
    }

}
