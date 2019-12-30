package models;

import java.util.concurrent.SynchronousQueue;

public class ChunkQueue extends SynchronousQueue<Chunk> {

    private int producers = 0;

    public ChunkQueue() {}

    public boolean gotProducers() {
        return (this.producers > 0);
    }

    public void registerProducer() {
        synchronized (this) {
            this.producers++;
        }
    }

    public void unregisterProducer() {
        synchronized (this) {
            this.producers--;
        }
    }
}
