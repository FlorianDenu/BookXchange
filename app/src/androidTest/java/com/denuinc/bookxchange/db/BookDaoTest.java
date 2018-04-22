package com.denuinc.bookxchange.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import com.denuinc.bookxchange.utils.TestUtils;
import com.denuinc.bookxchange.vo.Book;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BookDaoTest extends ProviderTestCase2<BookProvider> {

    private ContentResolver contentResolver;


    public BookDaoTest() {
        super(BookProvider.class, "com.denuinc.bookxchange.db.BookProvider");
        setContext(InstrumentationRegistry.getTargetContext());
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        contentResolver = getMockContentResolver();
    }

    @Test
    public void insertAndRead() {
        Book book = TestUtils.createBook("book1");
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookProvider.GOOGLE_BOOK_ID, book.googleBookId);
        contentValues.put(BookProvider.TITLE, book.volumeInfo.title);
        contentValues.put(BookProvider.DESCRIPTION, book.volumeInfo.description);
        contentValues.put(BookProvider.IS_FAVORITE, book.isFavorite);
        contentValues.put(BookProvider.SMALL_THUMBNAIL, book.volumeInfo.imageLinks.smallThumbnail);
        contentValues.put(BookProvider.THUMBNAIL, book.volumeInfo.imageLinks.thumbnail);
        contentResolver.insert(BookProvider.CONTENT_URI, contentValues);


        Book b = null;
        try (Cursor cursor = contentResolver.query(BookProvider.CONTENT_URI, null, null, null, null)) {
            assert cursor != null;
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(BookProvider._ID));
                String googleBookId = cursor.getString(cursor.getColumnIndex(BookProvider.GOOGLE_BOOK_ID));
                String title = cursor.getString(cursor.getColumnIndex(BookProvider.TITLE));
                String description = cursor.getString(cursor.getColumnIndex(BookProvider.DESCRIPTION));
                Boolean favorite = cursor.getInt(cursor.getColumnIndex(BookProvider.IS_FAVORITE)) == 1;
                String smallThumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.SMALL_THUMBNAIL));
                String thumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.THUMBNAIL));
                b = new Book(id, googleBookId, new Book.VolumeInfo(title, description, new Book.VolumeInfo.ImageLinks(smallThumbnail, thumbnail)), favorite);
            }
        }
        Assert.assertNotEquals(b, null);
        Assert.assertEquals(b.googleBookId, "book1");
        Assert.assertEquals(b.volumeInfo.description, "description");
        Assert.assertEquals(b.volumeInfo.title, "Test");
    }

    @Test
    public void update() {
        insertAndRead();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookProvider.IS_FAVORITE, true);
        contentResolver.update(BookProvider.CONTENT_URI, contentValues, null, null);
        Book b = null;
        try (Cursor cursor = contentResolver.query(BookProvider.CONTENT_URI, null, null, null, null)) {
            assert cursor != null;
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(BookProvider._ID));
                String googleBookId = cursor.getString(cursor.getColumnIndex(BookProvider.GOOGLE_BOOK_ID));
                String title = cursor.getString(cursor.getColumnIndex(BookProvider.TITLE));
                String description = cursor.getString(cursor.getColumnIndex(BookProvider.DESCRIPTION));
                Boolean favorite = cursor.getInt(cursor.getColumnIndex(BookProvider.IS_FAVORITE)) == 1;
                String smallThumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.SMALL_THUMBNAIL));
                String thumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.THUMBNAIL));
                b = new Book(id, googleBookId, new Book.VolumeInfo(title, description, new Book.VolumeInfo.ImageLinks(smallThumbnail, thumbnail)), favorite);
            }
        }
        Assert.assertNotEquals(b, null);
        Assert.assertEquals(b.isFavorite, true);

    }

    @Test
    public void delete() {
        int val = contentResolver.delete(BookProvider.CONTENT_URI, BookProvider.GOOGLE_BOOK_ID + " = 'book1'", null );
        assertNotSame(val, -1);
        Cursor cursor = contentResolver.query(BookProvider.CONTENT_URI, null, null, null, null);
        assert cursor != null;
        assertEquals(cursor.getCount(), 0);
    }

}
