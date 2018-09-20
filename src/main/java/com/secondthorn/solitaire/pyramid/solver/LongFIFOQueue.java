package com.secondthorn.solitaire.pyramid.solver;

import java.util.NoSuchElementException;

/**
 * A FIFO Queue for primitive (unboxed) longs.  This is basically like
 * ArrayDeque but hardcoded to just handle longs and only provide
 * enqueue/dequeue/isEmpty methods.
 */
public class LongFIFOQueue {
    private long[] elements;
    private int head;
    private int tail;

    /**
     * Create a FIFO queue for primitive longs.
     */
    public LongFIFOQueue() {
        this(128);
    }

    /**
     * Create a FIFO queue for primitive longs big enough to hold the capacity
     * without having to resize.  It will still grow if necessary later on.
     */
    public LongFIFOQueue(int capacity) {
        int size = Integer.max(16, nextPowerOf2(capacity - 1));
        elements = new long[size];
        head = 0;
        tail = 0;
    }

    /**
     * Add an item to the rear of the queue.
     */
    void enqueue(long item) {
        elements[tail] = item;
        tail = (tail + 1) & (elements.length - 1);
        if (tail == head)
            doubleCapacity();
    }

    /**
     * Return true if the queue is empty.
     */
    public boolean isEmpty() {
        return head == tail;
    }

    /**
     * Remove an item from the front of the queue.
     */
    public long dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        long item = elements[head];
        head = (head + 1) & (elements.length - 1);
        return item;
    }

    private static int nextPowerOf2(int num) {
        num |= (num >>> 1);
        num |= (num >>> 2);
        num |= (num >>> 4);
        num |= (num >>> 8);
        num |= (num >>> 16);
        num++;
        if (num < 0)
            num >>>= 1;
        return num;
    }

    private void doubleCapacity() {
        int p = head;
        int n = elements.length;
        int r = n - p;
        int newCapacity = n << 1;
        if (newCapacity < 0)
            throw new IllegalStateException("LongFIFOQueue can't grow any further");
        long[] a = new long[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = a;
        head = 0;
        tail = n;
    }
}
