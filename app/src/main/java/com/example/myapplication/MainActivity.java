package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.myapplication.server.RetrofitManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RetrofitManager retrofitManager = new RetrofitManager();
    private final Handler handler = new Handler();
    private final long DELAY = 2000; // 5초 간격으로 서버 요청
    String system = "";
    boolean shouldContinue = true; // Flag to control handler execution



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        handler.post(runnableCode);
    }
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("start", "");

                // ... 다른 필드들
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            // Retrofit을 사용하여 서버 요청
            retrofitManager.getApiService().startUp(requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==201){



                    }
                    else if(response.code()==202){
                        shouldContinue = true;
                    }
                    else if(response.code()==203){
                        try {
                            system = response.body().string();
                            Log.d("systemMessage",system);
                            if(system.equals("\"O\"") && shouldContinue == true) {
                                shouldContinue = false; // Stop the handler on 201 status code
                                Intent intent = new Intent(getApplicationContext(), startActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Log.d("BooleanTest", response.code()+"");

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("Fail", "fail");

                }
            });

            if (shouldContinue) {
                // 다시 Handler에 Runnable을 넣어 시간 간격마다 요청
                handler.postDelayed(this, DELAY);
            }
        }
    };
}