package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChunkManager implements Serializable {

    private Chunk[] chunkTable;

    /**
     * Creates an array of Chunks to track the downloaded data chunks.
     * @param fileSize - the total file size to be downloaded in bytes.
     */
    public ChunkManager(long fileSize) {
        int tableSize = (int)Math.ceil((double)fileSize / Chunk.CHUNK_SIZE);
        this.chunkTable = new Chunk[tableSize];
    }

    public ChunkManager(MinifiedChunkTable minified) {
        this.chunkTable = new Chunk[minified.getChunkCount()];
        for (int i = 0; i < minified.getChunkCount(); i++) {
            boolean chunkStatus = minified.getElementAt(i);
            if (chunkStatus) {
                this.setChunkAt(i, new Chunk(i, chunkStatus));
            }
        }
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

    public int[] getCompletedChunksIDs() {
        List<Integer> completedIDs = new ArrayList<>();
        for (int i = 0; i < chunkTable.length; i++) {
            if (chunkTable[i] != null) completedIDs.add(i);
        }
        return completedIDs.stream().mapToInt(Integer::intValue).toArray();
    }

    public int[] getRemainingChunkIDs() {
        List<Integer> remainingIDs = new ArrayList<>();
        for (int i = 0; i < chunkTable.length; i++) {
            if (chunkTable[i] == null) remainingIDs.add(i);
        }
        return remainingIDs.stream().mapToInt(Integer::intValue).toArray();
    }

    public String showData() {
        StringBuilder str = new StringBuilder();
        for (Chunk c : chunkTable) {
            if (c != null) {
                str.append(String.format("Chunk\t%d:\t%s\n", c.getID(), c.isCompleted()));
            }
        }
        return str.toString();
    }

}
