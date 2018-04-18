package com.denuinc.bookxchange.db;

import android.support.test.runner.AndroidJUnit4;

import com.denuinc.bookxchange.utils.LiveDataTestUtils;
import com.denuinc.bookxchange.utils.TestUtils;
import com.denuinc.bookxchange.vo.Book;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BookDaoTest extends DBTest {

    @Test
    public void insertAndRead() throws InterruptedException {
        Book book = TestUtils.createBook("book1");
        db.bookDao().insert(book);
        Book loaded = LiveDataTestUtils.getValue(db.bookDao().load("book1"));
        Assert.assertNotEquals(loaded, null);
        Assert.assertEquals(loaded.googleBookId, "book1");
        Assert.assertEquals(loaded.etag, "SnIInW92kzg");
        Assert.assertEquals(loaded.selfLink, "https://www.googleapis.com/books/v1/volumes/ftO4jLQ2RM8C");
    }

    @Test
    public void createIfNotExistsExists() throws InterruptedException {
        Book book = TestUtils.createBook("book2");
        db.bookDao().insert(book);
        Assert.assertEquals(db.bookDao().createBookIfNotExists(book), 2);
    }

    @Test
    public void createIfNotExists() throws InterruptedException {
        Book book = TestUtils.createBook("book3");
        Assert.assertEquals(db.bookDao().createBookIfNotExists(book), 1);
    }
}
