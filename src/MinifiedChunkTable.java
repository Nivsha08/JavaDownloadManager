import java.io.Serializable;

/**
 * An minified representation of the ChunkManager object.
 * A MinifiedChunkTable is practically a bitmap representing the completed chunks of the source file.
 */
public class MinifiedChunkTable implements Serializable {

    private boolean[] chunks;

    /**
     * Creates a minified representation for the given ChunkManager object.
     * @param chunkManager
     */
    public MinifiedChunkTable(ChunkManager chunkManager) {
        this.chunks = new boolean[chunkManager.getChunksCount()];
        for (int i = 0; i < chunkManager.getChunksCount(); i++) {
            Chunk currentChunk = chunkManager.getChunkAt(i);
            chunks[i] = (currentChunk != null) ? currentChunk.isCompleted() : false;
        }
    }

    /* GETTERS & SETTERS */

    public int getChunkCount() {
        return this.chunks.length;
    }

    public boolean[] getTable() {
        return chunks;
    }

    public boolean getElementAt(int i) {
        return chunks[i];
    }
}
