package lt.vycius.movies.network;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

@Module
public class MovieModule {

    public static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_KEY = "YOU_API_KEY";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public MovieService provideUserService(Retrofit retrofit) {
        return retrofit.create(MovieService.class);
    }

}
