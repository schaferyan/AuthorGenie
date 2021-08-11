package com.ryanschafer.authorgenie.ui.wordprocessor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.ActivityScrollingEditTextBinding;


import java.io.BufferedReader;
import java.io.File;

import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollingEditTextActivity extends AppCompatActivity {

    private static final String TITLE_PREF_NAME = "last_used_title";
    private static final String COUNT_PREF_NAME = "last_word_count";
    private ActivityScrollingEditTextBinding binding;
    private SharedPreferences mPreferences;
    private static final String prefFileName = "com.ryanschafer.authorgenie3";
    private static String words_counted_name = "words_counted_in_word_counter";
    private Uri fileUri;
    private Uri defaultUri;
    private String tempFileName;
    private File mFileTemp;
    EditText editText;

    ActivityResultLauncher<Intent> fileChooser = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            fileUri = intent.getData();
                            alterDocument(fileUri);
                            finish();
                        }

                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingEditTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> {
            hideKeyboard();
            Snackbar.make(view, "Add the word count to your score and save your work?", Snackbar.LENGTH_LONG)
                    .setAction("Yes", new AddWordsToProgress()).show();
        });
        editText = findViewById(R.id.textbox);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        if (mPreferences == null) {
            mPreferences = getSharedPreferences(prefFileName, MODE_PRIVATE);
        }

        tempFileName = "temp" + ".txt";
        mFileTemp = new File(getFilesDir() + File.separator
                + "AuthorGenie"
                , tempFileName);
        File parentFile = mFileTemp.getParentFile();
        if (parentFile != null) {
            boolean b = parentFile.mkdirs();
        }
        defaultUri = Uri.fromFile(mFileTemp);
        try {
            openDocument(defaultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDocument(Uri fileUri) throws IOException {
        ParcelFileDescriptor pfd =
                getContentResolver().openFileDescriptor(fileUri, "r");
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(pfd.getFileDescriptor()), StandardCharsets.UTF_8))) {

            String line;

            while ((line = br.readLine()) != null) {

                stringBuilder.append(line);
            }
        }

        editText.setText(stringBuilder.toString());
        pfd.close();
        binding.filename.setText(mPreferences.getString(TITLE_PREF_NAME, "Untitled"));
        binding.wordcount.setText(String.valueOf(mPreferences.getInt(COUNT_PREF_NAME, 0)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString().trim();
                if (!str.isEmpty()) {
                    Pattern pattern = Pattern.compile("[\\s]+[\\p{P}]*[a-zA-Z0-9']");
                    Matcher matcher = pattern.matcher(str);
                    int matches = 1;
                    while (matcher.find()) {
                        matches++;
                    }
                    binding.wordcount.setText(String.valueOf(matches));
                } else {
                    binding.wordcount.setText("0");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    protected void addToWordCount() {
        int words = Integer.parseInt(binding.wordcount.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(words_counted_name, words);
        setResult(Activity.RESULT_OK, intent);
    }

    private void createFile(String title) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, title + ".txt");
        fileChooser.launch(intent);
    }

    private void alterDocument(Uri uri) {
        try {
            ParcelFileDescriptor pfd = this.getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());

            fileOutputStream.write(editText.getText().toString().getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
            pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alterDocument(defaultUri);
        mPreferences.edit().putString(TITLE_PREF_NAME, binding.filename.getText().toString())
                .putInt(COUNT_PREF_NAME, Integer.parseInt(binding.wordcount.getText().toString()))
                .apply();
    }

    public class AddWordsToProgress implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            addToWordCount();
            String title = binding.filename.getText().toString();
            createFile(title);
            binding.wordcount.setText("0");
            binding.filename.setText(R.string.untitled);
            editText.setText("");
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}