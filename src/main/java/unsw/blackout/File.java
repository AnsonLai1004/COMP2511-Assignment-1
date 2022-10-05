package unsw.blackout;

import unsw.response.models.FileInfoResponse;

public class File {
    private String filename;
    private String content;
    private int size;
    private String fromId = null;
    private boolean transferCompleted;
    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.transferCompleted = true;
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

    public FileInfoResponse getFileInfo() {
        return new FileInfoResponse(filename, content, size, transferCompleted);
    }
}


