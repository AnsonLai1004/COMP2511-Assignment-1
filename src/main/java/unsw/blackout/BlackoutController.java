package unsw.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import unsw.blackout.FileTransferException.*;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;


public class BlackoutController {
    private ArrayList<Device> devices = new ArrayList<Device>();
    private ArrayList<Satellite> satellites = new ArrayList<Satellite>();

    public void createDevice(String deviceId, String type, Angle position) {
        // TODO: Task 1a)
        Device newDevice;
        if (type == "HandheldDevice") {
            newDevice = new HandheldDevice(deviceId, type, position);
        } else if (type == "LaptopDevice") {
            newDevice = new LaptopDevice(deviceId, type, position);
        } else {
            newDevice = new DesktopDevice(deviceId, type, position);
        }
        devices.add(newDevice);
        return;
    }

    public void removeDevice(String deviceId) {
        // TODO: Task 1b)
        for (Device device : devices) {
            if (device.getDeviceId() == deviceId) {
                devices.remove(device);
                return;
            }
        }
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        // TODO: Task 1c)
        Satellite newSatelite;
        if (type == "StandardSatellite") {
            newSatelite = new StandardSatellite(satelliteId, type, position, height);
        } else if (type == "TeleportingSatellite") {
            newSatelite = new TeleportingSatellite(satelliteId, type, position, height);
        } else {
            newSatelite = new RelaySatellite(satelliteId, type, position, height);
        }
        satellites.add(newSatelite);
        return;
    }

    public void removeSatellite(String satelliteId) {
        // TODO: Task 1d)
        for (Satellite satellite : satellites) {
            if (satellite.getSatelliteId() == satelliteId) {
                satellites.remove(satellite);
                return;
            }
        }
    }

    public List<String> listDeviceIds() {
        // TODO: Task 1e)
        List<String> deviceIds = new ArrayList<String>();
        for (Device device : devices) {
            deviceIds.add(device.getDeviceId());
        }
        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        // TODO: Task 1f)
        List<String> satelliteIds = new ArrayList<String>();
        for (Satellite satellite : satellites) {
            satelliteIds.add(satellite.getSatelliteId());
        }
        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        // TODO: Task 1g)
        File newFile = new File(filename, content);
        newFile.setSize(content.length());
        for (Device device : devices) {
            if (device.getDeviceId() == deviceId) {
                device.addFile(newFile);
            }
        }
    }

    public EntityInfoResponse getInfo(String id) {
        // TODO: Task 1h)
        for (Satellite satellite : satellites) {
            if (satellite.getSatelliteId() == id) {
                return satellite.getSatelliteInfo();
            }
        }
        for (Device device : devices) {
            if (device.getDeviceId() == id) {
                return device.getDeviceInfo();
            }
        }
        return null;
    }

    public void simulate() {
        // TODO: Task 2a)
        // update position and update progress of file transfer for Satellites
        for (Satellite sat : satellites) {
            sat.updatePosition();
            ArrayList<String> ids = sat.updateProgress();
            for (Satellite sat2 : satellites) {
                if (ids.contains(sat2.getSatelliteId())) {
                    sat2.setSending(sat2.getSending() - 1);
                }
            }
        }
        // update progress of file transfer for Devices
        for (Device dev : devices) {
            ArrayList<String> ids = dev.updateProgress();
            for (Satellite sat2 : satellites) {
                if (ids.contains(sat2.getSatelliteId())) {
                    sat2.setSending(sat2.getSending() - 1);
                }
            }
        }
        // remove files if its device out of range
        for (Satellite sat : satellites) {
            List<String> communicables = communicableEntitiesInRange(sat.getSatelliteId());
            ArrayList<String> ids = sat.removeFilesOutOfRange(communicables);
            for (Satellite sat2 : satellites) {
                if (ids.contains(sat2.getSatelliteId())) {
                    sat2.setSending(sat2.getSending() - 1);
                }
            }
        }
        for (Device dev : devices) {
            List<String> communicables = communicableEntitiesInRange(dev.getDeviceId());
            ArrayList<String> ids = dev.removeFilesOutOfRange(communicables);
            for (Satellite sat2 : satellites) {
                if (ids.contains(sat2.getSatelliteId())) {
                    sat2.setSending(sat2.getSending() - 1);
                }
            }
        }
        // recalculate speed for each file
        for (Satellite sat : satellites) {
            for (File f : sat.getFiles()) {
                if (!f.isTransferCompleted()) {
                    int sendSpeed = Integer.MAX_VALUE;
                    for (Satellite sat2 : satellites) {
                        if (f.getFromId() == sat2.getSatelliteId()) {
                            sendSpeed = sat2.hvSendBandwidth();
                        }
                    }
                    int speed = Math.min(sat.hvReceiveBandwidth(), sendSpeed);
                    f.setSpeed(speed);
                }
            }
        }
        for (Device dev : devices) {
            for (File f : dev.getFiles()) {
                if (!f.isTransferCompleted()) {
                    int sendSpeed = Integer.MAX_VALUE;
                    for (Satellite sat : satellites) {
                        if (f.getFromId() == sat.getSatelliteId()) {
                            sendSpeed = sat.hvSendBandwidth();
                        }
                    }
                    int speed = Math.min(Integer.MAX_VALUE, sendSpeed);
                    f.setSpeed(speed);
                }
            }
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        // TODO: Task 2 b)
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() == id) {
                return sat.updateCommunicables(satellites, devices);
            }
        }
        for (Device dev : devices) {
            if (dev.getDeviceId() == id) {
                return dev.updateCommunicables(satellites, devices);
            }
        }
        return null;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
        EntityInfoResponse from = getInfo(fromId);
        EntityInfoResponse to = getInfo(toId);
        Map<String, FileInfoResponse> filesMap;
        FileInfoResponse fileInfo;
        // Exception: the targeted file wasn't found on the source or it's a partial file.
        if (from.getFiles().containsKey(fileName)) {
            filesMap = from.getFiles();
            fileInfo = filesMap.get(fileName);
            if (!fileInfo.hasTransferCompleted()) {
                throw new VirtualFileNotFoundException(fileName);
            }
        } else {
            throw new VirtualFileNotFoundException(fileName);
        }
        // Exception: the targeted file already existed on the target or was in the process of downloading.
        if (to.getFiles().containsKey(fileName)) {
            throw new VirtualFileAlreadyExistsException(fileName);
        }
        File file = null;
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() == fromId) {
                file = sat.satelliteSendFile(fileName);
            }
        }
        for (Device dev : devices) {
            if (dev.getDeviceId() == fromId) {
                file = dev.deviceSendFile(fileName);
            }
        }
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() == toId) {
                sat.satelliteReceiveFile(file);
            }
        }
        for (Device dev : devices) {
            if (dev.getDeviceId() == toId) {
                dev.deviceReceiveFile(file);
            }
        }
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
