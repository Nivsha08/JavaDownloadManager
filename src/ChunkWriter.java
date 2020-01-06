import java.io.*;
import java.util.concurrent.PriorityBlockingQueue;

public class ChunkWriter implements Runnable {

    private PriorityBlockingQueue<Chunk> chunkQueue;
    private ChunkManager chunkManager;
    private String destinationPath;
    private MetadataManager metadataManager;
    private RandomAccessFile writer;
    private DownloadStatus downloadStatus;

    /**
     * Initializing the program writer thread.
     * @param destinationFilePath - the path of the output file.
     * @param chunkQueue - the chunk queue to dequeue chunks from.
     * @param metadataManager - the metadata manager object to serialize with.
     * @param chunkManager - the chunk manager to serialize after writing.
     * @param downloadStatus - the download status object to update after writing.
     */
    public ChunkWriter(String destinationFilePath, PriorityBlockingQueue<Chunk> chunkQueue,
                       MetadataManager metadataManager, ChunkManager chunkManager, DownloadStatus downloadStatus) {
        this.chunkQueue = chunkQueue;
        this.chunkManager = chunkManager;
        this.destinationPath = destinationFilePath;
        this.metadataManager = metadataManager;
        this.downloadStatus = downloadStatus;
        initWriter(destinationFilePath);
    }

    /**
     * Creating a RandomAccessFile object to handle the file writing.
     * If the output file already exists, open it and continue writing to it.
     * Otherwise, a new file will be created.
     * @param destFilePath - the downloaded file destination path.
     */
    private void initWriter(String destFilePath) {
        File destFile = new File(destFilePath);
        try {
            if (destFile.exists()) {
                writer = new RandomAccessFile(destFile, "rw");
            }
            else {
                writer = new RandomAccessFile(destFilePath, "rw");
                writer.setLength(0); // ensuring the file is empty before start writing.
            }
        }
        catch (FileNotFoundException e) {
            ProgramPrinter.printError("Unable to create destination file: invalid path.", e);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Unable to write to destination file.", e);
        }
    }

    /**
     * Starting the ChunkWriter thread.
     * The writer iteratively fetches a chunk from the {@chunkQueue}, and write it
     * to the destination file.
     */
    @Override
    public void run() {
        Chunk c;
        while (!downloadStatus.isCompleted()) {
            c = chunkQueue.poll();
            if (c != null) {
                writeChunkToFile(c);
            }
        }
        handleWritingCompletion();
    }

    /**
     * Synchronously writing the given chunk's data to the file, in it's correct position.
     * @param c - Chunk to be written to file.
     */
    private void writeChunkToFile(Chunk c) {
        synchronized (this) {
            try {
                writer.seek(c.getStartPosition());
                writer.write(c.getData());
            }
            catch (IOException e) {
                ProgramPrinter.printError("Failed to write data portion to file.", e);
            }
            flagChunkAsCompleted(c);
        }
    }

    /**
     * Mark the given Chunk as successfully written to disk, and add it to the metadata file.
     * @param c - the completed Chunk.
     */
    private void flagChunkAsCompleted(Chunk c) {
        c.setStatus(true);
        downloadStatus.addCompletedBytes(c.getSize());
        metadataManager.save(chunkManager);
        c.clearData();
    }

    /**
     * Finalizes the download process after all the Chunk written to disk.
     */
    private void handleWritingCompletion() {
        closeWriter();
        metadataManager.clearFiles();
        downloadStatus.handleDownloadSuccess();
    }

    /**
     * Frees all the resources used by this object.
     */
    private void closeWriter() {
        try {
            writer.close();
        }
        catch (IOException e) {
            ProgramPrinter.printError("Unable to properly close writer.", e);
        }
    }

}
