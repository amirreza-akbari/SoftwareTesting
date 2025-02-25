package com.example.healthcheckapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnBackToMain, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResult = findViewById(R.id.tvResult);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnExit = findViewById(R.id.btnExit);

        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String email = getIntent().getStringExtra("email");
        int score = getIntent().getIntExtra("totalScore", 0);

        String status = "";
        if (score < 10) {
            status = "وضعیت: اطلاعات شما نیاز به بهبود دارد";
        } else if (score >= 10 && score <= 15) {
            status = "وضعیت: خوب، می‌توانید بهتر شوید";
        } else if (score > 15 && score < 20) {
            status = "وضعیت: خیلی خوب، عالی پیش می‌روید";
        } else if (score == 20) {
            status = "وضعیت: ممتاز، فوق‌العاده‌اید!";
        }

        String result = "نام: " + name + "\n" + "\n" +
                "نام خانوادگی: " + surname + "\n" + "\n" +
                "ایمیل: " + email + "\n" + "\n" +
                "نمره: " + score + "\n" + "\n" +
                status;
        tvResult.setText(result);

        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnExit.setOnClickListener(v -> finishAffinity());
    }
}
