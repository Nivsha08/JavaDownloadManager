package models;

public class ChunkManager {

    private Chunk[] chunkTable;

    /**
     * Creates an array of Chunks to track the downloaded data chunks.
     * @param fileSize - the total file size to be downloaded in bytes.
     * @param chunkSize - the constant chunk size in bytes.
     */
    public ChunkManager(long fileSize, long chunkSize) {
        int tableSize = (int)Math.ceil(fileSize / chunkSize);
        this.chunkTable = new Chunk[tableSize];
    }

    /* GETTERS & SETTERS */

    public int getChunksCount() {
        return this.chunkTable.length;
    }

    public Chunk getChunkAt(int index) {
        return this.chunkTable[index];
    }

    public void setChunkAt(int index, Chunk newChunk) {
        this.chunkTable[index] = newChunk;
    }

}
