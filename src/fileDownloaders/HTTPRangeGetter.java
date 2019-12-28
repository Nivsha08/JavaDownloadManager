package fileDownloaders;

public class HTTPRangeGetter implements Runnable {

    private int chunkIndex;

    public HTTPRangeGetter(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    @Override
    public void run() {
        System.out.println("getter downloading chunk number: " + this.chunkIndex);
        // 1. Download chunk number <chunkIndex>
        // 2. Create Chunk object with the downloaded data
        // 3. Add chunk to ChunkQueue

    }
}
