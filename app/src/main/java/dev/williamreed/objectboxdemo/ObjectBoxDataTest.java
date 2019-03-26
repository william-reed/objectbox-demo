package dev.williamreed.objectboxdemo;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.objectbox.Box;

public class ObjectBoxDataTest implements DataTest {

    @Override
    public String bulkWrite() {
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
    }

    @Override
    public String bulkRead() {
        long startTime = System.currentTimeMillis();

        Box<Author> authorBox = ObjectBox.get().boxFor(Author.class);
        Box<Book> bookBox = ObjectBox.get().boxFor(Book.class);

        // don't care about result
        int amount = authorBox.getAll().size() + bookBox.getAll().size();
        bookBox.getAll();

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        return amount + " records retrieved in " + time / 1000.0 + " seconds";
    }

    @Override
    public String deleteAll() {
        Box<Author> authorBox = ObjectBox.get().boxFor(Author.class);
        Box<Book> bookBox = ObjectBox.get().boxFor(Book.class);

        bookBox.removeAll();
        authorBox.removeAll();
        return "Removed all records.";
    }
}
