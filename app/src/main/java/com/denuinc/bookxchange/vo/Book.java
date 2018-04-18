package com.denuinc.bookxchange.vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Created by Florian on 2/26/2018.
 */

@Entity
public class Book implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public final int bookId;
    @SerializedName("id")
    public final String googleBookId;
    @SerializedName("etag")
    public final String etag;
    @SerializedName("selfLink")
    public final String selfLink;
    @SerializedName("volumeInfo")
    @Embedded(prefix = "volume_info_")
    @NonNull
    public final VolumeInfo volumeInfo;
    @SerializedName("saleInfo")
    @Embedded(prefix = "book_")
    @NonNull
    public final SaleInfo saleInfo;
    @SerializedName("accessInfo")
    @Embedded(prefix = "book_")
    @NonNull
    public final AccessInfo accessInfo;
    @SerializedName("epud")
    @Embedded(prefix = "book_")
    @NonNull
    public final Epub epub;
    @SerializedName("pdf")
    @Embedded(prefix = "book_")
    @NonNull
    public final Pdf pdf;
    @SerializedName("searchInfo")
    @Embedded(prefix = "book_")
    @NonNull
    public final SearchInfo searchInfo;
    public Boolean isFavorite;

    public Book(int bookId,
                String googleBookId,
                String etag,
                String selfLink,
                VolumeInfo volumeInfo,
                SaleInfo saleInfo,
                AccessInfo accessInfo,
                Epub epub,
                Pdf pdf,
                SearchInfo searchInfo,
                Boolean isFavorite) {
        this.bookId = bookId;
        this.googleBookId = googleBookId;
        this.etag = etag;
        this.selfLink = selfLink;
        this.volumeInfo = volumeInfo;
        this.saleInfo = saleInfo;
        this.accessInfo = accessInfo;
        this.epub = epub;
        this.pdf = pdf;
        this.searchInfo = searchInfo;
        this.isFavorite = isFavorite;
    }

    public static class VolumeInfo implements Serializable {
        @SerializedName("title")
        public final String title;
        //TODO figure out how to handle intent
//        @SerializedName("authors")
//        public final List<String> authors;
        @SerializedName("publishedDate")
        public final String publishedDate;
//        @SerializedName("industryIdentifiers")
//        @Embedded(prefix = "volume_info")
//        @NonNull
//        public final IndustryIdentifiers industryIdentifiers;
        @SerializedName("readingModes")
        @Embedded(prefix = "volume_info")
        @NonNull
        public final ReadingModes readingModes;
        @SerializedName("printType")
        public final String printType;
        @SerializedName("maturityRating")
        public final String maturityRating;
        @SerializedName("allowAnonLogging")
        public final boolean allowAnonLogging;
        @SerializedName("contentVersion")
        public final String contentVersion;
        @SerializedName("imageLinks")
        @Embedded(prefix = "volume_info")
        @NonNull
        public final ImageLinks imageLinks;
        @SerializedName("language")
        public final String language;
        @SerializedName("previewLink")
        public final String previewLink;
        @SerializedName("infoLink")
        public final String infoLink;
        @SerializedName("canonicalVolumeLink")
        public final String canonicalVolumeLink;

        public VolumeInfo(String title,
//                          List<String> authors,
                          String publishedDate,
//                          IndustryIdentifiers industryIdentifiers,
                          ReadingModes readingModes,
                          String printType,
                          String maturityRating,
                          boolean allowAnonLogging,
                          String contentVersion,
                          ImageLinks imageLinks,
                          String language,
                          String previewLink,
                          String infoLink,
                          String canonicalVolumeLink) {
            this.title = title;
//            this.authors = authors;
            this.publishedDate = publishedDate;
//            this.industryIdentifiers = industryIdentifiers;
            this.readingModes = readingModes;
            this.printType = printType;
            this.maturityRating = maturityRating;
            this.allowAnonLogging = allowAnonLogging;
            this.contentVersion = contentVersion;
            this.imageLinks = imageLinks;
            this.language = language;
            this.previewLink = previewLink;
            this.infoLink = infoLink;
            this.canonicalVolumeLink = canonicalVolumeLink;
        }

        public static class IndustryIdentifiers implements Serializable  {
            @SerializedName("type")
            @NonNull
            public final String type;
            @SerializedName("identifier")
            public final String identifier;

            public IndustryIdentifiers(String type, String identifier) {
                this.type = type;
                this.identifier = identifier;
            }
        }

        public static class ReadingModes implements Serializable  {
            @SerializedName("text")
            @NonNull
            public final String text;
            @SerializedName("image")
            public final String image;

            public ReadingModes(String text, String image) {
                this.text = text;
                this.image = image;
            }
        }

        public static class ImageLinks implements Serializable  {
            @SerializedName("smallThumbnail")
            public final String smallThumbnail;
            @SerializedName("thumbnail")
            public final String thumbnail;

            public ImageLinks(String smallThumbnail, String thumbnail) {
                this.smallThumbnail = smallThumbnail;
                this.thumbnail = thumbnail;
            }
        }
    }

    public static class AccessInfo implements Serializable  {
        @SerializedName("country")
        @ColumnInfo(name = "access_info_country")
        public final String country;
        @SerializedName("viewability")
        public final String viewability;
        @SerializedName("embeddable")
        public final String embeddable;
        @SerializedName("publicDomain")
        public final String publicDomain;
        @SerializedName("textToSpeechPermission")
        public final String textToSpeechPermission;
        @SerializedName("epub")
        @Embedded(prefix = "access_info")
        @NonNull
        public final Epub epub;
        @SerializedName("pdf")
        @Embedded(prefix = "access_info")
        @NonNull
        public final Pdf pdf;
        @SerializedName("webReaderLink")
        public final String webReaderLink;
        @SerializedName("accessViewStatus")
        public final String accessViewStatus;
        @SerializedName("quoteSharingAllowed")
        public final boolean quoteSharingAllowed;

        public AccessInfo(String country,
                          String viewability,
                          String embeddable,
                          String publicDomain,
                          String textToSpeechPermission,
                          Epub epub,
                          Pdf pdf,
                          String webReaderLink,
                          String accessViewStatus, boolean quoteSharingAllowed) {
            this.country = country;
            this.viewability = viewability;
            this.embeddable = embeddable;
            this.publicDomain = publicDomain;
            this.textToSpeechPermission = textToSpeechPermission;
            this.epub = epub;
            this.pdf = pdf;
            this.webReaderLink = webReaderLink;
            this.accessViewStatus = accessViewStatus;
            this.quoteSharingAllowed = quoteSharingAllowed;
        }

        public static class Epub implements Serializable {
            @SerializedName("isAvailable")
            @ColumnInfo(name = "epud_is_available")
            public final String isAvailable;
            @SerializedName("downloadLink")
            @ColumnInfo(name = "epud_download_link")
            public final String downloadLink;

            public Epub(String isAvailable, String downloadLink) {
                this.isAvailable = isAvailable;
                this.downloadLink = downloadLink;
            }
        }

        public static class Pdf implements Serializable  {
            @SerializedName("isAvailable")
            @ColumnInfo(name = "pdf_is_available")
            public final String isAvailable;
            @SerializedName("downloadLink")
            @ColumnInfo(name = "pdf_download_link")
            public final String downloadLink;

            public Pdf(String isAvailable, String downloadLink) {
                this.isAvailable = isAvailable;
                this.downloadLink = downloadLink;
            }
        }
    }

    public static class Epub implements Serializable  {
        @SerializedName("isAvailable")
        @ColumnInfo(name = "epud_is_available")
        public final String isAvailable;
        @SerializedName("downloadLink")
        @ColumnInfo(name = "epud_download_link")
        public final String downloadLink;

        public Epub(String isAvailable, String downloadLink) {
            this.isAvailable = isAvailable;
            this.downloadLink = downloadLink;
        }
    }

    public static class Pdf implements Serializable  {
        @SerializedName("isAvailable")
        @ColumnInfo(name = "pdf_is_available")
        public final String isAvailable;
        @SerializedName("downloadLink")
        @ColumnInfo(name = "pdf_download_link")
        public final String downloadLink;

        public Pdf(String isAvailable, String downloadLink) {
            this.isAvailable = isAvailable;
            this.downloadLink = downloadLink;
        }
    }

    public static class SaleInfo implements Serializable  {
        @SerializedName("country")
        @ColumnInfo(name = "sale_info_country")
        public final String country;
        @SerializedName("saleability")
        public final String saleability;
        @SerializedName("isEbook")
        public final Boolean isEbook;
        @SerializedName("buyLink")
        public final String buyLink;

        public SaleInfo(String country, String saleability, Boolean isEbook, String buyLink) {
            this.country = country;
            this.saleability = saleability;
            this.isEbook = isEbook;
            this.buyLink = buyLink;
        }
    }

    public static class SearchInfo implements Serializable  {
        @SerializedName("textSnippet")
        public final String textSnippet;

        public SearchInfo(String textSnippet) {
            this.textSnippet = textSnippet;
        }
    }
}
