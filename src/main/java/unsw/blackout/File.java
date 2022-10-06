package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String filename;
    private String content;
    private String data;
    private int size;
    private String fromId = null;
    private boolean transferCompleted;
    private int speed;
    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.data = content;
        this.size = content.length();
        this.transferCompleted = true;
    }

    public File(String filename, String content, String data, int size,
        boolean transferCompleted, String fromId, int speed) {
        this.filename = filename;
        this.content = content;
        this.data = data;
        this.size = size;
        this.transferCompleted = transferCompleted;
        this.fromId = fromId;
        this.speed = speed;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public boolean isTransferCompleted() {
        return transferCompleted;
    }
    public void setTransferCompleted(boolean transferCompleted) {
        this.transferCompleted = transferCompleted;
    }
    public String getFromId() {
        return fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public String teleportTransfer(String content) {
        this.content = content;
        this.data = content;
        this.size = content.length();
        this.transferCompleted = true;
        return fromId;
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public String updateProgress() {
        if (transferCompleted) return null;
        for (int i = 0; i < speed; i++) {
            char c = content.charAt(data.length());
            data += (String.valueOf(c));
            if (data.length() == content.length()) {
                transferCompleted = true;
                return fromId;
            }
        }
        return null;
    }
    public FileInfoResponse getFileInfo() {
        return new FileInfoResponse(filename, data, size, transferCompleted);
    }
}


