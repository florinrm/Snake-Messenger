package com.example.snakemessenger.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.snakemessenger.models.Contact;
import com.example.snakemessenger.models.Message;
import com.example.snakemessenger.models.MessageExchangeLog;

@Database(entities = {Contact.class, Message.class, MessageExchangeLog.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDao getContactDao();

    public abstract MessageDao getMessageDao();

    public abstract MessageExchangeLogDao getMessageExchangeLogDao();
}
