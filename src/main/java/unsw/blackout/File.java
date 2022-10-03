package unsw.blackout;

public class File {
    private String filename;
    private String content;
    private int size;
    public File(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.size = content.length();
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
}


