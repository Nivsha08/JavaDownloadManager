import java.util.ArrayList;
import java.util.List;

public class ChunkManager {

    private Chunk[] chunkTable;

    /**
     * Creates an array of Chunks to track the downloaded data chunks.
     * @param fileSize - the total file size to be downloaded in bytes.
     */
    public ChunkManager(long fileSize) {
        int tableSize = (int)Math.ceil((double)fileSize / Chunk.CHUNK_SIZE);
        this.chunkTable = new Chunk[tableSize];
    }

    /**
     * Creates an array of Chunks from a loaded MinifiedChunkTable object. The method will
     * create a completed Chunk object for each Chunk marked as completed in the minified table.
     * @param minifiedTable
     */
    public ChunkManager(MinifiedChunkTable minifiedTable) {
        this.chunkTable = new Chunk[minifiedTable.getChunkCount()];
        for (int i = 0; i < minifiedTable.getChunkCount(); i++) {
            boolean chunkStatus = minifiedTable.getElementAt(i);
            if (chunkStatus) {
                this.setChunkAt(i, new Chunk(i, true));
            }
        }
    }

    /**
     * @returns an integer array containing all the IDs (indices) of the completed Chunks.
     */
    public int[] getCompletedChunksIDs() {
        List<Integer> completedIDs = new ArrayList<>();
        for (int i = 0; i < chunkTable.length; i++) {
            if (chunkTable[i] != null) completedIDs.add(i);
        }
        return completedIDs.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * @returns an integer array containing all the IDs (indices) of the Chunks which are yet to be downloaded.
     */
    public int[] getRemainingChunkIDs() {
        List<Integer> remainingIDs = new ArrayList<>();
        for (int i = 0; i < chunkTable.length; i++) {
            if (chunkTable[i] == null) remainingIDs.add(i);
        }
        return remainingIDs.stream().mapToInt(Integer::intValue).toArray();
    }

    /* GETTERS & SETTERS */

    public int getChunksCount() {
        return chunkTable.length;
    }

    public Chunk getChunkAt(int index) {
        return chunkTable[index];
    }

    public void setChunkAt(int index, Chunk newChunk) {
        chunkTable[index] = newChunk;
    }

}
