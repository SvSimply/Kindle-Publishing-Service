package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;

import javax.inject.Inject;

public final class BookPublishTask implements Runnable{
    private final BookPublishRequestManager requestManager;
    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;

    @Inject
    public BookPublishTask(BookPublishRequestManager requestManager, PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.requestManager = requestManager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
//        System.out.println("I'm in BookPublishTask constructor");
    }

    @Override
    public void run() {
        // retrieve request from queue
//        System.out.println("I'm in BookPublishTask RUN");
        BookPublishRequest request = requestManager.getBookPublishRequestToProcess();
        if (request == null) {
//            System.out.println("I'm in BookPublishTask RUN. queue is empty");
            return;
        }
//        System.out.println("I'm in BookPublishTask RUN. queue is NOT empty");
        PublishingStatusItem publishingStatusItem;
        publishingStatusItem = publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(),
                PublishingRecordStatus.IN_PROGRESS,
                request.getBookId());
//        System.out.println("I'm in BookPublishTask RUN. Write IN_PROGRESS");
        try {
            // do the publishing attempt
            // to create PublishingStatusItem IN_PROGRESS
            // generate KindleFormattedBook
            KindleFormattedBook kindleBook = KindleFormatConverter.format(request);

            CatalogItemVersion book = catalogDao.createOrUpdateBook(kindleBook);

            // if no exception occurred, then add SUCCESSFUL item with setPublishingStatus

            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(),
                    PublishingRecordStatus.SUCCESSFUL,
                    book.getBookId());

        } catch (Exception e) {
            // if during publishing attempt exception occurred, then add FAILED item with setPublishingStatus
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(),
                    PublishingRecordStatus.FAILED,
                    request.getBookId(), e.getMessage());
        }
    }
}
