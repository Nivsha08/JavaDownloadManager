package readers;

import ioHandlers.ProgramPrinter;
import models.*;
import java.io.*;
import java.net.*;

public class HTTPRangeGetter implements Runnable {

    private final String REQUEST_TYPE = "Range";
    private String serverAddress;
    private int chunkIndex;
    private ChunkRange range;
    private ChunkManager chunkManager;
    private ChunkQueue chunkQueue;

    /**
     * Initializes a HTTP getters object.
     * @param address - the server's URL address from which the getter will download.
     * @param range - ChunkRange object to hold the download range for this getter.
     * @param chunkIndex - the index of the chunk to be downloaded by this getter.
     * @param chunkManager - reference to object tracking the chunk downloaded.
     * @param chunkQueue - reference to synchronous queue handling completed chunks.
     */
    public HTTPRangeGetter(String address, ChunkRange range, int chunkIndex,
                           ChunkManager chunkManager, ChunkQueue chunkQueue) {
        this.serverAddress = address;
        this.chunkIndex = chunkIndex;
        this.range = range;
        this.chunkManager = chunkManager;
        this.chunkQueue = chunkQueue;
    }

    /**
     * Start downloading the chunk allocated for this getter.
     */
    @Override
    public void run() {
//        System.out.println("getter downloading chunk number: " + this.chunkIndex);
        this.chunkQueue.registerProducer();
        HttpURLConnection connection = this.initConnection();
//        System.out.println("chunk number " + this.chunkIndex + " downloads:" + this.range.start() + "-" + this.range.end());
        byte[] downloadedData = this.downloadChunk(connection);
        this.saveDownloadedData(downloadedData);
        this.chunkQueue.unregisterProducer();
    }

    /**
     * Establishing a HTTP connection to the server for downloading the data.
     * @return a HTTP connection to the server.
     */
    private HttpURLConnection initConnection() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.serverAddress);
            connection = (HttpURLConnection) url.openConnection();
        }
        catch (MalformedURLException e) {
            ProgramPrinter.printError("Invalid URL address given as input.", e);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Unable to establish the URL connection.", e);
        }
        return connection;
    }

    /**
     * Establish an HTTP connection and start downloading chunk
     * number <chunkIndex> of the entire file.
     * @param connection - the HTTP connection to use for downloading the data.
     * @return A byte array with the downloaded data.
     */
    private byte[] downloadChunk(HttpURLConnection connection) {
        String byteRange = this.range.httpByteRange();
        connection.setRequestProperty(REQUEST_TYPE, byteRange);

        try {
            InputStream connectionInputStream = connection.getInputStream();
            return this.readByteRange(connectionInputStream);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Failed to read the source file input stream.", e);
        }

        return null;
    }

    /**
     * Reads CHUNK_SIZE bytes into a byte array from the given input stream.
     * @param connectionInputStream - the file's input stream.
     * @return a byte array with the downloaded data.
     */
    private byte[] readByteRange(InputStream connectionInputStream) {
        int rangeSize = (int)this.range.size();
        byte[] result = new byte[rangeSize];

        try {
            BufferedInputStream reader = new BufferedInputStream(connectionInputStream);
            reader.read(result, 0, rangeSize);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Failed to read from the source file.", e);
        }

        return result;
    }

    /**
     * Creates a new Chunk object with the given downloaded data,
     * stores it at the chunks table in the correct index, and adding it
     * to the queue of the chunks waiting to be written to file.
     * @param downloadedData - the data downloaded by this getters.
     */
    private void saveDownloadedData(byte[] downloadedData) {
        try {
            Chunk c = new Chunk(downloadedData, this.range);
            this.chunkManager.setChunkAt(this.chunkIndex, c);
            this.chunkQueue.put(c);
            Thread.sleep(200); //todo: document
        }
        catch (InterruptedException e) {
            ProgramPrinter.printError(
                    "Failed to store a portion of the data downloaded from the source file.", e);
        }
    }

}
