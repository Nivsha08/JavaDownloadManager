public class Chunk {

    public static final long CHUNK_SIZE = 64000; // chunk size in bytes
    private byte[] data;
    private boolean isCompleted;


    public Chunk(byte[] chunkData) {
        this.data = chunkData;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setStatus(boolean newStatus) {
        this.isCompleted = newStatus;
    }
}
