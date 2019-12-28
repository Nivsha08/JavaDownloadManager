package models;

public class Chunk {

    public static final int CHUNK_SIZE = 64000; // chunk size in bytes
    private byte[] data;
    private boolean isCompleted;

    /**
     * Creates a chunk object containing {@CHUNK_SIZE} bytes of data, and
     * a status indicating whether it was downloaded or not.
     * @param chunkData
     */
    public Chunk(byte[] chunkData) {
        this.data = chunkData;
    }

    /* GETTERS & SETTERS */

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
