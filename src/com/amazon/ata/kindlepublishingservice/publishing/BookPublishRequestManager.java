package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public final class BookPublishRequestManager {
    private final Queue<BookPublishRequest> requestQueue;

    @Inject
    public BookPublishRequestManager() {
        this.requestQueue = new ConcurrentLinkedQueue<>();
    }

    public void addBookPublishRequest(final BookPublishRequest request) {
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
