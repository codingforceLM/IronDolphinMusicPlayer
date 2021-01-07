package de.codingforcelm.idmp.queue;

import java.util.Queue;

public interface OnQueueChangedListener {
    void onQueueChanged(Queue<String> queue);
}
