package com.example.healthcheckapp;

import android.content.Intent;
import android.print.PrintAttributes;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthcheckapp.utils.LocaleManager;
import com.example.healthcheckapp.utils.ThemeManager;
import androidx.cardview.widget.CardView;
import android.graphics.Color;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnBackToMain, btnExit, btnPrint;
    private static String lastLanguage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the theme based on dark or light mode
        if (ThemeManager.isDarkMode(this)) {
            setTheme(R.style.Theme_MyApp_Dark);
        } else {
            setTheme(R.style.Theme_MyApp_Light);
        }

        // Check the current language and set locale
        String currentLang = LocaleManager.getLanguage(this);
        if (lastLanguage == null) {
            lastLanguage = currentLang;
        }
        LocaleManager.setLocale(this, currentLang);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get the CardView ID for styling
        CardView cardView = findViewById(R.id.cardView);

        // Set background color based on theme (dark or light mode)
        if (ThemeManager.isDarkMode(this)) {
            cardView.setCardBackgroundColor(Color.parseColor("#0f4b6f")); // Navy in dark mode
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF")); // White in light mode
        }

        // Initialize UI components
        tvResult = findViewById(R.id.tvResult);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnExit = findViewById(R.id.btnExit);
        btnPrint = findViewById(R.id.btnPrint);  // Print button

        // Retrieve user data from the Intent
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String email = getIntent().getStringExtra("email");
        int score = getIntent().getIntExtra("totalScore", 0);

        // Set status message based on score and language
        String status;
        if (score < 10) {
            status = currentLang.equals("fa") ? "⚠️ وضعیت: اطلاعات شما نیاز به بهبود دارد." : "⚠️ Status: Your information needs improvement.";
        } else if (score <= 15) {
            status = currentLang.equals("fa") ? "✅ وضعیت: خوب، می‌توانید بهتر شوید." : "✅ Status: Good, but you can improve.";
        } else if (score < 20) {
            status = currentLang.equals("fa") ? "🌟 وضعیت: خیلی خوب، عالی پیش می‌روید!" : "🌟 Status: Very good, you're doing great!";
        } else {
            status = currentLang.equals("fa") ? "🏆 وضعیت: ممتاز، فوق‌العاده‌اید!" : "🏆 Status: Excellent, you are outstanding!";
        }

        // Format the result text based on the language
        String result = (currentLang.equals("fa") ?
                "نام: " + name + "\n\n" +
                        "نام خانوادگی: " + surname + "\n\n" +
                        "ایمیل: " + email + "\n\n" +
                        "نمره: " + score + "\n\n" + status :
                "Name: " + name + "\n\n" +
                        "Surname: " + surname + "\n\n" +
                        "Email: " + email + "\n\n" +
                        "Score: " + score + "\n\n" + status);

        // Set the result text to the TextView
        tvResult.setText(result);

        // Set button texts based on the current language
        btnBackToMain.setText(currentLang.equals("fa") ? "بازگشت به صفحه اصلی" : "Back to Main");
        btnExit.setText(currentLang.equals("fa") ? "خروج" : "Exit");
        btnPrint.setText(currentLang.equals("fa") ? "چاپ نتیجه" : "Print Result");

        // Handle back to main button click
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, TopScoresActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle exit button click to close the app
        btnExit.setOnClickListener(v -> finishAffinity());

        // Set the print button functionality
        btnPrint.setOnClickListener(v -> printResult());
    }

    // Function to handle the printing of the result
    private void printResult() {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        if (printManager != null) {
            // Inside Activity or wherever you need to print the result
            PrintAdapter printAdapter = new PrintAdapter();
            printAdapter.printDocument(this, findViewById(R.id.tvResult));  // Or any other View you want to print
        }
    }

    // Recreate activity if language has changed
    @Override
    protected void onResume() {
        super.onResume();
        String currentLang = LocaleManager.getLanguage(this);
        if (!currentLang.equals(lastLanguage)) {
            lastLanguage = currentLang;
            recreate();
        }
    }
}
