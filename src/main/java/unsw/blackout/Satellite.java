package unsw.blackout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.blackout.FileTransferException.*;


public abstract class Satellite {
    private String satelliteId;
    private String type;
    private Angle position;
    private double height;
    private ArrayList<File> files;
    private int sending = 0;
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
        List<String> satList = new ArrayList<String>();
        List<String> devList = new ArrayList<String>();
        // find all communicables satellites
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() != satelliteId) {
                double satDistance = MathsHelper.getDistance(sat.getHeight(), sat.getPosition(), height, position);
                boolean satVisible = MathsHelper.isVisible(sat.getHeight(), sat.getPosition(), height, position);
                if (satDistance <= getRange() && satVisible) {
                    satList.add(sat.getSatelliteId());
                }
            }
        }
        // find all communicables devices
        for (Device device : devices) {
            double devDistance = MathsHelper.getDistance(height, position, device.getPosition());
            boolean devVisible = MathsHelper.isVisible(height, position, device.getPosition());
            if (devDistance <= getRange() && devVisible) {
                devList.add(device.getDeviceId());
            }
        }
        // check for communicables relay and add
        List<String> relayList = new ArrayList<String>();
        for (String id : satList) {
            for (Satellite sat : satellites) {
                if (sat.getSatelliteId() == id && sat.getType() == "RelaySatellite") {
                    relayList = sat.updateCommunicables(satellites, devices);
                    relayList.remove(satelliteId);
                }
            }
        }
        satList.addAll(relayList);
        satList.addAll(devList);
        return satList;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public void addFile(File file) {
        this.files.add(file);
    }

    public abstract double getSendBandwidth();

    public abstract double getReceiveBandwidth();

    public int hvSendBandwidth() {
        double bandwidth = Math.floor(getSendBandwidth() / sending);
        return (int) bandwidth;
    };

    public int hvReceiveBandwidth() {
        int numFile = 0;
        List<File> files = getFiles();
        for (File f : files) {
            if (!f.isTransferCompleted()) {
                numFile++;
            }
        }
        double bandwidth = Math.floor(getReceiveBandwidth() / numFile);
        return (int) bandwidth;
    };

    public File getFile(String fileName) {
        for (File f : files) {
            if (f.getFilename() == fileName) {
                return f;
            }
        }
        return null;
    }

    public EntityInfoResponse getSatelliteInfo() {
        Map<String, FileInfoResponse> filesMap = new HashMap<String, FileInfoResponse>();
        for (File file : files) {
            filesMap.put(file.getFilename(), file.getFileInfo());
        }
        return new EntityInfoResponse(satelliteId, position, height, type, filesMap);
    }

    public File satelliteSendFile(String fileName) throws FileTransferException {
        // this sat send file to other dev
        // check bandwidth
        int speed = (int) Math.floor(getSendBandwidth() / (sending + 1));
        if (speed == 0) {
            throw new VirtualFileNoBandwidthException(satelliteId);
        }
        File file = getFile(fileName);
        File send = new File(fileName, file.getContent(), "", file.getSize(), false, satelliteId, speed);
        sending++;
        return send;
    }

    public void satelliteReceiveFile(File file) throws FileTransferException {
        int numFile = 1;
        for (File f : files) {
            if (!f.isTransferCompleted()) {
                numFile++;
            }
        }
        int speed = (int) Math.floor(getReceiveBandwidth() / numFile);
        if (speed == 0) {
            throw new VirtualFileNoBandwidthException(satelliteId);
        }
        speed = Math.min(speed, file.getSpeed());
        file.setSpeed(speed);
        files.add(file);
    }

    public ArrayList<String> updateProgress() {
        // update all the files
        ArrayList<String> ids = new ArrayList<String>();
        for (File f : files) {
            String id = f.updateProgress();
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    public int getSending() {
        return sending;
    }

    public void setSending(int sending) {
        this.sending = sending;
    }
    public ArrayList<String> removeFilesOutOfRange(List<String> communicables) {
        ArrayList<String> remove = new ArrayList<String>();
        for (File f : files) {
            if (!f.isTransferCompleted()) {
                if (!communicables.contains(f.getFromId())) {
                    remove.add(f.getFromId());
                    files.remove(f);
                }
            }
        }
        return remove;
    }
    
}
