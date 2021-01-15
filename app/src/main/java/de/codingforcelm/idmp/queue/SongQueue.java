package de.codingforcelm.idmp.queue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a queue containing mediaIds
 */
public class SongQueue {

    private static volatile SongQueue INSTANCE;
    private final LinkedList<String> queue;
    private OnQueueChangedListener listener;

    /**
     * Default constructor
     */
    protected SongQueue() {
        queue = new LinkedList<>();
    }

    /**
     * Retrieve a SongQueue instance
     * @return instance
     */
    public static SongQueue getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SongQueue();
        }
        return INSTANCE;
    }

    /**
     * Place mediaId of song in queue
     * @param id mediaId
     */
    public void enqueue(String id) {
        queue.add(id);
        if (listener != null) {
            listener.onQueueChanged(queue);
        }
    }

    /**
     * Return head of the queue
     * @return
     */
    public String dequeue() {
        String item = queue.remove();
        if (listener != null) {
            listener.onQueueChanged(queue);
        }
        return item;
    }

    /**
     * Get the iterator of the queue
     * @return iterator
     */
    public Iterator<String> iterator() {
        return queue.iterator();
    }

    /**
     * Check if the queue is empty
     * @return true if empty
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Get the queue as a list
     * @return queue as list
     */
    public List<String> getListRepresentation() {
        return queue;
    }

    /**
     * Register the class which shall be notified when the queue changes
     * @param listener listener
     */
    public void registerOnQueueChangedListener(OnQueueChangedListener listener) {
        this.listener = listener;
    }
}
