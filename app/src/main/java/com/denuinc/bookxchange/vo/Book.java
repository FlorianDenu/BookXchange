package com.denuinc.bookxchange.vo;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;


/**
 * Created by Florian on 2/26/2018.
 */

public class Book implements Parcelable {
    public final String bookId;
    @SerializedName("id")
    public final String googleBookId;
    @SerializedName("volumeInfo")
    @NonNull
    public final VolumeInfo volumeInfo;
    public Boolean isFavorite;

    public Book(String bookId,
                String googleBookId,
                VolumeInfo volumeInfo,
                Boolean isFavorite) {
        this.bookId = bookId;
        this.googleBookId = googleBookId;
        this.volumeInfo = volumeInfo;
        this.isFavorite = isFavorite;
    }

    public static class VolumeInfo implements Parcelable {
        @SerializedName("title")
        public final String title;
        @SerializedName("description")
        public final String description;
        @SerializedName("imageLinks")
        @NonNull
        public final ImageLinks imageLinks;
        public VolumeInfo(String title,
                          String description,
                          ImageLinks imageLinks) {
            this.title = title;
            this.description = description;
            this.imageLinks = imageLinks;
        }


        public static class ImageLinks implements Parcelable  {
            @SerializedName("smallThumbnail")
            public final String smallThumbnail;
            @SerializedName("thumbnail")
            public final String thumbnail;

            public ImageLinks(String smallThumbnail, String thumbnail) {
                this.smallThumbnail = smallThumbnail;
                this.thumbnail = thumbnail;
            }

            protected ImageLinks(Parcel in) {
                smallThumbnail = in.readString();
                thumbnail = in.readString();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(smallThumbnail);
                dest.writeString(thumbnail);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<ImageLinks> CREATOR = new Creator<ImageLinks>() {
                @Override
                public ImageLinks createFromParcel(Parcel in) {
                    return new ImageLinks(in);
                }

                @Override
                public ImageLinks[] newArray(int size) {
                    return new ImageLinks[size];
                }
            };
        }

        protected VolumeInfo(Parcel in) {
            title = in.readString();
            description = in.readString();
            imageLinks = in.readParcelable(ImageLinks.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(description);
            dest.writeParcelable(imageLinks, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<VolumeInfo> CREATOR = new Creator<VolumeInfo>() {
            @Override
            public VolumeInfo createFromParcel(Parcel in) {
                return new VolumeInfo(in);
            }

            @Override
            public VolumeInfo[] newArray(int size) {
                return new VolumeInfo[size];
            }
        };
    }

    protected Book(Parcel in) {
        bookId = in.readString();
        googleBookId = in.readString();
        volumeInfo = in.readParcelable(VolumeInfo.class.getClassLoader());
        byte tmpIsFavorite = in.readByte();
        isFavorite = tmpIsFavorite == 0 ? null : tmpIsFavorite == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(googleBookId);
        dest.writeParcelable(volumeInfo, flags);
        dest.writeByte((byte) (isFavorite == null ? 0 : isFavorite ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
