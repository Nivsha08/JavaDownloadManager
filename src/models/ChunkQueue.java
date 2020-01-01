package models;

import java.util.concurrent.PriorityBlockingQueue;

public class ChunkQueue extends PriorityBlockingQueue<Chunk> {

    public ChunkQueue(int queueCapacity) {
        super(queueCapacity);
    }
}
