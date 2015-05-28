package org.personalized.dashboard.dao.impl;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.personalized.dashboard.bootstrap.MongoBootstrap;
import org.personalized.dashboard.dao.api.BookmarkDao;
import org.personalized.dashboard.model.Bookmark;
import org.personalized.dashboard.utils.Constants;
import org.personalized.dashboard.utils.generator.IdGenerator;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Created by sudan on 3/4/15.
 */
public class BookmarkDaoImpl implements BookmarkDao {

    private IdGenerator idGenerator;

    @Inject
    public BookmarkDaoImpl(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public String create(Bookmark bookmark) {
        MongoCollection<Document> collection = MongoBootstrap.getMongoDatabase().getCollection(Constants.BOOKMARKS);

        String bookmarkId = idGenerator.generateId(Constants.BOOKMARK_PREFIX, Constants.ID_LENGTH);
        Document document = new Document()
                .append(Constants.PRIMARY_KEY, bookmarkId)
                .append(Constants.BOOKMARK_NAME, bookmark.getName())
                .append(Constants.BOOKMARK_DESCRIPTION, bookmark.getDescription())
                .append(Constants.BOOKMARK_URL, bookmark.getUrl())
                .append(Constants.BOOKMARK_USER_ID, bookmark.getUserId())
                .append(Constants.BOOKMARK_CREATED_ON, System.currentTimeMillis())
                .append(Constants.BOOKMARK_MODIFIED_AT, System.currentTimeMillis());
        collection.insertOne(document);

        return bookmarkId;
    }

    @Override
    public Bookmark get(String bookmarkId, String userId) {
        MongoCollection<Document> collection = MongoBootstrap.getMongoDatabase().getCollection(Constants.BOOKMARKS);
        Document document = collection.find(and
                        (
                                eq(Constants.PRIMARY_KEY, bookmarkId),
                                eq(Constants.BOOKMARK_USER_ID, userId)
                        )
        ).first();
        if(document != null) {
            Bookmark bookmark = new Bookmark();
            bookmark.setBookmarkId(document.getString(Constants.PRIMARY_KEY));
            bookmark.setName(document.getString(Constants.BOOKMARK_NAME));
            bookmark.setDescription(document.getString(Constants.BOOKMARK_DESCRIPTION));
            bookmark.setUrl(document.getString(Constants.BOOKMARK_URL));
            bookmark.setCreatedOn(document.getLong(Constants.BOOKMARK_CREATED_ON));
            bookmark.setModifiedAt(document.getLong(Constants.BOOKMARK_MODIFIED_AT));
            return bookmark;
        }
        return null;
    }
}
