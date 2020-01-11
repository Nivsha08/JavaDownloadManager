import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class ChunkGetter implements Runnable {

    private final int BYTE_BUFFER_SIZE = 4096; // buffer size (in bytes) used when reading a range content
    private final int CONNECTION_TIMEOUT = 2000; // max wait time (in ms) for trying to connect/read to/from a the server
    private final String REQUEST_TYPE = "Range"; // HTTP range request method name

    private int chunkIndex;
    private String serverAddress;
    private List<String> serverList;
    private ChunkRange range;
    private ChunkManager chunkManager;
    private PriorityBlockingQueue<Chunk> chunkQueue;
    private DownloadManager downloadManager;

    /**
     * Initializes a HTTP getters object.
     * @param serverList - the servers list of address.
     * @param range - ChunkRange object to hold the download range for this getter.
     * @param chunkIndex - the index of the chunk to be downloaded by this getter.
     * @param chunkManager - a reference to object tracking the chunk downloaded.
     * @param chunkQueue - a reference to priority queue handling chunks waiting to be written to disk.
     * @param downloadManager - a reference to the download manager object.
     */
    public ChunkGetter(List<String> serverList, ChunkRange range, int chunkIndex,
                       ChunkManager chunkManager, PriorityBlockingQueue<Chunk> chunkQueue,
                       DownloadManager downloadManager) {
        this.serverList = serverList;
        this.chunkIndex = chunkIndex;
        this.range = range;
        this.chunkManager = chunkManager;
        this.chunkQueue = chunkQueue;
        this.downloadManager = downloadManager;
    }

    /**
     * Start downloading the chunk allocated for this getter.
     */
    @Override
    public void run() {
        int threadID = (int)Thread.currentThread().getId();
        serverAddress =  calculateThreadServerAddress(threadID, serverList);
        HttpURLConnection connection = initConnection();
        byte[] downloadedData = downloadChunk(connection);
        saveDownloadedData(downloadedData);
    }

    /**
     * Determines to which server the given thread will connect.
     * @param threadID
     * @param serverList
     * @returns the server address' index in the server list.
     */
    private String calculateThreadServerAddress(int threadID, List<String> serverList) {
        int serverAddressIndex = threadID % serverList.size();
        return serverList.get(serverAddressIndex);
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
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);
        }
        catch (MalformedURLException e) {
            ProgramPrinter.printError("Invalid URL address given as input.", e);
        }
        catch (IOException e) {
            // suppressing connection or network interruptions errors and terminating the program
            downloadManager.interruptDownload();
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
            // suppressing connection or network interruptions errors and terminating the program
            downloadManager.interruptDownload();
        }
        finally {
            connection.disconnect();
        }
        return null;
    }

    /**
     * Reads the entire Http range into a byte array from the given input stream.
     * @param connectionInputStream - the file's input stream.
     * @returns a byte array with the downloaded data.
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
            // suppressing connection or network interruptions errors and terminating the program
            downloadManager.interruptDownload();
        }
        return out.toByteArray();
    }

    /**
     * Creates a new Chunk object with the given downloaded data,
     * stores it at the chunks table in the correct index, and adding it
     * to the queue of the chunks waiting to be written to disk.
     * @param downloadedData - the data downloaded by this getters.
     */
    private void saveDownloadedData(byte[] downloadedData) {
        Chunk c = new Chunk(chunkIndex, downloadedData, this.range);
        this.chunkManager.setChunkAt(this.chunkIndex, c);
        this.chunkQueue.put(c);
    }

}
