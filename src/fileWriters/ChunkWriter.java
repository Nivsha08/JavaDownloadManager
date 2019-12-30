package fileWriters;

import models.Chunk;
import models.ChunkQueue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class ChunkWriter implements Runnable {

    private ChunkQueue chunkQueue;
    private String destFolder;
    private RandomAccessFile writer;

    public ChunkWriter(String destinationFolderPath, String fileName, ChunkQueue chunkQueue) {
        this.chunkQueue = chunkQueue;
        this.destFolder = destinationFolderPath;
        String destFilePath = this.destFolder + "/" + fileName;
        this.initWriter(destFilePath);
    }

    private void initWriter(String destFilePath) {
        try {
            this.writer = new RandomAccessFile(destFilePath, "rw");
            this.writer.setLength(0);
            this.writer.seek(0);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (this.chunkQueue.gotProducers()) {
            Chunk c = this.chunkQueue.poll();
            if (c != null) {
                System.out.println("writes: " + c.getStartPosition());
                this.writeChunkToFile(c);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        this.closeWriter();
    }

    private void closeWriter() {
        System.out.println("closing writer");
        try {
            this.writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeChunkToFile(Chunk c) {
        synchronized (this) {
            try {
                this.writer.seek(c.getStartPosition());
                this.writer.write(c.getData());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
