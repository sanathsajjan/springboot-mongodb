package com.sanath.springBootMongoDB.service;

import com.sanath.springBootMongoDB.collection.Person;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonService {
    String save(Person person);

    List<Person> getPersonsStartWith(String nameFilter);

    void deletePerson(String id);

    List<Person> findPersonsByAge(Integer minAge, Integer maxAge);

    Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable);

    List<Document> findOldestPersonByCity();

    List<Document> getPopulationInCities();
}
