package unsw.blackout;

import unsw.utils.Angle;

import java.util.ArrayList;
import java.util.List;

import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;
import unsw.blackout.FileTransferException.*;


public class TeleportingSatellite extends Satellite {
    private double linearVelocity = 1000;
    private double range = 200000;
    private int direction = ANTI_CLOCKWISE;
    private double sendBandwidth = 10;
    private double receiveBandwidth = 15;
    private boolean teleported = false;
    public TeleportingSatellite(String satelliteid, String type, Angle position, double height) {
        super(satelliteid, type, position, height);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void updatePosition() {
        double currPos = super.getPosition().toDegrees();
        double change = Angle.fromRadians(linearVelocity / super.getHeight()).toDegrees();
        double newPos = currPos + (direction * change);
        if ((currPos < 180 && newPos >= 180) || (currPos > 180 && newPos <= 180)) {
            super.setPosition(Angle.fromDegrees(0));
            direction = direction * -1;
            teleported = true;
        } else if ((currPos < 360 && newPos >= 360) || (currPos > 0 && newPos <= 0)) {
            super.setPosition(Angle.fromDegrees(180));
            direction = direction * -1;
            teleported = true;
        } else {
            super.setPosition(Angle.fromDegrees(newPos));
            teleported = false;
        }
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
        for (File f : files) {
            count += f.getSize();
        }
        if (count > 200) {
            throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
        }
        super.satelliteReceiveFile(file);
    }
    public String instantDownload(String fileName) {
        File f = super.getFile(fileName);
        String content = f.getContent();
        String removeT = content.replace("t", "");
        return f.teleportTransfer(removeT);
    }
    @Override
    public ArrayList<String> updateProgress() {
        // update all the files
        ArrayList<String> ids = new ArrayList<String>();
        if (teleported) {
            for (File f : super.getFiles()) {
                if (!f.isTransferCompleted()) {
                    ids.add(instantDownload(f.getFilename()));
                }
            }
        }
        ids.addAll(super.updateProgress());
        return ids;
    }

}
