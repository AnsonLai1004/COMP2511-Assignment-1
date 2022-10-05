package unsw.blackout;

import unsw.utils.Angle;

import java.util.ArrayList;
import java.util.List;

public class StandardSatellite extends Satellite {
    private double linearVelocity = 2500;
    private double range = 150000;
    private double sendBandwidth = 1;
    private double receiveBandwidth = 1;

    public StandardSatellite(String satelliteId, String type, Angle position, double height) {
        super(satelliteId, type, position, height);
    }

    @Override
    public void updatePosition() {
        double currPos = super.getPosition().toDegrees();
        double change = Angle.fromRadians(linearVelocity / super.getHeight()).toDegrees();
        currPos -= change;
        super.setPosition(Angle.fromDegrees(currPos));
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

    @Override
    public int hvStorage(File file) {
        List<File> files = super.getFiles();
        int count = file.getSize();
        if (files.size() + 1 > 3) {
            return 1;
        }
        for (File f : files) {
            count += f.getSize();
        }
        if (count > 80) {
            return 2;
        }
        return 0;
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
