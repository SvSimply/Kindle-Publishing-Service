package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetPublishingStatusActivity {
    private PublishingStatusDao publishingStatusDao;

    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        List<PublishingStatusItem> statusItems = publishingStatusDao.getPublishingStatuses(publishingStatusRequest.getPublishingRecordId());
        // convert List<PublishingStatusItem> to List<PublishingStatusRecord>
        List<PublishingStatusRecord> statusRecords = new ArrayList<>();
        for (PublishingStatusItem item : statusItems) {
            statusRecords.add(PublishingStatusRecord.builder()
                    .withStatusMessage(item.getStatusMessage())
                    .withStatus(item.getStatus().name())
                    .withBookId(item.getBookId())
                    .build());
        }
        // use builder pattern to create GetPublishingStatusResponse object with List<PublishingStatusRecord>

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(statusRecords)
                .build();
    }
}
