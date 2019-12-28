package fileDownloaders;

import models.*;
import java.io.*;
import java.net.*;

public class HTTPRangeGetter implements Runnable {

    private final String REQUEST_TYPE = "Range";
    private int chunkIndex;
    private String serverAddress;
    private ChunkManager chunkManager;

    /**
     * Initializes a HTTP getters object.
     * @param address - the server's URL address from which the getter will download.
     * @param chunkIndex - the index of the chunk to be downloaded by this getter.
     * @param chunkManager - reference to object tracking the chunk downloaded.
     */
    public HTTPRangeGetter(String address, int chunkIndex, ChunkManager chunkManager) {
        this.serverAddress = address;
        this.chunkIndex = chunkIndex;
        this.chunkManager = chunkManager;
    }

    /**
     * Start downloading the chunk allocated for this getter.
     */
    @Override
    public void run() {
        System.out.println("getter downloading chunk number: " + this.chunkIndex);
        HttpURLConnection connection = this.initConnection();
        byte[] downloadedData = this.downloadChunk(connection);
        this.saveDownloadedData(downloadedData);
        // 3. Add chunk to ChunkQueue
    }

    /**
     * Establishing a HTTP connection to the server for downloading the data.
     * @return
     */
    private HttpURLConnection initConnection() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.serverAddress);
            connection = (HttpURLConnection) url.openConnection();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
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
        long rangeStart = chunkIndex * Chunk.CHUNK_SIZE;
        long rangeEnd = rangeStart + Chunk.CHUNK_SIZE - 1;
        String byteRange = String.format("bytes=%d-%d", rangeStart, rangeEnd);
        connection.setRequestProperty(REQUEST_TYPE, byteRange);

        try {
            InputStream connectionInputStream = connection.getInputStream();
            return this.readByteRange(connectionInputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Reads CHUNK_SIZE bytes into a byte array from the given input stream.
     * @param connectionInputStream - the file's input stream.
     * @return a byte array with the downloaded data.
     */
    private byte[] readByteRange(InputStream connectionInputStream) {
        byte[] result = new byte[Chunk.CHUNK_SIZE];

        try {
            BufferedInputStream reader = new BufferedInputStream(connectionInputStream);
            reader.read(result, 0, Chunk.CHUNK_SIZE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Creates a new Chunk object with the given downloaded data, and
     * stores it at the chunks table in the correct index.
     * @param downloadedData - the data downloaded by this getters.
     */
    private void saveDownloadedData(byte[] downloadedData) {
        Chunk c = new Chunk(downloadedData);
        this.chunkManager.setChunkAt(this.chunkIndex, c);
    }
}
