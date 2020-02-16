package net.mednikov.vertxtestingexample.repositories;

import java.util.List;
import java.util.Optional;

import net.mednikov.vertxtestingexample.models.Person;

public interface PersonRepository {

    Person add (Person p);

    void remove (String id);

    Optional<Person> findById (String id);

    List<Person> findAll ();
}