package de.codingforcelm.idmp.queue;

import java.util.Queue;
/**
 * Interface to implement onQueueChanged function
 */
public interface OnQueueChangedListener {
    void onQueueChanged(Queue<String> queue);
}
