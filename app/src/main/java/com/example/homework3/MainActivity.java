package com.example.homework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

        seekBar.setMax(10);
        //threadPool = Executors.newFixedThreadPool(2);

        progressBar = new ProgressBar(this);
        progressBar.setMax(100);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case DoWork.STATUS_START:
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d("demo", "Starting.... ");
                        break;

                    case DoWork.STATUS_PROGRESS:
                        /*progressBar.setProgress(msg.getData().getInt(DoWork.PROGRESS_KEY));
                        Log.d("demo", "Progress.... " + msg.getData().getInt(DoWork.PROGRESS_KEY));*/
                        progressBar.setProgress(progressBar.getProgress()+1);
                        Log.d("demo","Progress.... " + Integer.valueOf(progressBar.getProgress()));
                        tv_times.setText(String.valueOf(seekBar.getProgress()) + " times");
                        tv_minVal.setText(list.get(0).toString());
                        tv_maxVal.setText(list.get(1).toString());
                        tv_avgVal.setText(list.get(2).toString());
                        break;

                    case DoWork.STATUS_STOP:
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("demo", "Stopping.... ");
                        break;
                }
                return false;
            }
        });

        button_thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complexity = String.valueOf(seekBar.getProgress());
                //threadPool.execute(new DoWork());
                new Thread(new DoWork()).start();
            }
        });
    }

    class DoWork implements Runnable{
        static final int STATUS_START = 0x00;
        static final int STATUS_PROGRESS = 0x01;
        static final int STATUS_STOP = 0x02;
        static final String PROGRESS_KEY = "PROGRESS";

        @Override
        public void run() {
            Message startMessage = new Message();
            startMessage.what = STATUS_START;
            handler.sendMessage(startMessage);

            HeavyWork hw = new HeavyWork();
            list = hw.getArrayNumbers(Integer.valueOf(complexity));
            Double min = Collections.min(list);
            Double max = Collections.max(list);

            Double sum = 0.0;
            for (Double each:list){
                sum += each;
            }

            Double avg = sum/list.size();
            list.clear();
            list.add(min);
            list.add(max);
            list.add(avg);
            Message message = new Message();
            message.what = STATUS_PROGRESS;
            handler.sendMessage(message);
            /*for(int i=0; i<100; i++){
                for(int j=0; j<10; j++){
                    HeavyWork hw = new HeavyWork();
                    list = hw.getArrayNumbers(Integer.valueOf(complexity));
                    Double min = Collections.min(list);
                    Double max = Collections.max(list);

                    Double sum = 0.0;
                    for (Double each:list){
                        sum += each;
                    }

                    Double avg = sum/list.size();
                    list.clear();
                    list.add(min);
                    list.add(max);
                    list.add(avg);
                }
                Message message = new Message();
                message.what = STATUS_PROGRESS;
                Bundle bundle = new Bundle();
                bundle.putInt(PROGRESS_KEY, (Integer)i);
                message.setData(bundle);
                handler.sendMessage(message);
            }*/

            Message stopMessage = new Message();
            stopMessage.what = STATUS_STOP;
            handler.sendMessage(stopMessage);
        }
    }
/*
    class DoWork extends AsyncTask<String, Integer, ArrayList<Double>> {

        @Override
        protected void onPreExecute() {
            progressBar.setMax(100);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Double> aDouble) {
            progressBar.setVisibility(View.INVISIBLE);
            tv_times.setText(String.valueOf(seekBar.getProgress()) + " times");
            tv_minVal.setText(aDouble.get(0).toString());
            tv_maxVal.setText(aDouble.get(1).toString());
            tv_avgVal.setText(aDouble.get(2).toString());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(progressBar.getProgress()+1);
        }

        @Override
        protected ArrayList<Double> doInBackground(String... strings) {
            HeavyWork hw = new HeavyWork();
            list = hw.getArrayNumbers(Integer.parseInt(strings[0]));
            Double min = Collections.min(list);
            Double max = Collections.max(list);

            Double sum = 0.0;
            for (Double each:list){
                sum += each;
            }

            Double avg = sum/list.size();
            list.clear();
            list.add(min);
            list.add(max);
            list.add(avg);

            return list;
        }
    }*/

}
