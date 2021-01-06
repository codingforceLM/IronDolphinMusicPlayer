package de.codingforcelm.idmp.structure.queue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import de.codingforcelm.idmp.music.Song;

public class SongQueue {

    private static volatile SongQueue INSTANCE;

    private Queue<String> queue;

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
    }

    public String dequeue() {
        return queue.remove();
    }

    public Iterator<String> iterator() {
        return queue.iterator();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
