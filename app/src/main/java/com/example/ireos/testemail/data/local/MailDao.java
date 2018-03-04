package com.example.ireos.testemail.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.ireos.testemail.sent.domain.model.Mail;

import java.util.List;

/**
 * Created by tav on 01/03/2018.
 */

@Dao
public interface MailDao {

    @Query("SELECT * FROM sentmail")
    List<Mail> getMail();

    @Query("SELECT * FROM sentmail WHERE entryid = :mailId")
    Mail getMailById(String mailId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMail(Mail mail);

    @Update
    int updateMail(Mail mail);

    @Query("UPDATE sentmail SET favorite = :favorite WHERE entryid = :mailId")
    void updateFavorite(String mailId, boolean favorite);

    @Query("DELETE FROM sentmail WHERE entryid = :mailId")
    int deleteMailById(String mailId);

    @Query("DELETE FROM sentmail")
    void deleteMail();

    @Query("DELETE FROM sentmail WHERE favorite = 0")
    int deleteActiveMail();
}
