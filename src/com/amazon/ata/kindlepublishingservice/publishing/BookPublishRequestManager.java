package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Queue;

public class BookPublishRequestManager {
    Queue<BookPublishRequest> requestQueue;

    @Inject
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
