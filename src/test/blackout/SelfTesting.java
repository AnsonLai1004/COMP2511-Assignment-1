package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;


import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class SelfTesting {
    // new test 1
    @Test
    public void testRelayExtentRange() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "StandardSatellite", 79741, Angle.fromDegrees(210));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(130));
        controller.createSatellite("Satellite3", "RelaySatellite", 81795, Angle.fromDegrees(161));

        assertListAreEqualIgnoringOrder(
            Arrays.asList("DeviceC", "Satellite3"),
            controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(
            Arrays.asList("DeviceC", "Satellite1"),
            controller.communicableEntitiesInRange("Satellite3"));
        assertListAreEqualIgnoringOrder(
            Arrays.asList("Satellite1", "Satellite3"),
            controller.communicableEntitiesInRange("DeviceC"));
    }
    @Test
    public void messageProgress() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite(
            "Satellite1",
            "StandardSatellite",
            10000 + RADIUS_OF_JUPITER,
            Angle.fromDegrees(320));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(
            new FileInfoResponse("FileAlpha", "", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate();
        assertEquals(
            new FileInfoResponse("FileAlpha", "H", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate();
        assertEquals(
            new FileInfoResponse("FileAlpha", "He", msg.length(), false),
            controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate();
        assertEquals(
            new FileInfoResponse("FileAlpha", "Hey", msg.length(), true),
            controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
    }
}
