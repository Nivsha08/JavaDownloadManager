package models;

public class ChunkRange {

    private long fileSize;
    private int totalChunkCount;
    private int chunkIndex;
    private long startPosition;
    private long endPosition;

    /**
     * Creates a ChunkRange object.
     * @param chunkIndex - the index of the chunk.
     * @param fileSize - the total file size to be downloaded.
     * @param totalChunkCount - the total number of chunks in the file.
     */
    public ChunkRange(int chunkIndex, long fileSize, int totalChunkCount) {
        this.totalChunkCount = totalChunkCount;
        this.chunkIndex = chunkIndex;
        this.fileSize = fileSize;
        this.calculateRange();
    }

    /**
     * Sets the range positions according to the chunk size. If the chunk is the last chunk
     * of the file, the function calculates the reminder size to avoid writing excess bytes.
     */
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

    /**
     * @returns true iff this is the last chunk of the file, false otherwise.
     */
    private boolean lastChunk() {
        return (this.chunkIndex == this.totalChunkCount - 1);
    }

    /**
     * @returns the byte range in a format suitable to a HTTP Range request.
     */
    public String httpByteRange() {
        return String.format("bytes=%d-%d", this.startPosition, this.endPosition);
    }

    /* GETTERS & SETTERS */

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