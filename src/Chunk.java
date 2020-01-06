public class Chunk implements Comparable<Chunk> {

    public static final int CHUNK_SIZE = 128000; // chunk size in bytes
    private int chunkID;
    private byte[] data;
    private ChunkRange range;
    private boolean isCompleted;

    /**
     * Creating a chunk object with the given ID and status.
     * @param chunkID
     * @param isCompleted
     */
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
     * @param range - ChunkRange object to indicate the range to download.
     */
    public Chunk(int chunkID, byte[] chunkData, ChunkRange range) {
        this.chunkID = chunkID;
        this.isCompleted = false;
        this.data = chunkData;
        this.range = range;
    }

    /**
     * Enables the program to clear data which was already successfully written to disk.
     */
    public void clearData() {
        data = null;
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
