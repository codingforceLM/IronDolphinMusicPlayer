package de.codingforcelm.idmp.structure.queue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.codingforcelm.idmp.music.Song;

public class SongQueue {

    private static volatile SongQueue INSTANCE;
    private LinkedList<String> queue;
    private OnQueueChangedListener listener;

    protected SongQueue() {
        queue = new LinkedList<>();
    }

    public static SongQueue getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SongQueue();
        }
        return INSTANCE;
    }

    public void enqueue(String id) {
        queue.add(id);
        if(listener != null) {
            listener.onQueueChanged(queue);
        }
    }

    public String dequeue() {
        String item = queue.remove();
        if(listener != null) {
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
