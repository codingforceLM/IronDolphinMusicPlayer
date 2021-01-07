package de.codingforcelm.idmp.queue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SongQueue {

    private static volatile SongQueue INSTANCE;
    private final LinkedList<String> queue;
    private OnQueueChangedListener listener;

    protected SongQueue() {
        queue = new LinkedList<>();
    }

    public static SongQueue getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SongQueue();
        }
        return INSTANCE;
    }

    public void enqueue(String id) {
        queue.add(id);
        if (listener != null) {
            listener.onQueueChanged(queue);
        }
    }

    public String dequeue() {
        String item = queue.remove();
        if (listener != null) {
            listener.onQueueChanged(queue);
        }
        return item;
    }

    public Iterator<String> iterator() {
        return queue.iterator();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public List<String> getListRepresentation() {
        return queue;
    }

    public void registerOnQueueChangedListener(OnQueueChangedListener listener) {
        this.listener = listener;
    }
}
