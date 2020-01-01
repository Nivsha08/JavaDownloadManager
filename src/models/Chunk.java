package models;

public class Chunk implements Comparable<Chunk> {

    public static final int CHUNK_SIZE = 64000; // chunk size in bytes
    private byte[] data;
    private ChunkRange range;
    private boolean isCompleted;

    /**
     * Creates a chunk object containing {@CHUNK_SIZE} bytes of data, and
     * a status indicating whether it was downloaded or not.
     * @param chunkData
     */
    public Chunk(byte[] chunkData, ChunkRange range) {
        this.data = chunkData;
        this.range = range;
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

    public long getStartPosition() {
        return this.range.start();
    }

    public long getSize() { return this.data.length; }

    @Override
    public int compareTo(Chunk o) {
        return (int)(this.getStartPosition() - o.getStartPosition());
    }
}
