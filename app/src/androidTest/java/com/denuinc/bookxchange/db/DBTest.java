package com.denuinc.bookxchange.db;

import org.junit.After;
import org.junit.Before;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;

abstract public class DBTest {

    protected BookXchangeDB db;

    @Before
    public void initDb() {
        this.db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), BookXchangeDB.class).build();
    }

    @After
    public void closeDb() { db.close(); }

}