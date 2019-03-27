package dev.williamreed.objectboxdemo.storage;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.williamreed.objectboxdemo.models.Author;
import dev.williamreed.objectboxdemo.models.Book;
import io.objectbox.Box;

public class ObjectBoxDataTest implements DataTest {

    @Override
    public String bulkWrite() {
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

        // insert the data
        long startTime = System.currentTimeMillis();
        authorBox.put(authors);
        int amount = authors.size() + booksCount;

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        return "Stored " + amount + " records in " + time / 1000.0 + " seconds";
    }

    @Override
    public String bulkRead() {

        Box<Author> authorBox = ObjectBox.get().boxFor(Author.class);
        Box<Book> bookBox = ObjectBox.get().boxFor(Book.class);

        long startTime = System.currentTimeMillis();
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

        long startTime = System.currentTimeMillis();
        bookBox.removeAll();
        authorBox.removeAll();

        long time = System.currentTimeMillis() - startTime;
        return "Removed in " + time / 1000.0 + " seconds.";
    }
}
