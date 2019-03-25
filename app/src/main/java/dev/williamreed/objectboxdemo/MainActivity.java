package dev.williamreed.objectboxdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.javafaker.Faker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.objectbox.Box;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_AUTHORS = 1000;
    private static final int NUM_BOOKS_PER_AUTHOR = 10;

    private static final int NAMES_TO_GEN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.storeBulkData).setOnClickListener(v -> storeBulkData());
        findViewById(R.id.readBulkData).setOnClickListener(v -> readBulkData());
        findViewById(R.id.deleteAllData).setOnClickListener(v -> deleteAllData());
    }

    private void storeBulkData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> {
            long startTime = System.currentTimeMillis();
            Box<Author> authorBox = ObjectBox.get().boxFor(Author.class);
            Faker faker = new Faker();

            // generate some fake data
            List<Author> authors = new ArrayList<>(NUM_AUTHORS);
            int booksCount = 0;
            for (int i = 0; i < NUM_AUTHORS; i++) {
                Author author = new Author(faker.name().firstName(), faker.name().lastName());

                List<Book> books = new ArrayList<>(NUM_AUTHORS * NUM_BOOKS_PER_AUTHOR);
                for (int j = 0; j < NUM_BOOKS_PER_AUTHOR; j++) {
                    Book book = new Book(faker.book().title(), faker.date().past(365 * 10, TimeUnit.DAYS));
                    book.author.setTarget(author);
                    books.add(book);
                }

                // satisfy relationship
                author.books.addAll(books);
                authors.add(author);
                booksCount += books.size();
            }

            authorBox.put(authors);
            int amount = authors.size() + booksCount;

            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;

            return "Stored " + amount + " records in " + time / 1000.0 + " seconds";
        });
    }

    private void readBulkData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> {

            long startTime = System.currentTimeMillis();

            Box<Author> authorBox = ObjectBox.get().boxFor(Author.class);
            Box<Book> bookBox = ObjectBox.get().boxFor(Book.class);

            // don't care about result
            int amount = authorBox.getAll().size() + bookBox.getAll().size();
            bookBox.getAll();

            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;

            return amount + " records retrieved in " + time / 1000.0 + " seconds";
        });
    }

    private void deleteAllData() {
        new ToastAsyncTask(this, findViewById(R.id.spinner)).execute((Task) () -> {

            Box<Author> authorBox = ObjectBox.get().boxFor(Author.class);
            Box<Book> bookBox = ObjectBox.get().boxFor(Book.class);

            bookBox.removeAll();
            authorBox.removeAll();
            return "Removed all records.";
        });
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
