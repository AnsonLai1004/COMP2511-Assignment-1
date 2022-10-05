package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

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
    public ArrayList<File> getFiles() {
        return files;
    }
    public void addFile(File file) {
        this.files.add(file);
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

    public List<String> updateCommunicables(List<Satellite> satellites, List<Device> devices) {
        List<String> communicables = new ArrayList<String>();
        // find all communicables satellites
        for (Satellite sat : satellites) {
            double distance = MathsHelper.getDistance(sat.getHeight(), sat.getPosition(), getPosition());
            boolean visible = MathsHelper.isVisible(sat.getHeight(), sat.getPosition(), getPosition());
            if (distance <= getRange() && visible) {
                if (sat.getType() == "RelaySatellite") {
                    List<String> relayList = sat.updateCommunicables(satellites, devices);
                    relayList.remove(deviceId);
                    communicables.addAll(relayList);
                }
                communicables.add(sat.getSatelliteId());
            }
        }
        return communicables;
    }

    public File getFile(String fileName) {
        for (File f : files) {
            if (f.getFilename() == fileName) {
                return f;
            }
        }
        return null;
    }

    public EntityInfoResponse getDeviceInfo() {
        Map<String, FileInfoResponse> filesMap = new HashMap<String, FileInfoResponse>();
        for (File file : files) {
            filesMap.put(file.getFilename(), file.getFileInfo());
        }
        return new EntityInfoResponse(deviceId, position, RADIUS_OF_JUPITER, type, filesMap);
    }

    public File deviceSendFile(String fileName) {
        for (File f : files) {
            if (f.getFilename() == fileName) {
                File sendFile = new File(fileName, "");
                sendFile.setFromId(deviceId);
                sendFile.setTransferCompleted(false);
                sendFile.setSize(f.getSize());
                f.setTransferCompleted(false);
                return sendFile;
            }
        }
        return null;
    }

    public void deviceReceiveFile(File file) {
        files.add(file);
    }
}
