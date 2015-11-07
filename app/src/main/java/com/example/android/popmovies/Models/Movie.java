package com.example.android.popmovies.Models;

import android.net.Uri;

/**
 * Created by harryliu on 10/26/15.
 */
public class Movie {
    public static String VIDEO_TYPE_TRAILER = "Trailer";

    public static String BASE_URI = "http://image.tmdb.org/t/p";
    public static String POSTER_WIDTH = "w185";
    public static String BACKDROP_WIDTH = "w780";

    long id;
    String title;
    String overview;
    Uri posterPath;
    Uri backdropPath;
    String releaseYear;
    double voteAverage;

    Trailer[] trailers;

    public Movie() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Uri getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(Uri posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Trailer[] getTrailers() {
        return trailers;
    }

    public void setTrailers(Trailer[] trailers) {
        this.trailers = trailers;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Uri getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(Uri backdropPath) {
        this.backdropPath = backdropPath;
    }
}
