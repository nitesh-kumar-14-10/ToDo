package com.example.todo;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView taskHistoryTextView;
    private Spinner hourSpinner, minuteSpinner;
    private Button startButton, submitButton, clearButton;
    private Chronometer chronometer;
    private EditText taskEditText;

    private boolean isTimerRunning = false;
    private long selectedDurationInSeconds = 0; // in seconds
    private Handler handler = new Handler();
    private List<String> taskHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSpinners();
        setupStartButton();
        setupSubmitButton();
        setupClearButton();
    }

    private void initializeViews() {
        taskHistoryTextView = findViewById(R.id.taskHistoryTextView);
        hourSpinner = findViewById(R.id.hourSpinner);
        minuteSpinner = findViewById(R.id.minuteSpinner);
        startButton = findViewById(R.id.startButton);
        submitButton = findViewById(R.id.submitButton);
        clearButton = findViewById(R.id.clearButton);
        chronometer = findViewById(R.id.chronometer);
        taskEditText = findViewById(R.id.taskEditText);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(
                this, R.array.hours_array, android.R.layout.simple_spinner_item);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);

        ArrayAdapter<CharSequence> minuteAdapter = ArrayAdapter.createFromResource(
                this, R.array.minutes_array, android.R.layout.simple_spinner_item);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minuteAdapter);

    }

    private void setupStartButton() {
        submitButton.setVisibility(View.VISIBLE); // Show submit button initially
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    int selectedHour = Integer.parseInt(hourSpinner.getSelectedItem().toString());
                    int selectedMinute = Integer.parseInt(minuteSpinner.getSelectedItem().toString());
                    selectedDurationInSeconds = selectedHour * 3600 + selectedMinute * 60;

                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    isTimerRunning = true;

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isTimerRunning) {
                                submitButton.setVisibility(View.VISIBLE);
                                checkTimerExpiry();
                            }
                        }
                    }, selectedDurationInSeconds * 1000); // Convert to milliseconds
                }
            }
        });
    }

    private void checkTimerExpiry() {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        if (elapsedMillis >= selectedDurationInSeconds * 1000) {
            submitButton.setVisibility(View.GONE);
        }
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    chronometer.stop();
                    isTimerRunning = false;

                    String task = taskEditText.getText().toString();
                    long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                    String taskHistory = "Task: " + task + ", Duration: " + elapsedMillis + " ms";
                    taskHistoryList.add(taskHistory);
                    updateTaskHistory();

                    submitButton.setVisibility(View.GONE);
                    taskEditText.getText().clear();
                }
            }
        });
    }

    private void setupClearButton() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskHistoryList.clear();
                updateTaskHistory();
            }
        });
    }

    private void updateTaskHistory() {
        StringBuilder historyBuilder = new StringBuilder("Task History:");
        for (String task : taskHistoryList) {
            historyBuilder.append("\n").append(task);
        }
        taskHistoryTextView.setText(historyBuilder.toString());
    }
}
