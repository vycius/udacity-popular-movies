package lt.vycius.movies.entity;

import com.orm.SugarRecord;

public class FavoriteMovie extends SugarRecord {

    public FavoriteMovie() {

    }

    public FavoriteMovie(long id) {
        setId(id);
    }
}
