package models;

public class ChunkRange {

    private long fileSize;
    private int totalChunkCount;
    private int chunkIndex;

    private long startPosition;
    private long endPosition;

    public ChunkRange(int chunkIndex, long fileSize, int totalChunkCount) {
        this.totalChunkCount = totalChunkCount;
        this.chunkIndex = chunkIndex;
        this.fileSize = fileSize;
        this.calculateRange();
    }

    private void calculateRange() {
        if (!this.lastChunk()) {
            this.startPosition = chunkIndex * Chunk.CHUNK_SIZE;
            this.endPosition = this.startPosition + Chunk.CHUNK_SIZE - 1;
        }
        else {
            this.startPosition = chunkIndex * Chunk.CHUNK_SIZE;
            this.endPosition = this.startPosition + (fileSize % Chunk.CHUNK_SIZE);
        }
    }

    private boolean lastChunk() {
        return (this.chunkIndex == this.totalChunkCount - 1);
    }

    public long start() {
        return startPosition;
    }

    public long end() {
        return endPosition;
    }

    public long size() {
        return this.endPosition - this.startPosition;
    }
}
