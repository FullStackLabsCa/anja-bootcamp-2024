package io.reactivestax.problems.producerconsumer;

import java.util.LinkedList;
import java.util.Queue;

public class SharedBuffer {
    private final Queue<String> queue;
    private final int capacity;

    public SharedBuffer(int size) {
        this.queue = new LinkedList<>();
        this.capacity = size;
    }

    public void addToBuffer(String str) {
        this.queue.add(str);
    }

    public boolean checkBufferFull() {
        return this.queue.size() == capacity;
    }

    public String getValueFromBuffer() {
        return this.queue.poll();
    }

    public boolean checkBufferEmpty(){
        return this.queue.isEmpty();
    }
}
