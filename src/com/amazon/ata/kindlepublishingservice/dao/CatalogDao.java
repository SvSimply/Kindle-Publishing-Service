package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    public CatalogItemVersion saveBookToCatalog(CatalogItemVersion book) {
        // If The book object has inactive = true, then update the Latest Version Of Book
        // If The book object has inactive = false, then close previous Latest Version Of Book and create a new item
        if (book.isInactive()) {
            CatalogItemVersion bookToUpdate = getLatestVersionOfBook(book.getBookId());
            book.setVersion(bookToUpdate.getVersion());
            dynamoDbMapper.save(book);
        } else {
            CatalogItemVersion bookToUpdate = getLatestVersionOfBook(book.getBookId());
            bookToUpdate.setInactive(true);
            dynamoDbMapper.save(bookToUpdate);
            book.setVersion(bookToUpdate.getVersion() + 1);
            dynamoDbMapper.save(book);

        }

        return book;
    }

    public void validateBookExists(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
    }

    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook kindleBook) {
        if (kindleBook.getBookId() == null) {
            // Check if bookId is null, and it's a new book
            // Create a new Catalog Item
            CatalogItemVersion book = new CatalogItemVersion();
            book.setBookId(KindlePublishingUtils.generateBookId());
            book.setInactive(false);
            book.setVersion(1);
            book.setAuthor(kindleBook.getAuthor());
            book.setGenre(kindleBook.getGenre());
            book.setText(kindleBook.getText());
            book.setTitle(kindleBook.getTitle());
            dynamoDbMapper.save(book);
            return book;

        } else {
            // Check if bookId is not null and find the corresponding Catalog Item
            // Mark previous Catalog Item as inactive, create a new Catalog Item with updated version
            CatalogItemVersion book = new CatalogItemVersion();
            book.setBookId(kindleBook.getBookId());
            book.setInactive(false);
            book.setVersion(0);
            book.setAuthor(kindleBook.getAuthor());
            book.setGenre(kindleBook.getGenre());
            book.setText(kindleBook.getText());
            book.setTitle(kindleBook.getTitle());

            return saveBookToCatalog(book);
        }

    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }


}
