package app.com.example.android.queuee2.utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import app.com.example.android.queuee2.model.Response;

/**
 * Created by Abraham on 9/25/2015.
 */
public class Utils {

    public static Response jsonToResponse(JsonElement data) {
        Gson gson = new Gson();
        return gson.fromJson(data, Response.class);
    }
}
