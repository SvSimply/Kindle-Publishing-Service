package com.amazon.ata.kindlepublishingservice.publishing;

import java.util.LinkedList;
import java.util.Queue;

public class BookPublishRequestManager {
    Queue<BookPublishRequest> requestQueue;

    public BookPublishRequestManager() {
        this.requestQueue = new LinkedList<>();
    }

    public void addBookPublishRequest(BookPublishRequest request) {
        this.requestQueue.add(request);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        if (this.requestQueue.isEmpty()) {
            return null;
        } else {
            return this.requestQueue.remove();
        }
    }
}
