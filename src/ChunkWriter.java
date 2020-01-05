import java.io.*;
import java.util.concurrent.PriorityBlockingQueue;

public class ChunkWriter implements Runnable {

    private PriorityBlockingQueue<Chunk> chunkQueue;
    private ChunkManager chunkManager;
    private String destFolder;
    private MetadataManager metadataManager;
    private RandomAccessFile writer;
    private DownloadStatus downloadStatus;

    /**
     * Initializing the program writer thread.
     * @param destinationFolderPath - the destination folder to save the downloaded file in.
     * @param fileName - the downloaded file's name.
     * @param chunkQueue - the chunk queue to take chunks from.
     * @param chunkManager
     */
    public ChunkWriter(String destinationFolderPath, String fileName,
                       PriorityBlockingQueue<Chunk> chunkQueue, ChunkManager chunkManager, MetadataManager metadataManager, DownloadStatus downloadStatus) {
        this.chunkQueue = chunkQueue;
        this.chunkManager = chunkManager;
        this.destFolder = destinationFolderPath;
        this.metadataManager = metadataManager;
        String destFilePath = this.destFolder + "/" + fileName;
        this.initWriter(destFilePath);
        this.downloadStatus = downloadStatus;
    }

    /**
     * Creating a RandomAccessFile object to handle the file writing.
     * @param destFilePath - the downloaded file destination path.
     */
    private void initWriter(String destFilePath) {
        File destFile = new File(destFilePath);
        try {
            if (destFile.exists()) {
                this.writer = new RandomAccessFile(destFile, "rw");
            }
            else {
                this.writer = new RandomAccessFile(destFilePath, "rw");
                this.writer.setLength(0);
            }
            this.writer.seek(0);
        }
        catch (FileNotFoundException e) {
            ProgramPrinter.printError("Unable to create destination file: file not found", e);
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
        while (!this.downloadStatus.isCompleted()) {
            c = this.chunkQueue.poll();
            if (c != null)
                writeChunkToFile(c);
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
                this.writer.seek(c.getStartPosition());
                this.writer.write(c.getData());
                this.downloadStatus.addCompletedBytes(c.getSize());
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
        metadataManager.save(chunkManager);
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
            this.writer.close();
        }
        catch (IOException e) {
            ProgramPrinter.printError("Unable to properly close writer.", e);
        }
    }

}
