/*
Homework 3
Group No:22
Group Members: Neeraj Auti
               Vedija Jagtap
*/

package com.example.homework3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    TextView tv_times;
    TextView tv_minVal;
    TextView tv_maxVal;
    TextView tv_avgVal;
    Button button_thread;
    SeekBar seekBar;
    ProgressBar progressBar;

    ArrayList<Double> list = new ArrayList<>();
    ExecutorService threadPool;
    Handler handler;
    String complexity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_times = findViewById(R.id.tv_times);
        tv_minVal = findViewById(R.id.tv_minVal);
        tv_maxVal = findViewById(R.id.tv_maxVal);
        tv_avgVal = findViewById(R.id.tv_avgVal);
        button_thread = findViewById(R.id.button_thread);
        progressBar = findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);

        threadPool = Executors.newFixedThreadPool(2);

        seekBar.setMax(10);
        progressBar.setMax(100);
        tv_times.setText(String.valueOf(seekBar.getProgress()) + " time");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_times.setText(String.valueOf(seekBar.getProgress()) + " times");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case DoWork.STATUS_START:
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d("Demo", "Starting.... ");
                        break;

                    case DoWork.STATUS_PROGRESS:
                        progressBar.setProgress(Integer.valueOf(msg.obj.toString()));
                        Log.d("Demo", "Progress.... " + msg.obj);
                        break;

                    case DoWork.STATUS_STOP:
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("Demo", "Stopping.... ");
                        tv_minVal.setText(list.get(0).toString());
                        tv_maxVal.setText(list.get(1).toString());
                        tv_avgVal.setText(list.get(2).toString());
                        break;
                }
                return false;
            }
        });

        button_thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seekBar.getProgress() != 0) {
                    complexity = String.valueOf(seekBar.getProgress());
                    threadPool.execute(new DoWork());
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Complexity greater than 0", Toast.LENGTH_LONG).show();
                    tv_minVal.setText("");
                    tv_maxVal.setText("");
                    tv_avgVal.setText("");

                }
            }
        });
    }

    class DoWork implements Runnable {
        static final int STATUS_START = 0x00;
        static final int STATUS_PROGRESS = 0x01;
        static final int STATUS_STOP = 0x02;

        @Override
        public void run() {
            Message startMessage = new Message();
            startMessage.what = STATUS_START;
            handler.sendMessage(startMessage);

            Log.d("Demo", "Complexity: " + complexity);
            HeavyWork hw = new HeavyWork();
            list = hw.getArrayNumbers(Integer.valueOf(complexity));
            Log.d("Demo", "List: " + list);
            Double min = Collections.min(list);
            Double max = Collections.max(list);

            Double sum = 0.0;
            for (Double each : list) {
                sum += each;
            }

            Double avg = sum / list.size();
            list.clear();
            list.add(min);
            list.add(max);
            list.add(avg);

            for(int i=0; i<100; i++) {
                for(int j=0; j<10000000; j++){
                }
                Message message = new Message();
                message.what = STATUS_PROGRESS;
                message.obj = i;
                handler.sendMessage(message);
            }

            Message stopMessage = new Message();
            stopMessage.what = STATUS_STOP;
            handler.sendMessage(stopMessage);
        }
    }
}
