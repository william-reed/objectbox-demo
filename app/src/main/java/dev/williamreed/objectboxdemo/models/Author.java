package dev.williamreed.objectboxdemo.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class Author {
    @Id
    public long id;
    public String firstName;
    public String lastName;
    public ToMany<Book> books;

    public Author() {}

    /**
     * Convenience constructor for manual creation
     */
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
