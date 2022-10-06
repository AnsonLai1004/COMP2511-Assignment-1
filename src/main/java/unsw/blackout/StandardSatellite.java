package unsw.blackout;

import unsw.utils.Angle;

import java.util.ArrayList;
import java.util.List;
import unsw.blackout.FileTransferException.*;

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
    public double getSendBandwidth() {
        return sendBandwidth;
    }

    @Override
    public double getReceiveBandwidth() {
        return receiveBandwidth;
    }

    @Override
    public void satelliteReceiveFile(File file) throws FileTransferException {
        List<File> files = super.getFiles();
        int count = file.getSize();
        if (files.size() + 1 > 3) {
            throw new VirtualFileNoStorageSpaceException("Max Files Reached");
        }
        for (File f : files) {
            count += f.getSize();
        }
        if (count > 80) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
        super.satelliteReceiveFile(file);
    }
}
