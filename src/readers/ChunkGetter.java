package readers;

import ioHandlers.ProgramPrinter;
import models.*;
import java.io.*;
import java.net.*;

public class ChunkGetter implements Runnable {

    private final int BYTE_BUFFER_SIZE = 4096; // byte buffer size used when reading content
    private final String REQUEST_TYPE = "Range"; // HTTP range request method name

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
    public ChunkGetter(String address, ChunkRange range, int chunkIndex,
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
        HttpURLConnection connection = this.initConnection();
        byte[] downloadedData = this.downloadChunk(connection);
        this.saveDownloadedData(downloadedData);
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
        try {
            connection.setRequestProperty(REQUEST_TYPE, byteRange);
            InputStream connectionInputStream = connection.getInputStream();
            return this.readByteRange(connectionInputStream);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Failed to read the source file input stream.", e);
        }
        finally {
            connection.disconnect();
        }

        return null;
    }

    /**
     * Reads the entire Http range into a byte array from the given input stream.
     * @param connectionInputStream - the file's input stream.
     * @return a byte array with the downloaded data.
     */
    private byte[] readByteRange(InputStream connectionInputStream) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] chunkBuffer = new byte[BYTE_BUFFER_SIZE];
        int bytesRead;

        try {
            while ((bytesRead = connectionInputStream.read(chunkBuffer)) > 0) {
                out.write(chunkBuffer, 0, bytesRead);
            }
        }
        catch (IOException e) {
            ProgramPrinter.printError("Failed to read from the source file.", e);
        }

        return out.toByteArray();
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
