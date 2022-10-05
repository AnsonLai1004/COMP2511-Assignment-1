package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unsw.blackout.FileTransferException.*;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

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
        EntityInfoResponse info;
        for (Satellite satellite : satellites) {
            if (satellite.getSatelliteId() == id) {
                ArrayList<File> files = satellite.getFiles();
                Map<String, FileInfoResponse> filesMap = new HashMap<String, FileInfoResponse>();
                for (File file : files) {
                    FileInfoResponse fileInfo = new FileInfoResponse(
                        file.getFilename(), file.getContent(), file.getSize(), file.isTransferCompleted());
                    filesMap.put(file.getFilename(), fileInfo);
                }
                info = new EntityInfoResponse(id, satellite.getPosition(),
                    satellite.getHeight(), satellite.getType(), filesMap);
                return info;
            }
        }
        for (Device device : devices) {
            if (device.getDeviceId() == id) {
                ArrayList<File> files = device.getFiles();
                Map<String, FileInfoResponse> filesMap = new HashMap<String, FileInfoResponse>();
                for (File file : files) {
                    FileInfoResponse fileInfo = new FileInfoResponse(
                        file.getFilename(), file.getContent(), file.getSize(), file.isTransferCompleted());
                    filesMap.put(file.getFilename(), fileInfo);
                }

                info = new EntityInfoResponse(id, device.getPosition(), RADIUS_OF_JUPITER, device.getType(), filesMap);
                return info;
            }
        }
        return null;
    }

    public void simulate() {
        // TODO: Task 2a)
        for (Satellite satellite : satellites) {
            satellite.updatePosition();
            updateTransfer(satellite);
        }
        for (Device device : devices) {
            updateTransfer(device);
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
        List<String> communicables = new ArrayList<String>();
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() == id) {
                communicables = sat.updateCommunicables(satellites, devices);
            }
        }
        for (Device dev : devices) {
            if (dev.getDeviceId() == id) {
                communicables = dev.updateCommunicables(satellites, devices);
            }
        }
        return communicables;
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
        // check if fromId or toId is satellite
        boolean toIdIsSatellite = false;
        boolean fromIdIsSatellite = false;
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() == toId) {
                toIdIsSatellite = true;
            }
            if (sat.getSatelliteId() == fromId) {
                fromIdIsSatellite = true;
            }
        }
        // no more bandwidth exists for a satellite to be able to use for new devices
        if (fromIdIsSatellite) {
            // check if fromId have more bandwidth to send
            for (Satellite sat : satellites) {
                if (sat.getSatelliteId() == fromId) {
                    double bandwidth = sat.hvSendBandwidth(1);
                    if (bandwidth == 0) {
                        throw new VirtualFileNoBandwidthException(fromId);
                    }
                }
            }
        }
        if (toIdIsSatellite) {
            // check if toId have more bandwidth to receive
            for (Satellite sat : satellites) {
                if (sat.getSatelliteId() == toId) {
                    double bandwidth = sat.hvReceiveBandwidth(1);
                    if (bandwidth == 0) {
                        throw new VirtualFileNoBandwidthException(toId);
                    }
                }
            }

        }
        // change original file TransferCompleted to false + getFilesize
        int fileSize = 0;
        if (fromIdIsSatellite) {
            for (Satellite sat : satellites) {
                if (sat.getSatelliteId() == fromId) {
                    ArrayList<File> files = sat.getFiles();
                    for (File f : files) {
                        if (f.getFilename() == fileName) {
                            f.setTransferCompleted(false);
                            fileSize = f.getSize();
                        }
                    }
                    sat.setFiles(files);
                }
            }
        } else {
            for (Device dev : devices) {
                if (dev.getDeviceId() == fromId) {
                    ArrayList<File> files = dev.getFiles();
                    for (File f : files) {
                        if (f.getFilename() == fileName) {
                            f.setTransferCompleted(false);
                            fileSize = f.getSize();
                        }
                    }
                }
            }
        }
        // create file
        File file = new File(fileName, "");
        file.setSize(fileSize);
        file.setFromId(fromId);
        file.setTransferCompleted(false);
        // Exception: a satellite runs out of space
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() == toId) {
                if (sat.hvStorage(file) == 1) {
                    throw new VirtualFileNoStorageSpaceException("Max Files Reached");
                } else if (sat.hvStorage(file) == 2) {
                    throw new VirtualFileNoStorageSpaceException("Max Storage Reached");
                }
            }
        }
        // add file to device
        for (Satellite sat : satellites) {
            if (sat.getSatelliteId() == toId) {
                sat.addFile(file);
            }
        }
        for (Device dev : devices) {
            if (dev.getDeviceId() == toId) {
                dev.addFile(file);
            }
        }
    }

    // helper
    public void updateTransfer(Satellite sat) {
        List<File> files = sat.getFiles();
        for (File f : files) {
            if (!f.isTransferCompleted() && f.getFromId() != null) {
                // receive
                File originalFile = null;
                double sendSpeed = 0;
                for (Satellite fromSat : satellites) {
                    if (fromSat.getSatelliteId() == f.getFromId()) {
                        // get sending speed of fromSat
                        sendSpeed = fromSat.hvSendBandwidth(0);
                        originalFile = fromSat.getFile(f.getFilename());
                    }
                }
                for (Device fromDev : devices) {
                    if (fromDev.getDeviceId() == f.getFromId()) {
                        sendSpeed = Double.MAX_VALUE;
                        originalFile = fromDev.getFile(f.getFilename());
                    }
                }
                double receiveSpeed = sat.hvReceiveBandwidth(0);
                double bottleneck = Double.min(sendSpeed, receiveSpeed);
                fileTransfer(originalFile, f, bottleneck);
            }
        }
    }
    public void updateTransfer(Device dev) {
        List<File> files = dev.getFiles();
        for (File f : files) {
            if (!f.isTransferCompleted() && f.getFromId() != null) {
                // receive
                File originalFile = null;
                double sendSpeed = 0;
                for (Satellite fromSat : satellites) {
                    if (fromSat.getSatelliteId() == f.getFromId()) {
                        // get sending speed of fromSat
                        sendSpeed = fromSat.hvSendBandwidth(0);
                        originalFile = fromSat.getFile(f.getFilename());
                    }
                }
                fileTransfer(originalFile, f, sendSpeed);
            }
        }
    }
    public File fileTransfer(File from, File to, double speed) {
        String fromContent = from.getContent();
        String toContent = to.getContent();
        int end = toContent.length() + (int) speed;
        if (end >= fromContent.length()) {
            // transfer done, set to transferCompleted true
            end = fromContent.length();
            to.setTransferCompleted(true);
            to.setFromId(null);
            from.setTransferCompleted(true);
        }
        fromContent = fromContent.substring(toContent.length(), end);
        to.setContent(toContent + fromContent);
        return to;
    }
    /*public void teleportingTransfer(Satellite sat) {
        List<File> files = sat.getFiles();
        for (File f : files) {
            if (!f.isTransferCompleted()) {
                String fromId = f.getFromId();
                List<String> devList = listDeviceIds();
                if (devList.contains(fromId)) {
                    // from device

                } else {
                    // from sat
                }
            }

        }
    }*/
    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
