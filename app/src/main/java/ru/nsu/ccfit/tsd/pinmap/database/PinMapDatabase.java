package ru.nsu.ccfit.tsd.pinmap.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.nsu.ccfit.tsd.pinmap.database.daos.PinDao;
import ru.nsu.ccfit.tsd.pinmap.database.daos.TagDao;
import ru.nsu.ccfit.tsd.pinmap.database.entities.PhotoEntity;
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinEntity;
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinTagEntity;
import ru.nsu.ccfit.tsd.pinmap.database.entities.TagEntity;

@Database(entities = {PinEntity.class, TagEntity.class, PinTagEntity.class, PhotoEntity.class}, version = 1)
public abstract class PinMapDatabase extends RoomDatabase {
    public abstract PinDao pinDao();
    public abstract TagDao tagDao();
}
