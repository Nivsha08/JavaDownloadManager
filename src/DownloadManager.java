import fileDownloaders.HTTPRangeGetter;
import inputHandlers.ProgramInput;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadManager {

    private ThreadPoolExecutor threadPool;
    private ArrayList<String> serverList;
    private ChunkManager chunkManager = null;
    private long fileSize;

    /**
     * Initialize a thread pool with the given <n> </n>number of connections.
     * Creates an array of Chunks objects, initially empty.
     * Initializing <n> threads to handle different areas of the array.
     * @param userInput
     */
    public DownloadManager(ProgramInput userInput) {
        this.initDownloadManager(userInput);
        this.initChunkManager(this.fileSize);
        this.initConnections();
    }

    private void initDownloadManager(ProgramInput userInput) {
        int maxConnections = userInput.getMaxConnections();
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxConnections);
        this.serverList = userInput.getServerList();
        this.fileSize = this.getFileSize();
    }

    private void initChunkManager(long fileSize) {
        this.chunkManager = new ChunkManager(fileSize, Chunk.CHUNK_SIZE);
    }

    private void initConnections() {
        int chunkCount = this.chunkManager.getChunksCount();

        for (int i = 0; i < chunkCount; i++) {
            HTTPRangeGetter getter = new HTTPRangeGetter(i);
            this.threadPool.execute(getter);
        }

        this.terminateConnections();
    }

    private long getFileSize() {
        long fileSize = 0;
        try {
            URL url = new URL(this.serverList.get(0));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            fileSize = connection.getContentLength();
            connection.disconnect();
        }
        catch (MalformedURLException e) {
            System.err.println("Download failed.\nInvalid URL address.");
            System.err.println(e);
        }
        catch (IOException e) {
            System.err.println("Download failed.\nUnable to calculate file size.");
            System.err.println(e);
        }

        return fileSize;
    }

    private void terminateConnections() {
        // maybe ensure download is completed before killing connections?
        this.threadPool.shutdown();
    }

}




//for (chunkTable) {
//
//    downaload(i)
//        }
//
//data = getData(start: i * chunk size, i * chunksize + chunksize)
//table[i] = new Chunk(data)