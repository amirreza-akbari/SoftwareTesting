package com.example.healthcheckapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvQuestion, tvTimer;
    private MaterialButton btnYes, btnNo;
    private int currentQuestion = 1;
    private int totalScore = 0;
    private CountDownTimer timer;
    private String[] questions = {
            "آیا شما تجربه استفاده از نرم‌افزار مدیریت پروژه دارید؟",
            "آیا می‌توانید با نرم‌افزارهای اکسل و ورد کار کنید؟",
            "آیا از سیستم عامل ویندوز استفاده کرده‌اید؟",
            "آیا از نرم‌افزارهای گرافیکی مانند فتوشاپ استفاده کرده‌اید؟",
            "آیا تجربه برنامه‌نویسی دارید؟",
            "آیا از نرم‌افزارهای مدیریت دیتابیس مانند SQL استفاده کرده‌اید؟",
            "آیا از برنامه‌های کاربردی موبایل مانند اینستاگرام یا تلگرام استفاده کرده‌اید؟",
            "آیا با استفاده از نرم‌افزارهای تحلیلی داده مانند SPSS آشنا هستید؟",
            "آیا از نرم‌افزارهای ویرایش ویدیو مانند پریمیر استفاده کرده‌اید؟",
            "آیا با استفاده از نرم‌افزارهای امنیت سایبری مانند فایروال آشنا هستید؟"
    };
    private String[] answers = new String[10];
    private String name, surname, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        email = intent.getStringExtra("email");

        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        updateQuestion();

        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));

                if (millisUntilFinished <= 20000) {
                    tvTimer.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("زمان تمام شد!");
                submitAnswers();
            }
        };
        timer.start();

        btnYes.setOnClickListener(v -> {
            answers[currentQuestion - 1] = "yes";
            totalScore += 2;
            goToNextQuestion();
        });

        btnNo.setOnClickListener(v -> {
            answers[currentQuestion - 1] = "no";
            goToNextQuestion();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(QuestionsActivity.this)
                        .setTitle("خطا")
                        .setMessage("شما تا پایان آزمون حق خروج ندارید!")
                        .setCancelable(false)
                        .setIcon(R.drawable.logo)
                        .setPositiveButton("باشه", (dialog, which) -> {})
                        .show();
            }
        });
    }

    private void goToNextQuestion() {
        if (currentQuestion < questions.length) {
            currentQuestion++;
            updateQuestion();
        } else {
            submitAnswers();
        }
    }

    private void updateQuestion() {
        tvQuestionNumber.setText("سوال " + currentQuestion);
        tvQuestion.setText(questions[currentQuestion - 1]);
    }

    private void submitAnswers() {
        if (timer != null) timer.cancel();

        String url = "https://b.mrbackend.ir/save_score.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(this, "نمره با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QuestionsActivity.this, ResultActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("surname", surname);
                        intent.putExtra("email", email);
                        intent.putExtra("totalScore", totalScore);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "خطا در ذخیره نمره", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("surname", surname);
                params.put("email", email);
                params.put("score", String.valueOf(totalScore));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
