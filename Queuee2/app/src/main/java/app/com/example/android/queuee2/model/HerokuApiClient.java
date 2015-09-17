package app.com.example.android.queuee2.model;

import com.google.gson.JsonElement;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by bkach on 9/16/15.
 */
public class HerokuApiClient {

    private static String sHerokuServiceEndpoint = "https://intern-queue.herokuapp.com";
    private static HerokuService sHerokuService;

    public static HerokuService getHerokuService() {
        if (sHerokuService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(sHerokuServiceEndpoint)
                    .build();
            sHerokuService = restAdapter.create(HerokuService.class);
        }
        return sHerokuService;
    }

    public interface HerokuService {
        @GET("/add/{queueId}/{userId}")
        Observable<JsonElement> add(
                @Path("queueId") String queueId,
                @Path("userId") String userId
        );
        @GET("/info/{queueId}/{userId}")
        Observable<JsonElement> info(
                @Path("queueId") String queueId,
                @Path("userId") String userId
        );
        @GET("/dequeue")
        Observable<JsonElement> dequeue();
    }
}
