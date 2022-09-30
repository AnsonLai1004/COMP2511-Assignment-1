package unsw.blackout;

import unsw.utils.Angle;

public abstract class Device {
    private String deviceId;
    private String type;
    private Angle position;

    public Device(String deviceId, String type, Angle position) {
        this.deviceId = deviceId;
        this.type = type;
        this.position = position;
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
}
