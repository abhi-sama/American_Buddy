package edu.psu.axs7326.american_buddy.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SlangDAO {
    @Query("SELECT * FROM slangs WHERE liked = :onlyLiked " +
            "ORDER BY title COLLATE NOCASE, rowid")
    LiveData<List<Slang>> getLiked(boolean onlyLiked);

    @Query("SELECT * FROM slangs ORDER BY title COLLATE NOCASE, rowid")
    LiveData<List<Slang>> getAll();

    @Query("SELECT * FROM slangs WHERE rowid = :slangId")
    Slang getById(int slangId);

    @Insert
    void insert(Slang... slangs);

    @Update
    void update(Slang... slang);

    @Delete
    void delete(Slang... user);

    @Query("DELETE FROM slangs WHERE rowid = :slangId")
    void delete(int slangId);
}
