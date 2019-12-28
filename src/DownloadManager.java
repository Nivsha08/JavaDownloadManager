import inputHandlers.ProgramInput;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadManager {

    private ThreadPoolExecutor threadPool;
    private ArrayList<String> serverList;

    /**
     * Initialize a thread pool with the given <n> </n>number of connections.
     * Creates an array of Chunks objects, initially empty.
     * Initializing <n> threads to handle different areas of the array.
     * @param userInput
     */
    public DownloadManager(ProgramInput userInput) {
        this.initDownloadManager(userInput);
        // calculate file size
        long fileSize = 200000;
        this.initChunkManager(fileSize);

    }

    private void initDownloadManager(ProgramInput userInput) {
        int maxConnections = userInput.getMaxConnections();
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxConnections);
        this.serverList = userInput.getServerList();
    }

    private ChunkManager initChunkManager(long fileSize) {
        return new ChunkManager(fileSize, Chunk.CHUNK_SIZE);
    }

}




//for (chunkTable) {
//
//    downaload(i)
//        }
//
//data = getData(start: i * chunk size, i * chunksize + chunksize)
//table[i] = new Chunk(data)