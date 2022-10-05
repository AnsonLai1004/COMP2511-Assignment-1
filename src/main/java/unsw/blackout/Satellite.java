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

    /**
     * check if satellite hv enough storage for file
     * @param file
     * @return 0 if true, 1 if max files reached, 2 if max storage reached
     */
    public abstract int hvStorage(File file);


    public abstract double getSendBandwidth();

    public abstract double getReceiveBandwidth();

    public double hvSendBandwidth(int addNumFile) {
        int numFile = addNumFile;
        List<File> files = getFiles();
        for (File f : files) {
            if (!f.isTransferCompleted() && f.getFromId() == null) {
                numFile++;
            }
        }
        double bandwidth = Math.floor(getSendBandwidth() / numFile);
        return bandwidth;
    };

    public double hvReceiveBandwidth(int addNumFile) {
        int numFile = addNumFile;
        List<File> files = getFiles();
        for (File f : files) {
            if (!f.isTransferCompleted() && f.getFromId() != null) {
                numFile++;
            }
        }
        double bandwidth = Math.floor(getReceiveBandwidth() / numFile);
        return bandwidth;
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

    public File satelliteSendFile(String fileName) throws FileTransferException{
        // this sat send file to other dev
        // check bandwidth
        int numFile = 1;
        for (File f : files) {
            if (!f.isTransferCompleted() && f.getFromId() == null) {
                numFile++;
            }
        }
        if (Math.floor(getSendBandwidth() / numFile) == 0) {
            throw new VirtualFileNoBandwidthException(satelliteId);
        }
        // create file and return
        for (File f : files) {
            if (f.getFilename() == fileName) {
                File sendFile = new File(fileName, "");
                sendFile.setFromId(satelliteId);
                sendFile.setTransferCompleted(false);
                sendFile.setSize(f.getSize());
                f.setTransferCompleted(false);
                return sendFile;
            }
        }
        return null;
    }

    public void satelliteReceiveFile(File file) throws FileTransferException {
        int numFile = 1;
        for (File f : files) {
            if (!f.isTransferCompleted() && f.getFromId() != null) {
                numFile++;
            }
        }
        if (Math.floor(getReceiveBandwidth() / numFile) == 0) {
            throw new VirtualFileNoBandwidthException(satelliteId);
        }
        files.add(file);
    }
}
