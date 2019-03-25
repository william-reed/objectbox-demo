package dev.williamreed.objectboxdemo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Author {
    @Id
    public long id;
    public String firstName;
    public String lastName;


    /**
     * Convenience constructor for manual creation
     */
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Author(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
