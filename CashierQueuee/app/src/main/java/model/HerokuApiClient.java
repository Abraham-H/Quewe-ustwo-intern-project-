package model;

import com.google.gson.JsonElement;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by bkach on 9/16/15.
 */
public class HerokuApiClient {

    private static final String sHerokuServiceEndpoint = "https://intern-queue.herokuapp.com";
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
        @GET("/add/{queueId}")
        Observable<JsonElement> add(
                @Path("queueId") String queueId
        );
        @GET("/add/{queueId}/{userId}")
        Observable<JsonElement> add(
                @Path("queueId") String queueId,
                @Path("userId") String userId
        );
        @GET("/info")
        Observable<JsonElement> info();
        @GET("/info/{queueId}")
        Observable<JsonElement> info(
                @Path("queueId") String queueId
        );
        @GET("/info/{queueId}/{userId}")
        Observable<JsonElement> info(
                @Path("queueId") String queueId,
                @Path("userId") String userId
        );
        @GET("/pop/{queueId}")
        Observable<JsonElement> pop(
                @Path("queueId") String queueId
        );
        @GET("/remove/{queueId}/{userId}")
        Observable<JsonElement> remove(
                @Path("queueId") String queueId,
                @Path("userId") String userId
        );
        @GET("/remove/{queueId}")
        Observable<JsonElement> remove(
                @Path("queueId") String queueId
        );
        @GET("/snooze/{queueId}/{userId}")
        Observable<JsonElement> snooze(
                @Path("queueId") String queueId,
                @Path("userId") String userId
        );
    }
}//EOF HerokuApiClient
