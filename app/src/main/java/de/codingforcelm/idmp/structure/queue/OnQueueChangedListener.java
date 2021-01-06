package de.codingforcelm.idmp.structure.queue;

import java.util.Queue;

public interface OnQueueChangedListener {
    void onQueueChanged(Queue<String> queue);
}
