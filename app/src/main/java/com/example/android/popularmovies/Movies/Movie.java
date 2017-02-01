package com.example.android.popularmovies.Movies;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Michalis on 1/27/2017.
 */

public class Movie implements Parcelable{
    private Integer id;
    private String posterPath;
    private String backdropPath;
    private String title;
    private String originalTitle;
    private String originalLanguage;
    private Boolean adult;
    private String overview;
    private Integer popularity;
    private Integer voteCount;
    private Integer voteAverage;
    private Boolean video;
    private String releaseDate;
    private int[] genresIds;

    public Movie(Integer id, String posterPath, String backdropPath, String title,
                 String originalTitle, String originalLanguage, Boolean adult,
                 String overview, Integer popularity, Integer voteCount,
                 Integer voteAverage, Boolean video, String releaseDate) {
        this.id = id;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.title = title;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.adult = adult;
        this.overview = overview;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.video = video;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        posterPath = in.readString();
        backdropPath = in.readString();
        title = in.readString();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        overview = in.readString();
        Log.d("HELLo", "HERE IS pareceable" + overview);
        popularity = in.readInt();
        voteCount = in.readInt();
        voteAverage = in.readInt();
        releaseDate = in.readString();
        genresIds = in.createIntArray();
        video = in.readByte() != 0;
        adult = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(originalLanguage);
        dest.writeString(overview);
        dest.writeInt(popularity);
        dest.writeInt(voteCount);
        dest.writeInt(voteAverage);
        dest.writeString(releaseDate);
        dest.writeIntArray(genresIds);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeByte((byte) (adult ? 1 : 0));
    }

    public int[] getGenresIds() {
        return genresIds;
    }

    public void setGenresIds(int[] genresIds) {
        this.genresIds = genresIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Integer voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
