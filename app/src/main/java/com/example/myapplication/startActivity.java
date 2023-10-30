package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class startActivity extends AppCompatActivity {
    RetrofitManager retrofitManager = new RetrofitManager();
    private final Handler handler = new Handler();
    Button button;
    private final long DELAY = 500; // 5초 간격으로 서버 요청
    String system = "";
    boolean shouldContinue = true; // Flag to control handler execution
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        button = findViewById(R.id.button);
        handler.post(runnableCode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("start", "Off");

                    // ... 다른 필드들
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                // Retrofit을 사용하여 서버 요청
                retrofitManager.getApiService().startUp(requestBody).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==202){
                            Toast.makeText(startActivity.this, "운행을 종료합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("replace",t.toString());
                    }
                });
            }
        });

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

                    else if(response.code()==203){
                        try {
                            system = response.body().string();
                            Log.d("systemMessage",system);
                            if(system.equals("\"X\"") && shouldContinue == true) {
                                Toast.makeText(startActivity.this, "운행을 종료합니다.", Toast.LENGTH_SHORT).show();
                                shouldContinue = false;
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
