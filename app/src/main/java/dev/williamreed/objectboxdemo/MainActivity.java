package dev.williamreed.objectboxdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;
import dev.williamreed.objectboxdemo.storage.ObjectBoxDataTest;
import dev.williamreed.objectboxdemo.storage.SQLiteDataTest;

public class MainActivity extends AppCompatActivity {
    private ObjectBoxDataTest objectBoxDataTest = new ObjectBoxDataTest();
    private SQLiteDataTest sqLiteDataTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqLiteDataTest = new SQLiteDataTest(this);

        findViewById(R.id.obStoreBulkData).setOnClickListener(v -> obStoreBulkData());
        findViewById(R.id.obReadBulkData).setOnClickListener(v -> obReadBulkData());
        findViewById(R.id.obDeleteAllData).setOnClickListener(v -> obDeleteAllData());

        findViewById(R.id.sqlStoreBulkData).setOnClickListener(v -> sqlStoreBulkData());
        findViewById(R.id.sqlReadBulkData).setOnClickListener(v -> sqlReadBulkData());
        findViewById(R.id.sqlDeleteAllData).setOnClickListener(v -> sqlDeleteAllData());
    }

    private void obStoreBulkData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> objectBoxDataTest.bulkWrite());
    }

    private void obReadBulkData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> objectBoxDataTest.bulkRead());
    }

    private void obDeleteAllData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> objectBoxDataTest.deleteAll());
    }

    private void sqlStoreBulkData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> sqLiteDataTest.bulkWrite());
    }

    private void sqlReadBulkData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> sqLiteDataTest.bulkRead());
    }

    private void sqlDeleteAllData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> sqLiteDataTest.deleteAll());
    }

    /**
     * Convenient async task for showing text after running something
     */
    private static class ToastAsyncTask extends AsyncTask<Task, Void, String> {
        private static volatile int taskCounter = 0;
        private static final Object counterLock = new Object();

        private final WeakReference<Context> context;
        private final WeakReference<ProgressBar> spinner;


        ToastAsyncTask(Context context, ProgressBar spinner) {
            this.context = new WeakReference<>(context);
            this.spinner = new WeakReference<>(spinner);
        }

        @Override
        protected void onPreExecute() {
            synchronized (counterLock) {
                // only change status if none are shown currently
                if (taskCounter == 0)
                    this.spinner.get().setVisibility(View.VISIBLE);
                taskCounter++;
            }
        }

        @Override
        protected String doInBackground(Task... tasks) {
            return tasks[0].execute();
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context.get(), result, Toast.LENGTH_LONG).show();

            // hide spinner if there are no tasks running
            synchronized (counterLock) {
                if (--taskCounter == 0)
                    this.spinner.get().setVisibility(View.GONE);
            }
        }
    }

    /**
     * A task
     */
    interface Task {
        /**
         * @return a string to display after this task is done
         */
        String execute();
    }


}
