package dev.williamreed.objectboxdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SQLiteDataTest extends SQLiteOpenHelper implements DataTest {
    private static final String AUTHORS_TABLE = "create table IF NOT EXISTS authors ( author_id integer constraint authors_pk primary key autoincrement, firstName TEXT not null, lastName text not null );";
    private static final String BOOKS_TABLE = "create table IF NOT EXISTS books ( book_id integer constraint book_pk primary key autoincrement, name TEXT not null, published TEXT not null, author_id integer not null constraint authors_fk references authors (author_id) );";

    SQLiteDataTest(Context context) {
        super(context, "demo_db", null, 1);
    }

    @Override
    public String bulkWrite() {
        long startTime = System.currentTimeMillis();
        Faker faker = new Faker();

        // generate some fake data
        List<Author> authors = new ArrayList<>(NUM_AUTHORS);
        List<Book> books = new ArrayList<>(NUM_AUTHORS * NUM_BOOKS_PER_AUTHOR);
        for (int i = 0; i < NUM_AUTHORS; i++) {
            Author author = new Author(faker.name().firstName(), faker.name().lastName());

            for (int j = 0; j < NUM_BOOKS_PER_AUTHOR; j++) {
                Book book = new Book(faker.book().title(), faker.date().past(365 * 10, TimeUnit.DAYS));
                books.add(book);
            }

            authors.add(author);
        }

        int amount = authors.size() + books.size();
        SQLiteDatabase db = getWritableDatabase();

        SQLiteStatement insertAuthor = db.compileStatement("INSERT INTO authors (firstName, lastName) VALUES(?, ?)");
        SQLiteStatement insertBook = db.compileStatement("INSERT INTO books (name, published, author_id) VALUES(?, ?, ?)");
        int bookCount = 0;
        try {
            db.beginTransaction();

            for (Author author : authors) {
                insertAuthor.bindString(1, author.firstName);
                insertAuthor.bindString(2, author.lastName);
                long id = insertAuthor.executeInsert();

                for (int i = bookCount; i < bookCount + DataTest.NUM_BOOKS_PER_AUTHOR; i++) {
                    insertBook.bindString(1, books.get(i).name);
                    insertBook.bindString(2, books.get(i).published.toString());
                    // author id
                    insertBook.bindLong(3, id);
                    insertBook.execute();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        return "Stored " + amount + " records in " + time / 1000.0 + " seconds";
    }

    @Override
    public String bulkRead() {
        long startTime = System.currentTimeMillis();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;
        int amount = 0;
        try {
            db.beginTransaction();

            List<Book> books = new ArrayList<>();
            cursor = db.rawQuery("SELECT * FROM authors JOIN books b on authors.author_id = b.author_id;", null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    // this isn't an exercise in reading data, we are just trying to read the data
                    // to see how fast it goes, idc what the data is.
                    cursor.getInt(0);
                    cursor.getString(1);
                    cursor.getString(2);

                    cursor.getInt(3);
                    cursor.getString(4);
                    cursor.getString(5);

                    cursor.moveToNext();
                    amount += 1;

                }
            }
            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        return amount + " records retrieved in " + time / 1000.0 + " seconds";
    }

    @Override
    public String deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            this.getWritableDatabase().beginTransaction();

            db.execSQL("DELETE FROM authors");
            db.execSQL("DELETE FROM books");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return "Removed all data";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(AUTHORS_TABLE);
            db.execSQL(BOOKS_TABLE);
            db.setTransactionSuccessful();
            Log.i("sql", "made all tables");
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op, don't care
    }
}
