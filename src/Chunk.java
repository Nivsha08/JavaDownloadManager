import java.io.Serializable;

public class Chunk implements Comparable<Chunk>, Serializable {

    public static final int CHUNK_SIZE = 64000; // chunk size in bytes
    private int chunkID;
    private byte[] data;
    private ChunkRange range;
    private boolean isCompleted;

    public Chunk(int chunkID, boolean isCompleted) {
        this.chunkID = chunkID;
        this.isCompleted = isCompleted;
        this.data = null;
        this.range = null;
    }

    /**
     * Creates a chunk object containing {@CHUNK_SIZE} bytes of data, and
     * a status indicating whether it was downloaded or not.
     * @param chunkData
     */
    public Chunk(int chunkID, byte[] chunkData, ChunkRange range) {
        this.chunkID = chunkID;
        this.isCompleted = false;
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

    public int getID() { return this.chunkID; }

    @Override
    public int compareTo(Chunk o) {
        return (int)(this.getStartPosition() - o.getStartPosition());
    }
}
