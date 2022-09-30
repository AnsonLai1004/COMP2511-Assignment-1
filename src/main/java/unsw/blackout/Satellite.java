package unsw.blackout;
import unsw.utils.Angle;

public abstract class Satellite {
    private String satelliteId;
    private String type;
    private Angle position;
    private double height;

    public Satellite(String satelliteId, String type, Angle position, double height) {
        this.satelliteId = satelliteId;
        this.type = type;
        this.position = position;
        this.height = height;

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

}
// distances are in kilometres (1km = 1000m)
// Angular velocity is in radians per min
// Linear velocity is in kilometres per min
