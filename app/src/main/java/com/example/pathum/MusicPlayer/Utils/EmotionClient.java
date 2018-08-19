package com.example.muvindu.recyclerdemo.Utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;


import com.bumptech.glide.Glide;
import com.example.muvindu.recyclerdemo.UI.PlayList_view;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmotionClient {
    private static final String DEFAULT_API_ROOT = "https://westus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceAttributes=emotion";
    private static final MediaType MEDIA_TYPE_OCTET_STREAM = MediaType
            .parse("application/octet-stream");
    private static final MediaType MEDIA_TYPE_JSON = MediaType
            .parse("application/json");

    private String API_KEY;
    private Context context;
    private Gson jsonObject;

    public EmotionClient(Context context, String key) {
        this.context = context;
        this.API_KEY = key;
    }

    public void Request(byte[] stream) throws IOException, ExecutionException, InterruptedException {

        OkHttpClient client = new OkHttpClient();
        //byte[] data= ByteStreams.toByteArray(stream);

        byte[] data = stream;

        // data= ImageToByte(context,stream);


        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        // Log.e("BITMAP",stream.)


        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("ocp-apim-subscription-key", API_KEY)
                .url(DEFAULT_API_ROOT)
                .post(RequestBody.create(MediaType.parse("application/octet-stream"), data))
                // .post(RequestBody.create(MEDIA_TYPE_JSON,"{ \"url\": \"http://3.bp.blogspot.com/-x76ft4FJL4U/Vekqlogsr0I/AAAAAAAAAW4/bZfgtonBZVA/s200/shutterstock_81301759.jpg\" }"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("Request", e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                // Log.e("Request Response",response.body().string());
                //jsonObject=new Gson().toJson(response);//=(new JsonParser()).parse(response.body().string().replace("[","")).getAsJsonArray();
                Double anger, happy, neutral, sad;

                HashMap<String, Double> map = new HashMap<>();
                String jsonData = response.body().string().replaceAll("\\[", "").replaceAll("\\]", "");
                try {

                    JSONObject obj = new JSONObject(jsonData);
                    obj = obj.getJSONObject("faceAttributes").getJSONObject("emotion");

                    map.put("neutral", obj.getDouble("neutral"));
                    map.put("anger", obj.getDouble("anger"));

                    map.put("happy", obj.getDouble("happiness"));
                    map.put("sad", obj.getDouble("sadness"));

                    Double max = (Collections.max(map.values()));  // This will return max value in the Hashmap
                    for (Map.Entry<String, Double> entry : map.entrySet()) {  // Itrate through hashmap
                        if (entry.getValue() == max) {
                            System.out.println(entry.getKey());
                            Log.e("My App", entry.getKey());// Print the key with max value
                            Intent i = new Intent(context.getApplicationContext(), PlayList_view.class);
                            switch (entry.getKey()) {
                                case "neutral":

                                    i.putExtra("name", "Normal Songs");
                                    context.startActivity(i);
                                    break;

                                case "anger":
                                    // Intent i = new Intent(context.getApplicationContext(), PlayList_view.class);
                                    i.putExtra("name", "Relaxing Songs");
                                    context.startActivity(i);
                                    break;
                                case "happy":
                                    //Intent i = new Intent(context.getApplicationContext(), PlayList_view.class);
                                    i.putExtra("name", "Favourite Songs");
                                    context.startActivity(i);
                                    break;
                                case "sad":
                                    //Intent i = new Intent(context.getApplicationContext(), PlayList_view.class);
                                    i.putExtra("name", "Happy Songs");
                                    context.startActivity(i);
                                    break;
                            }


                        }
                    }


                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + jsonData + "\"");
                }


                Log.e("Request Response", "xx");
            }


        });
    }


}
