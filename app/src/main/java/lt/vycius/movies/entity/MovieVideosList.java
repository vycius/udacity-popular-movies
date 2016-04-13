package lt.vycius.movies.entity;


import java.util.ArrayList;

public class MovieVideosList {
    long id;

    public long getId() {
        return id;
    }

    ArrayList<MovieVideo> results;

    public ArrayList<MovieVideo> getVideos() {
        return results;
    }
}
