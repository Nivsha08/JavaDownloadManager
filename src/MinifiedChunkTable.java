import java.io.Serializable;

public class MinifiedChunkTable implements Serializable {

    private boolean[] chunks;

    public MinifiedChunkTable(ChunkManager chunkManager) {
        this.chunks = new boolean[chunkManager.getChunksCount()];
        for (int i = 0; i < chunkManager.getChunksCount(); i++) {
            Chunk currentChunk = chunkManager.getChunkAt(i);
            if (currentChunk != null) {
                chunks[i] = currentChunk.isCompleted();
            }
            else {
                chunks[i] = false;
            }
        }
    }

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
