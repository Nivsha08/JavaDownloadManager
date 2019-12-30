package writers;

import ioHandlers.ProgramPrinter;
import models.Chunk;
import models.ChunkQueue;
import models.DownloadStatus;

import java.io.*;

public class ChunkWriter implements Runnable {

    private ChunkQueue chunkQueue;
    private String destFolder;
    private RandomAccessFile writer;
    private DownloadStatus downloadStatus;

    /**
     * Initializing the program writer thread.
     * @param destinationFolderPath - the destination folder to save the downloaded file in.
     * @param fileName - the downloaded file's name.
     * @param chunkQueue - the chunk queue to take chunks from.
     */
    public ChunkWriter(String destinationFolderPath, String fileName,
                       ChunkQueue chunkQueue, DownloadStatus downloadStatus) {
        this.chunkQueue = chunkQueue;
        this.destFolder = destinationFolderPath;
        String destFilePath = this.destFolder + "/" + fileName;
        this.initWriter(destFilePath);
        this.downloadStatus = downloadStatus;
    }

    /**
     * Creating a RandomAccessFile object to handle the file writing.
     * @param destFilePath - the downloaded file destination path.
     */
    private void initWriter(String destFilePath) {
        try {
            this.writer = new RandomAccessFile(destFilePath, "rw");
            this.writer.setLength(0);
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
        while (this.chunkQueue.gotProducers()) {
            Chunk c = this.chunkQueue.poll();
            try {
                if (c != null) {
                    // System.out.println("writes: " + c.getStartPosition());
                    this.writeChunkToFile(c);
                }
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                ProgramPrinter.printError("Writer thread interrupted.", e);
            }
        }
        this.downloadStatus.handleDownloadSuccess();
        this.closeWriter();
    }

    /**
     * Frees all the resources used by this object.
     */
    private void closeWriter() {
        // System.out.println("closing writer");
        try {
            this.writer.close();
        }
        catch (IOException e) {
            ProgramPrinter.printError("Unable to properly close writer.", e);
        }
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
        }
    }
}
