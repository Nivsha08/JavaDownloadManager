public class ChunkManager {

    private Chunk[] chunkTable;

    public ChunkManager(long fileSize, long chunkSize) {
        int tableSize = (int)Math.ceil(fileSize / chunkSize);
        this.chunkTable = new Chunk[tableSize];
    }

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
