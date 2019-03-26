package dev.williamreed.objectboxdemo.models;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Book {
    @Id
    public long id;
    public String name;
    public Date published;
    public ToOne<Author> author;

    public Book(String name, Date published) {
        this.name = name;
        this.published = published;
    }

    public Book(long id, String name, Date published, long authorId) {
        this.id = id;
        this.name = name;
        this.published = published;
        this.author.setTargetId(authorId);
    }
}
