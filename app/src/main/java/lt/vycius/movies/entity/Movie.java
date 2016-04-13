package lt.vycius.movies.entity;

import com.orm.SugarRecord;

import java.io.Serializable;


public class Movie extends SugarRecord implements Serializable {

    protected static final String API_MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    protected static final String API_MOVIE_BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w500/";

    public Movie() {

    }

    protected String title;

    protected String backdrop_path;

    protected String release_date;

    protected String vote_average;

    protected String overview;


    protected String poster_path;


    public String getPosterImage() {
        return API_MOVIE_POSTER_BASE_URL + poster_path;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropUrl() {
        return API_MOVIE_BACKDROP_BASE_URL + backdrop_path;
    }

}
