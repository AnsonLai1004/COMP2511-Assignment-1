1. Preamble and Problem
A great deal of today's technology uses satellites which orbit the Earth in one way or another. For example, tagging photographs with their location, telecommunications, and even missile control systems. There are currently 4,550 active satellites (as of 2022) orbiting the Earth all together.

In the far future, society has become advanced to the point where they have begun to occupy a series of moons and asteroids around the planet Jupiter. Individuals rely on satellites for all their communication desires. Three major satellites exist around Jupiter.

This assessment aims to provide you with design experience for a non-trivial system. You will be architecting and modelling how these multiple satellites will communicate and function with various devices. The form of communication that you will be simulating is a simplified version of the modern internet, simply just file transfer. You can either upload a file to a satellite from a device, download a file from a satellite to a device, or send a file from a satellite to a satellite.

Your solution should adopt an Object-Oriented approach that utilises concepts such as abstraction, encapsulation, composition, and inheritance, as taught in lectures.

There is an introduction video available which you can watch - some of the contents of the video relates to a previous version of the assignment but most of it is relevant and gives you an in-depth overview of the tasks and starter code.

1.1 A simple example
Let’s assume initially there is a Standard Satellite at height approximately 80,000 km above the centre of Jupiter and at θ = 128 degrees. In addition there are 3 devices; Device A, Device B, and Device C. Device A and Device C are handheld devices, whereas Device B is a desktop device.


 

In this system, the satellite can talk to Device A and Device A can talk to the satellite (communication is done by sending files) since they are in range of each other. Satellite A however cannot talk to Device B (and Device B cannot talk to Satellite A) because Standard Satellites have restrictions on what devices they can talk to (discussed in more detail later). Finally, Satellite A cannot connect to Device C because it is not in visible sight range. The red line in the image highlights this.

In this example, devices are static and will not move, but satellites can! Satellites move based on a constant linear velocity, in this case Standard Satellites have a linear velocity of 2,500 km per minute (or 2,500,000 metres). From this, we can calculate its angular velocity based on its height (which is the radius from the center of Jupiter). For example, after 10 minutes it would have moved a total of 2,500 / 80,000 * 10 mins = 0.03125 * 10 = 0.3125 radians ~= 18 degrees(note that we don't have to account for the fact they are measured in km here since the extra 10^3 component will cancel out). This means our new position is 128 - 18 which is approximately 110-111 degrees.


Eventually after moving for a couple more minutes it will get out of range of the devices A and B.

Secondly, we need to look at interaction between satellites and devices. They can communicate through the transfer of files. Files can be transferred between satellites/devices and other satellites (files can not be directly sent from device to device).

To begin let's create a new file on a device. Clicking on Device A and pressing Create File as shown below we can create a file.


 

Once created, files cannot be modified/deleted, furthermore every filename has to be unique. Creating a file called Halo Jupiter with its contents also being Halo Jupiter we can view it by going to the device again and clicking Open File as shown below.


We can then send this file to our satellite by just clicking on the device ensuring the file and satellite are selected as below then clicking Send File.


If we then try to open the file on the satellite we notice that it is empty and says 0/12 for its size. This is because files transfers aren't instant, they are based on the bandwidth of the satellite. Standard Satellites are relatively slow, so they will take 12 minutes to send this file since they only send at 1 byte per minute. If we let it run for at least 12 minutes and look again we will see the file has finished sending.


We could then continue this by running the simulation a little longer and letting the satellite orbit around to Device C and then sending the file down to Device C.

1.2 Simulation
A simulation is an incremental process starting with an initial world state, say WorldState_00. We add a specified time interval of 1 minute and calculate the new positions of all the satellites after the minute. We then go and update all the connections accordingly to derive the next world state WorldState_01. Similarly, we derive WorldState_02 from WorldState_01, WorldState_03 from WorldState_02, and so on. This act of feeding a world state into the next forms a sort of state machine. An similar example of this is Conway's Game of Life.

WorldState_00 -> WorldState_01 -> WorldState_02 -> …

In our case our simulation runs at an accuracy of 1 minute and each state transition will take only 1 minute.

2. Requirements 🪐
ℹ  NOTE: This problem will not require any heavy use of mathematics, you will be provided with a library that will perform all the calculations for you.

There are three tasks set out for you.

Model the problem, including:

Modelling the satellites/devices;

Satellites/Devices must be able to be added/removed at runtime (with various consequences);

Most importantly, write a series of 'queries' about the current world state such as what devices currently exist.

Allow satellites/devices to send files to other satellites.

Implement moving devices.

2.1 Assumptions
In this problem, we are going to have to make some assumptions. Let us assume that:

Satellites move around at a constant linear velocity regardless of their distance from the planet (their angular velocity would change based upon the distance, though).

The model is two dimensional

Objects do not rotate on their axis and simple planatary orbit is the only 'rotation' that is allowed in the system.

Jupiter has a radius of 69,911 kilometres.

For the sake of consistency:

All distances are in kilometres (1 km = 1,000 m).

Angular velocity is in radians per minute (not per second).

Linear velocity is in kilometres per minute (not per second).

2.2 Devices 🖨️
There are three types of devices available. Each device has a maximum range from which it can connect to satellites.

HandheldDevice – phones, GPS devices, tablets.

Handhelds have a range of only 50,000 kilometres (50,000,000 metres)

LaptopDevice – laptop computers.

Laptops have a range of only 100,000 kilometres (100,000,000 metres)

DesktopDevice – desktop computers and servers.

Desktops have a range of only 200,000 kilometres (200,000,000 metres)

2.3 Files 📨
Devices can store an infinite number of files and can upload/download files from satellites. Files are represented simply by just a string representing their content and a filename representing their name.

All files can be presumed to purely consist of alphanumeric characters or spaces (i.e. a-z, A-Z, 0-9, or spaces) and filenames can be presumed to be unique (i.e. we will never create two files of the same name with different content). Furthermore, since we are dealing with such a simple subset, 1 character is equivalent to 1 byte. We will often refer to the size of files in terms of bytes, and the file size only relates to the content of the file (and not the filename).

To send files the target needs to be within the range of the source BUT the source does not have to be within the range of the target. For example if a HandheldDevice (range 50,000 km) is 100,000 km away from a StandardSatellite(range 150,000 km) it can't send files to the satellite but it can receive files from the satellite. If the device is 160,000 km away from the satellite neither can interact with each other. Satellites can also send files to other satellites but devices can not send files to other devices.

Files do not send instantly however, and are limited by the bandwidth of the satellites. Satellites will always ensure fairness and will evenly allocate bandwidth to all currently uploading files (for example, if a satellite has a bandwidth of 10 bytes per minute and 3 files, every file will get 3 bytes per minute. You'll have 1 unused byte of bandwidth). Devices aren't limited on the number of downloads/uploads they can do.

If a device goes out of range of a satellite during the transfer of a file (either way) the partially downloaded file should be removed from the recipient. This does raise the question of whether or not you should start transferring a file if it's obvious that it won't finish. Solving this problem is mostly algorithmic and isn't particularly interesting to the point of this assignment so you don't have to do anything special here: if someone asks to transfer a file... begin to transfer it.

During a file transfer, in the case where sending speed and receiving speed are different, the transfer rate is bottlenecked by min(sending speed, receiving speed).

2.4 Satellites 🛰️
There are 2 specialised types of satellites (and one basic one). Satellites have a set amount of bandwidth for transferring files and a set amount of storage.

Default direction for all satellites is negative (clockwise), unless otherwise specified.

StandardSatellite

Moves at a linear speed of 2,500 kilometres (2,500,000 metres) per minute

Supports handhelds and laptops only (along with other satellites)

Maximum range of 150,000 kilometres (150,000,000 metres)

Can store up to either 3 files or 80 bytes (whichever is smallest for the current situation).

Can receive 1 byte per minute and can send 1 byte per minute meaning it can only transfer 1 file at a time.

TeleportingSatellite

Moves at a linear velocity of 1,000 kilometres (1,000,000 metres) per minute

Supports all devices

Maximum range of 200,000 kilometres (200,000,000 metres)

Can receive 15 bytes per minute and can send 10 bytes per minute.

Can store up to 200 bytes and as many files as fits into that space.

When the position of the satellite reaches θ = 180, the satellite teleports to θ = 0 and changes direction.

If a file transfer from a satellite to a device or a satellite to another satellite is in progress when the satellite teleports, the rest of the file is instantly downloaded, however all "t" letter bytes are removed from the remaining bytes to be sent.

For the satellite to satellite case, the behaviour is the same whether it is the sender or receiving that is teleporting

If a file transfer from a device to a satellite is in progress when the satellite teleports, the download fails and the partially uploaded file is removed from the satellite, and all "t" letter bytes are removed from the file on the device.

There is no 'correction' with the position after a teleport occurs as there is for Relay Satellites (see below). Once the satellite teleports to θ = 0 it does not continue moving for the remainder of the tick.

Teleporting satellites start by moving anticlockwise.

RelaySatellite

Moves at a linear velocity of 1,500 kilometres (1,500,000 metres) per minute

Supports all devices

Max range of 300,000 kilometres (300,000,000 metres)

Cannot store any files and has no bandwidth limits

Devices/Satellites cannot transfer files directly to a relay but instead a relay can be automatically used by satellites/devices to send to their real target.

For example if a HandheldDevice (range 50,000km) is 200,000km away from a StandardSatellitethat it wishes to communicate with, it is able to communicate to the satellite through the use of the relay if the relay is within 50,000km of the device and the satellite is within 300,000km (the range of the relay) of the relay.

Files being transferred through a relay should not show up in the relay's list of files.

Only travels in the region between 140° and 190°

When it reaches one side of the region its direction reverses and it travels in the opposite direction.

This 'correction' will only apply on the next minute. This means that it can briefly exceed this boundary. There is a unit test that details this behaviour quite well called testRelaySatelliteMovement in Task2ExampleTests.java

You can either do the radian maths here yourself or use the functions in src/unsw/utils/Angle.java to do comparisons.

In the case that the satellite doesn't start in the region [140°, 190°], it should choose whatever direction gets it to the region [140°, 190°] in the shortest amount of time.

As a hint (and to prevent you having to do maths) this 'threshold' angle is 345°; if a relay satellite starts on the threshold 345° it should take the positive direction.

You can assume that we don’t create any satellite at 140 and 190 degree in our tests (this behaviour’s undefined).

Relay satellites don't allow you to ignore satellite requirements (other than visibility/range), for example you can't send a file from a Desktop Device to a Standard Satellite due to the fact that a Standard Satellite doesn't support Desktops. This should hold even if a Relay is used along the way.

HINT: because there are no bandwidth limits and you don't have to show any tracking of files that go through the relay. Keep it simple! Don't over-engineer a solution for this one. You'll notice that the frontend when drawing connections that utilise relays don't go through the relay as shown below.


 

ℹ  To save you some googling v = r * ω (where v is linear velocity i.e. metres per minute, ω is angular velocity i.e. radians per minute, and r is the radius / height of the satellite).

2.5 Visualisation 🎨
To help you understand this problem we've made a frontend for the application you are writing, we also have a sample implementation for you to refer to. You'll also find that the starter code will have a simple webserver to run this frontend for you (already written) such that you can run the UI locally.

Assignment Sample Implementation

This is NOT necessary for you to get marks, and it is more there just for those that enjoy seeing something slowly come together as they complete tasks. It's possible and still quite nice to just use the reference implementation + JUnit tests to design and build your solution without ever having to run and test the UI locally.

⚠  As with any software, bugs could exist in either the frontend or the sample implementation. Thus you should treat the specification as the final word and not the sample implementation. If you do notice any bugs or issues, please raise it on the forum so it can get fixed (or a workaround will be provided). Furthermore as the frontend expects that most of the code follows the specification you may run into weird bugs if you have wildly different behaviour.

Functionality is listed below;

You can click on a satellite or a device to view its properties, as well as all the files it currently has, send files to other satellites, create files if it's a device, or delete the entity

You can mouse over any satellite/device to see all the possible targets

You can click on anything near the radius of Jupiter to create a device and anywhere else in space to create a satellite

There are buttons on the top left to refresh the screen (for whatever reason) as well as run simulations for set periods of time

If your backend throws any exceptions an error will popup in the UI and an error log will be in the Java output window in VSCode (except for transferring file tasks which require the throwing of certain exceptions).

3. Program Structure
File

Path

Description

Should you need to modify this?

BlackoutController.java

src/unsw/blackout/BlackoutController.java

Contains one method for each command you need to implement.

Yes.

App.java

src/App.java

Runs a server for blackout.

No.

MathsHelper.java

src/unsw/utils/MathsHelper.java

Contains all the math logic that you'll require.

No.

Angle.java

src/unsw/utils/Angle.java

Contains an abstraction for angles that lets you easily convert between radians/degrees without having to worry about what 'state' it is currently in.

No.

EntityInfoResponse.javaand FileInfoResponse.java

src/unsw/response/models/EntityInfoResponse.javaand src/unsw/response/models/FileInfoResponse.java

Contains the result for certain functions in BlackoutController.

No.

Scintilla.java and auxiliary files; Environment.java, PlatformUtils.java, and WebServer.java

src/scintilla

Contains a small custom built wrapper around Spark-Java for running a web server. When run it automatically opens a web browser.

No.

Task1ExampleTests.java

src/test/Task1ExampleTests.java

Contains a simple test to get you started with Task 1.

Yes, feel free to add more tests here or just create a new testing file.

Task2ExampleTests.java

src/test/Task2ExampleTests.java

Contains a simple test to get you started with Task 2.

Yes, feel free to add more tests here or just create a new testing file.

4. Tasks
Task 1 - Modelling (15%) 🌎
This task is mainly focused on design, if you start with an initial design via a rough UML Diagram on pen and paper, you'll find this will be quite straightforward! Very little logic exists in this first task.

All methods below exist in the class src/unsw/blackout/BlackoutController.java

Task 1 a) Create Device
Adds a device to the ring at the position specified, the position is measured as an angle relative to the x-axis, rotating anticlockwise.


public void createDevice(String deviceId, String type, Angle position);
Task 1 b) Remove Device
Removes a device (specified by id). You don't need to cancel all current downloads/uploads (relevant for Task 2).


public void removeDevice(String deviceId);
Task 1 c) Create Satellite
Creates a satellite at a given height (measured from centre of Jupiter) at a given angle.


public void createSatellite(String satelliteId, String type, 
                            double height, Angle position);
Task 1 d) Remove Satellite
Removes a satellite from orbit. You don't need to cancel all current downloads/uploads (relevant for Task 2).


public void removeSatellite(String satelliteId);
Task 1 e) List all device ids
Lists all the device ids that currently exist.


public List<String> listDeviceIds();
Task 1 f) List all satellite ids
Lists all the satellite ids that currently exist.


public List<String> listSatelliteIds();
Task 1 g) Add file to device
Adds a file to a device (not a satellite). Files are added instantly.


public void addFileToDevice(String deviceId, String filename, String content);
Task 1 h) Get device/satellite information
Get detailed information about a single device or a satellite.

ℹ  NOTE: ids are unique, so no 2 devices and/or satellites can have the same id.


public EntityInfoResponse getInfo(String id);
EntityInfoResponse is a struct that is supplied that contains the following members (it also comes with a constructor and a getter).


public final class EntityInfoResponse {
  /**
   * The unique ID of the device.
   **/
  private final String id;

  /**
   * The angular position of the entity in radians
   **/
  private final Angle position;

  /**
   * The height of the entity measured in kilometres
   * devices will have a height equal to the radius of Jupiter.
   */
  private final double height;

  /**
   * the type of the entity i.e. DesktopDevice, StandardSatellite, ...
   **/
  private final String type;

  /**
   * A map of all the files that this entity has access to.
   **/
  private final Map<String, FileInfoResponse> files;
}
ℹ  NOTE: final for classes means it can't have subclasses, final for functions means they can't have overrides, and final for members means they can't be modified after the constructor. You don't have to use it in the assignment.

🔗  Map<..> is similar to Dictionaries in Python and acts as a mapping between a key and a value. You can find more information here

Task 1 Example
You can test your implementations for Task 1 using the simple test provided in the file src/test/Task1ExampleTests.java. Later you need to add more tests to properly test your implementations.

The method testExample uses a JUnit test to test a few world states. Please read the method testExample.


@Test
public void testExample() throws FileTransferException {
    // Task 1
    // Example from the specification
    BlackoutController controller = new BlackoutController();

    // Creates 1 satellite and 2 devices
    // Gets a device to send a file to a satellites and gets another device to download it.
    // StandardSatellites are slow and transfer 1 byte per minute.
    controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
    controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
    controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

    String msg = "Hi 42";
    controller.addFileToDevice("DeviceC", "FileAlpha", msg);
    controller.sendFile("FileAlpha", "DeviceC", "Satellite1");
    assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

    controller.simulate(msg.length());
    assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

    controller.sendFile("FileAlpha", "Satellite1", "DeviceB");
    assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

    controller.simulate(msg.length());
    assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

    // Hints for further testing:
    // - What about checking about the progress of the message half way through?
    // - Device/s get out of range of satellite
    // ... and so on.
}
Task 2 - Simulation 📡 (25%)
The second tasks involves the actual simulating of the movement of satellites and transferring files.

Task 2 a) Run the Simulation
This should run the simulation for a single minute. This will include moving satellites around and later on transferring files between satellites and devices.


public void simulate();
ℹ  NOTE: To help with testing a simulate(int numberOfMinutes) has already been written which just calls simulate() numberOfMinutes' times

Task 2 b) List all entities in range
Lists every entity in range of the specified entity that can communicate. Devices can only communicate with certain satellites (as specified in the Satellites section). Satellites can communicate with every satellite and a certain subset of devices (once again as discussed in the Satellites section).

For an entity to be in range it also has to be visible to the other entities (i.e. if you drew a line between the two entities that line cannot go through Jupiter). Relay satellites allow you to reach entities that were otherwise invisible due to position/range, so long as the entity is visible to the relay satellite. In the example below, satellite 1 and device B and C are not visible to each other initially, adding relay satellites (R1, R2 and R3) make them fall in range of each other 


 


public List<String> communicableEntitiesInRange(String id);
??  Checkout src/unsw/blackout/utils/MathHelper there are some very useful functions there that do the vast majority of the work for you, you shouldn't need to figure out any complicated maths.

ℹ  This is useful for 2c.

Task 2 c) File Transferring
Devices/Satellites can communicate by transferring files between each other. Files transfer progress can be measured by looking at getInfo(String id) which contains a map of all files currently transferred/being transferred with the current progress being the number of bytes that have currently been transferred. You can only transfer files to entities in range.


public void sendFile(String fileName, String fromId, String toId) throws FileTransferException;
This function should throw an exception in the following cases (the message is important and will be marked), all of these Exception classes should derive from the exception class FileTransferException and are provided for you.

Sending and receiving bandwidth are calculated separately at each tick.

File doesn't exist on fromId or it's a partial file (hasn't finished transferring): should throw unsw.blackout.VirtualFileNotFoundException with message equal to the file name

File already exists on toId or is currently downloading to the target: should throw unsw.blackout.VirtualFileAlreadyExistsException with message equal to the file name

Satellite Bandwidth is full: should throw unsw.blackout.VirtualFileNoBandwidthException with message equal to whatever satellite has too little bandwidth

i.e. if you have a bandwidth of 8 bytes per minute and are currently uploading/download 8 files, you cannot upload/download any more files.

No room on the satellite: should throw unsw.blackout.VirtualFileNoStorageSpaceException , with the following cases

Exception message equal to Max Files Reached if the lack of room was due to a max file cap (for example standard satellites can only store 3 files); or

Max Storage Reached if the lack of room was due to a max storage cap (for example standard satellites can only store 80 bytes). This includes the full size of messages that are currently being sent. For example, if a file is in mid-transfer to a StandardSatellite (capacity 80 bytes) with 50/80 bytes completed, and the file we now want to transmit is 10 bytes, the exception should be thrown.

You'll find all the exception classes in once place src/unsw/blackout/FileTransferException.java, exceptions are a great case for a static nested class; for the sake of this assignment this is not a detail you need to worry/care about, just use them as you normally would. For example throw new FileTransferException.VirtualFileNoBandwidthException("Satellite1").

Note: you don't have to throw an exception for the case that the entity is out of range/not visible since you can presume that all entities will be in range for at least the first minute.

If multiple exception conditions occur, you should throw them in the following order:

VirtualFileNotFoundException

VirtualFileNoBandwidthException

VirtualFileAlreadyExistsException

VirtualFileNoStorageSpaceException

ℹ  We won't give you any very malicious test cases where this will make a difference but just for consistency; you should do the movement of satellites before you handle any download/upload for that simulation minute.

Task 2 Example
You can test your implementations for Task 2 using the simple test provided in the file src/test/Task2ExampleTests.java. Later, you will need to add more tests to properly test your implementation. For Task 2 we supply a few different tests just to help you test a variety of cases.


@Test
public void testExample() {
    // Task 2
    // Example from the specification
    BlackoutController controller = new BlackoutController();

    // Creates 1 satellite and 2 devices
    // Gets a device to send a file to a satellites and gets another device to download it.
    // StandardSatellites are slow and transfer 1 byte per minute.
    controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
    controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
    controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

    String msg = "Hey";
    controller.addFileToDevice("DeviceC", "FileAlpha", msg);
    assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

    controller.simulate(msg.length() * 2);
    assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

    assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
    assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

    controller.simulate(msg.length());
    assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

    // Hints for further testing:
    // - What about checking about the progress of the message half way through?
    // - Device/s get out of range of satellite
    // ... and so on.
}
Task 3 - Evolution of Requirements (10%) :alien: 
This task is intentionally meant to be difficult (intended for students aiming to achieve a High Distinction) and will test the quality of your design in Tasks 1 and 2.

For this part of the assignment, you have the option to complete either Part A or Part B. If you complete both, please specify which part you would like us to mark.

Part A is more difficult to understand, but once you understand the problem it is reasonably straightforward to implement.

Part B is easier to understand, but more difficult to implement. 

a) Moving Devices :mobile_phone: 
This task introduces a new subset of device which moves on a path of its own as time progresses. Each type of device can be created as a moving device, though the behaviour of each type of moving device is the same, except for the speed at which it moves:

Handheld - 50km per minute

Desktop - 20km per minute

Laptop - 30km per minute

Moving Devices move at a constant rate along the surface of Jupiter in an anticlockwise direction. However, though in this problem Jupiter is still 2D, it is no longer completely spherical. A slope can be created between any two positions on Jupiter, which signifies a change in altitude of the Jupiter's surface. All slopes have a gradient μ, which is the change in height for every degree between the start and end of the slope.

In the diagram below, two slopes have been created - one which goes from θ = 0 to θ = 20 with a gradient of 700, and one which goes from θ = 20 to θ = 50 with a gradient of -520.

The second slope intersects the Jupiter at around θ = 46.9.


 

When a Moving Device encounters an area of Jupiter which contains a slope, it remains on the new "surface" of Jupiter, i.e. moves up and down the slopes. Its height changes relative to the centre of Jupiter as well as its position according to the formula Δh = μ * Δθ.

Some complexity arises with this calculation as the mathematically minded reader may have already figured out - because the Δθ in each tick is calculated based on the angular velocity of the device, which itself is calculated using the formula ω = v / h. However, since h is changing constantly, this becomes a calculus problem. You are welcome to determine the equations required to make this computation accurate, however for simplicity's sake we will allow the hin the calculation of the angular velocity to simply be the initial height of the satellite, before the tick takes place in any given tick, i.e. ω = v / h_initial. Our tests will allow for either implementation.

If, like in the above diagram, a downward slope extends past the base radius of Jupiter, then the maximum of the height of the slope and the base radius is taken; i.e. a device will never be at a height less than 69,911. In the above example, when the device is at θ = 46.9 to θ = 50 its height will be 69,911.

If a moving device finds itself to be at a sudden drop, where the slope finishes but the height is above ground level, then the device simply is transported back to ground level. We will not test the scenario where, at the end of a tick, the device is on the edge of a slope not at ground level (thereby being at two possible locations due to the infinite gradient of the "cliff").


 

To implement slopes and moving devices, you will need to complete the following methods in the BlackoutController:


public void createDevice(String deviceId, String type, 
                         Angle position, boolean isMoving)

public void createSlope(int startAngle, int endAngle, int gradient) 
Note that because of the way the frontend renders slopes, you will be able to see a slope when it is created on the frontend even without the backend having been implemented but it will have no logic/real existence in the model.

It is possible for slopes to be created at overlapping positions. If a slope A is created at angle (X, Y), where X is the starting position and Y is the end of the slope, and slope B is created at angle (I, J) similarly, and I is between X and Y:

If slope A is increasing and B is increasing, then Slope B becomes the actual surface of the Jupiter, starting from Iat A's current height, and continuing until it finishes, if the gradient of Slope B is greater than the gradient of Slope A. Otherwise, B will become the actual surface of Jupiter from Y to J, though the mathematical start of slope B is still I.

If slope A is increasing and B is decreasing, then A remains the actual surface of the Jupiter until Y. If J > Y, then Bbecomes the new surface from J to Y.

If slope A is decreasing and B is increasing, then Slope B becomes the actual surface of the Jupiter, starting at A's current height.

If slope A is decreasing and B is also decreasing, then Slope B becomes the actual surface of the Jupiter if the absolute values of B's gradient is less than the absolute value of A's gradient.

The below diagram illustrates the first of these scenarios.


There will only be one test that contains overlapping slopes, so consider it an extension activity worth very few marks.

b) Elephant Satellite :elephant: 
Remember that you can only complete one out of Part A and Part B!

ElephantSatellites have the following properties;

Moves at a linear speed of 2,500 kilometres (2,500,000 metres) per minute

Supports Desktops and Laptops only (along with other satellites, except for teleporting satellites)

Maximum range of 400,000 kilometres (400,000,000 metres)

Can store up to 90 bytes

Can receive 20 byte per minute and can send 20 byte per minute

When this satellite goes out of range of a device/satellite that is uploading a file, the ElephantSatellite doesn't delete the file but rather marks the file as transient, in which case it will continue the download from where it left off if the same device/satellite enters range again. Transient files don’t occupy any transfer bandwidth. File transience does not occur for files that are outgoing (transferring from the Elephant Satellite to another satellite). 

However, transient files are not held forever; if at any point memory becomes an issue (i.e. there is not enough space for new transfers) it will delete transient files (that aren't currently​ transferring) to make room.

This becomes an optimisation problem known as a knapsack problem. An Elephant Satellite has a set of transient files, each with a total length of N bytes and a value being the bytes already transferred. You want to determine which files to include so that the total bytes is less than the available space and maximise the bytes already transferred.

5. Other Requirements 🔭
You do not need to account for invalid input of any sort (e.g. device/satellite names with spaces, negative heights) and thus do NOT need to check for any invalid input.

We will never give you two satellites/devices with the same ID.

When dealing with degrees vs radians don't just convert all degrees to radians by hand and then manually put inprecise doubles as constants. Good design is able to cope with varying units to maintain precision. You will lose design/style marks for just hardcoding the conversions.

Most angles are given as radians.

All device ids are alphanumeric i.e. they consist of just alphabet characters and digits i.e. A-Z, a-z, 0-9, or .

All floating point (double) values only have to be accurate to a precision of 0.01. i.e. 3.33 and 3.34 are both 'equal' in any test we'll be running. You do NOT need to worry about rounding/formatting them in your code. We'll design test cases such that floating point accuracy issues aren't a problem.

All satellites travel clockwise (exception being Relay Satellites which can travel in both directions, and Teleporting Satellites which begin by travelling anticlockwise) angles are measured from the x-axis, so this means their angle should 'decrease' over time.

You should ONLY refer to positions in the range [0, 360) that is any value that is any value >= 360 or < 0 should be wrapped back around i.e. 360 = 0, 361 = 1, 390 = 30, 720 = 0, -1 = 359, -360 = 0...

You may use any of the following Java libraries:

java.io

java.lang

java.math

java.net

java.nio

java.rmi

java.security

java.text

java.time

java.util

org.junit.jupiter (for tests only)

Most likely however quite a few of these libraries you'll never use. You may not use any libraries other than these in your assignment.

6. Design 🏛️
6.1 UML Diagram
You will need to identify entities, attributes and functions within the problem domain and articulate them in a UML class diagram. The UML diagram will need to contain all key elements, including fields, methods, getters and setters, constructors for each entity, inheritance, aggregation and composition relationships and cardinalities.

Put your UML diagram in a file named design.pdf in the root directory of your repository.

Your UML Diagram and blog posts will be used in the assessment of your design. Your blog posts will also be marked (see marking criteria).

Good tools to draw UML Diagrams are lucid or draw.io. Both of these can export to pdf.

Do not submit an autogenerated UML diagram (via IntelliJ or any other tool). You will need to complete your UML diagram yourself.

6.2 Blogging
As you design your solution, you will have to make a series of design decisions based on Design Principles and Object-Oriented Programming concepts taught in the course. Document all of your key decisions and the reasoning behind them inside a private blog post(s) on Confluence. This can include your thought processes and internal reasoning, advice from your tutor/forum on design decisions and course content (lectures, tutorials, labs).

You are encouraged to make multiple blog posts (two or three, maybe once for each week of work on the assignment) to showcase your progression and evolution of design thinking as you work on the assignment. In your final blog post you will also need to briefly write about how you think you went overall in the assignment, the challenges you faced and what you learned completing the tasks.

Put links to all your post(s) inside blog.md in your repository.

6.3 Interview
During your Week 5 or your Week 7 lab, you will have a short (5 minutes) interview with your tutor regarding the design of your solution.

You will not be directly assessed on this interview - it is an opportunity for your tutor to give you some initial feedback on your design, and demonstrate an understanding of your solution.

During the interview, you will go through:

Your UML diagram, the key entities and relationships present in your domain model; and

Some of the code in your solution.

You will not be able to take any notes into the interview, as you should be familiar enough with your solution to speak to it in depth. Your tutor will lead the interview and give you prompts for you to talk about your design. You can also consider the writing of your blog posts as preparation for the interview.

7. Testing and Dryruns 🧪
The example use cases of Tasks 1 and 2 are set up to run against your code in the src/test directory and are the dryrun for this assignment. There will not be any example tests provided for Task 3 as it is intended to be more challenging.

You will need to write your own additional tests.

8. Style and Documentation 🖌️
You will be assessed on your code style. Examples of things to focus on include:

Correct casing of variable, function and class names;

Meaningful variable and function names;

Readability of code and use of whitespace; and

Modularisation and use of helper functions where needed.

You are not required to use JavaDoc on all methods/classes, however if you feel that the clarity of any code/method would be improved by including a comment then you are welcome to add one.

9. Tips 💡
This problem is designed to require inheritance. So don't try to avoid it.

You should NOT store any response objects (i.e. FileInfoResponse or EntityInfoResponse) in your classes, pull the information out of those into ArrayLists/Fields/Classes. You can create it them in the classes though.

Task 3 is a test of how strong your design is, if you start writing a lot of specific class code in Blackout or similar classes (rather than pushing logic 'into' classes) it is probably an indication that you can cleanup some aspects of your design.

Try to refactor and clean up your code as you go rather than waiting until the end. One approach is to, for each task or subtask, implement it to get your tests passing as messily as you like, and then refactor and make it beautiful while keeping the tests passing. Repeat for the next step, and the next, and so on.

10. Submission 🧳
To submit, make a tag to show that your code at the current commit is ready for your submission using the command:


$ git tag -a submission -m "Assignment Submission for {YOUR_zID_HERE}"
$ git push origin submission
Or, you can create one via the GitLab website by going to Repository > Tags > New Tag. We will take your most recent commit on master as your submission during marking. Any commits on master after the deadline will be taken as a late submission.

Make sure you include your design.pdf and blog.md in your repository when you submit.

10.1 Late Penalties
The late penalty for the submission is the standard UNSW late penalty of a 5% per day reduction of the on-time assignment mark. For example, if the assignment would receive an on-time mark of 70% and was submitted 3 days late the actual mark would be 55%.

10.2 Extensions
Extensions are only granted in extenuating circumstances and must be approved through either Special Consideration, which needs to be submitted prior to the assignment deadline, or pre-arranged through an Equitable Learning Plan with Equitable Learning Services and the Course Authority. In all cases please email cs2511@cse.unsw.edu.au.

11. Marking Criteria ✅
Section

Subsection

Description

Section

Subsection

Description

Software Correctness (50%)

Task 1 (15%)

Your code will be run against a series of autotests to determine the correctness of your solution. You do not need to have completed Tasks 2/3 to receive full marks for Task 1.

 Task 2 (25%)

Your code will be run against a series of autotests to determine the correctness of your solution. You do not need to have completed Task 3 to receive full marks for Task 2.

Task 3 (10%)

Your code will be run against a series of autotests to determine the correctness of your solution.

Software Design (45%)

Modelling of Entities (15%)

Have all the appropriate entities been modelled as classes, or is data grouped arbitrarily objects/strings/arrays?

Do your inheritance relationships make logical sense?

Have you used interfaces vs abstract classes appropriately?

Are the aggregation and composition relationships and cardinalities shown on the UML logical and appropriate?

Is the UML diagram correctly formatted?

Coupling & Cohesion (20%)

Does the design have good cohesion?

Does the design minimise coupling?

Has data been encapsulated appropriately?

Has functionality been delegated to the appropriate classes and abstracted where needed?

Are there any redundant classes / data classes?

Are all classes single responsibility?

Is there a lot of logic in the main Blackout class(es) or is it split up?

Are the Law of Demeter and Liskov Substitution Principle obeyed?

Code Quality (5%)

Your code quality and style will be manually marked. See Section 8.

Half the marks in this section can be achieved by passing the linter in the repository CI.

Blogging (5%)

Do the blog post(s) show an evolution of design thinking?

Do the blog posts contain reflection and areas for improvement?

Do the blog posts demonstrate an understanding of OO design concepts and their application?

Software Testing (5%)

Are the tests logically structured?

Does the test code adhere to design and style principles?

Do the tests cover a range of possible cases?

12. Credits 🎥
The initial inspiration for this assignment was derived from a problem "GPS Blackout" sourced from NCSS Challenge (Advanced), 2016. Outside of purely superficial comparisons, the problems are completely distinct.
