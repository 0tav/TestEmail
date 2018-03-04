package com.example.ireos.testemail.sent.domain.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Created by tav on 01/03/2018.
 */

@Entity(tableName = "sentmail")
public final class Mail {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @Nullable
    @ColumnInfo(name = "title")
    private final String mTitle;

    @Nullable
    @ColumnInfo(name = "subject")
    private final String mSubject;

    @Nullable
    @ColumnInfo(name = "description")
    private final String mDescription;

    @Nullable
    @ColumnInfo(name = "favorite")
    private final boolean mFavorite;

    @Ignore
    public Mail(@Nullable String title, @Nullable String subject, @Nullable String description){
        this(title,subject, description, UUID.randomUUID().toString(), false);
    }

    @Ignore
    public Mail(@Nullable String title, @Nullable String subject, @Nullable String description,
                @Nullable String id){
        this(title, subject, description, id, false);
    }

    @Ignore
    public Mail(@Nullable String title, @Nullable String subject, @Nullable String description,
                boolean favorite){
        this(title, subject, description, UUID.randomUUID().toString(), favorite);
    }

    public Mail(@Nullable String title, @Nullable String subject,
                @Nullable String description, @NonNull String id,
                boolean favorite){
        mId = id;
        mTitle = title;
        mSubject = subject;
        mDescription = description;
        mFavorite = favorite;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getSubject() {
        return mSubject;
    }

    @Nullable
    public String getSubjectForList(){
        if (!Strings.isNullOrEmpty(mSubject)){
            return mSubject;
        } else {
            return mDescription;
        }
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isFavorite(){
        return mFavorite;
    }

    public boolean isActive(){
        return !mFavorite;
    }

    public boolean isEmpty(){
        return Strings.isNullOrEmpty(mTitle) &&
                Strings.isNullOrEmpty(mSubject) &&
                Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Mail mail = (Mail) obj;
        return Objects.equal(mId, mail.mId) &&
                Objects.equal(mTitle, mail.mTitle) &&
                Objects.equal(mDescription, mail.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mSubject, mDescription);
    }

    @Override
    public String toString() {
        return "Mail with subject" + mSubject;
    }
}
