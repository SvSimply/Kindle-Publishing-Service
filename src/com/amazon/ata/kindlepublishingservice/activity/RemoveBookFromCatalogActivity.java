package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {
    private CatalogDao catalogDao;

    @Inject
    public RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }


    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        // Find the latest version of the book in the CatalogItemVersions table
        // Change its inactive attribute to true
        // Throws a BookNotFoundException when given book id is not found or the corresponding book is not active in the catalog.
        CatalogItemVersion book = catalogDao.getBookFromCatalog(removeBookFromCatalogRequest.getBookId());
        book.setInactive(true);
        catalogDao.saveBookToCatalog(book);

        return new RemoveBookFromCatalogResponse();
    }
}
