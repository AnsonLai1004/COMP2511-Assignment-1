package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                info = new EntityInfoResponse(id, satellite.getPosition(), satellite.getHeight(), satellite.getType());
                return info;
            }
        }
        for (Device device : devices) {
            if (device.getDeviceId() == id) {
                ArrayList<File> files = device.getFileList();
                Map<String, FileInfoResponse> filesMap = new HashMap<String, FileInfoResponse>();
                for (File file : files) {
                    FileInfoResponse fileInfo = new FileInfoResponse(
                        file.getFilename(), file.getContent(), file.getSize(), true);
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
                communicables = dev.updateCommunicables(satellites);
            }
        }
        return communicables;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
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
