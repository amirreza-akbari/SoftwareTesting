package com.example.healthcheckapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.healthcheckapp.utils.LocaleManager;
import com.example.healthcheckapp.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;
import java.util.HashMap;
import java.util.Map;
import androidx.cardview.widget.CardView;
import android.graphics.Color;

public class QuestionsActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvQuestion, tvTimer;
    private MaterialButton btnYes, btnNo;
    private int currentQuestion = 1;
    private int totalScore = 0;
    private CountDownTimer timer;
    private String[] answers = new String[10];
    private String name, surname, email;
    private String lastLanguage;
    private String[] questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeAndLocale(); // Apply theme and language settings
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide action bar
        }

        // CardView setup for displaying questions
        CardView cardView = findViewById(R.id.questionCard);
        if (ThemeManager.isDarkMode(this)) {
            cardView.setCardBackgroundColor(Color.parseColor("#0f4b6f")); // Dark mode background color
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF")); // Light mode background color
        }

        // Retrieving user data passed from previous activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        email = intent.getStringExtra("email");

        // Initialize views
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        // Setup the questions based on the selected language
        setupQuestions();
        updateQuestion();

        // Initialize and start the countdown timer
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));

                // Change the timer text color to red when less than 20 seconds remain
                if (millisUntilFinished <= 20000) {
                    tvTimer.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText(getCurrentLanguage().equals("fa") ? "زمان تمام شد!" : "Time's up!");
                submitAnswers(); // Submit the answers once the timer finishes
            }
        };
        timer.start();

        // Setting button actions for "Yes" and "No" answers
        btnYes.setOnClickListener(v -> {
            answers[currentQuestion - 1] = "yes";
            totalScore += 2; // Add score for answering "Yes"
            goToNextQuestion();
        });

        btnNo.setOnClickListener(v -> {
            answers[currentQuestion - 1] = "no";
            goToNextQuestion();
        });

        // Prevent user from going back during the quiz
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(QuestionsActivity.this)
                        .setTitle(getCurrentLanguage().equals("fa") ? "خطا" : "Warning")
                        .setMessage(getCurrentLanguage().equals("fa")
                                ? "شما تا پایان آزمون حق خروج ندارید!"
                                : "You cannot exit until the end of the test!")
                        .setCancelable(false)
                        .setIcon(R.drawable.logo)
                        .setPositiveButton(getCurrentLanguage().equals("fa") ? "باشه" : "OK", (dialog, which) -> {})
                        .show();
            }
        });
    }

    // Apply the app's theme and language based on preferences
    private void applyThemeAndLocale() {
        if (ThemeManager.isDarkMode(this)) {
            setTheme(R.style.Theme_MyApp_Dark); // Set dark theme
        } else {
            setTheme(R.style.Theme_MyApp_Light); // Set light theme
        }

        String currentLang = LocaleManager.getLanguage(this); // Get current language setting
        lastLanguage = currentLang;
        LocaleManager.setLocale(this, currentLang); // Set the app's language
    }

    // Setup the questions based on the current language
    private void setupQuestions() {
        String lang = getCurrentLanguage();
        if (lang.equals("fa")) {
            questions = new String[] {
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
        } else {
            questions = new String[] {
                    "Do you have experience using project management software?",
                    "Can you work with Excel and Word software?",
                    "Have you used the Windows operating system?",
                    "Have you worked with graphic software like Photoshop?",
                    "Do you have programming experience?",
                    "Have you used database management software like SQL?",
                    "Do you use mobile apps like Instagram or Telegram?",
                    "Are you familiar with analytical software like SPSS?",
                    "Have you used video editing software like Premiere?",
                    "Are you familiar with cybersecurity software like Firewalls?"
            };
        }
    }

    // Retrieve the current language
    private String getCurrentLanguage() {
        return LocaleManager.getLanguage(this);
    }

    // Move to the next question or finish the quiz if all questions are answered
    private void goToNextQuestion() {
        if (currentQuestion < questions.length) {
            currentQuestion++;
            updateQuestion();
        } else {
            submitAnswers();
        }
    }

    // Update the UI with the current question number and text
    private void updateQuestion() {
        tvQuestionNumber.setText(getCurrentLanguage().equals("fa") ? "سوال: " + currentQuestion : "Question: " + currentQuestion);
        tvQuestion.setText(questions[currentQuestion - 1]);
    }

    // Submit the answers to the server after the quiz is completed
    private void submitAnswers() {
        if (timer != null) timer.cancel();
        String url = "https://b.mrbackend.ir/save_score.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("success")) {
                        Toast.makeText(this,
                                getCurrentLanguage().equals("fa") ? "نمره با موفقیت ذخیره شد" : "Score saved successfully",
                                Toast.LENGTH_SHORT).show();
                        // Navigate to the result screen after submitting answers
                        Intent intent = new Intent(QuestionsActivity.this, ResultActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("surname", surname);
                        intent.putExtra("email", email);
                        intent.putExtra("totalScore", totalScore);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this,
                                getCurrentLanguage().equals("fa") ? "خطا در ذخیره نمره" : "Error saving score",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this,
                        getCurrentLanguage().equals("fa") ? "خطا در اتصال به سرور" : "Server connection error",
                        Toast.LENGTH_SHORT).show()) {
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

    @Override
    protected void onResume() {
        super.onResume();
        String currentLang = LocaleManager.getLanguage(this);
        if (!currentLang.equals(lastLanguage)) {
            applyThemeAndLocale();
            recreate();
        }
    }
}
