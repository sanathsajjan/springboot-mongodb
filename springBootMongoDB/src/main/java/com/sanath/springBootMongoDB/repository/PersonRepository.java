package com.sanath.springBootMongoDB.repository;

import com.sanath.springBootMongoDB.collection.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    List<Person> findByFirstNameStartsWith(String name);

    // Method to find the result between the ages by using mongoRepository
    //List<Person> findByAgeBetween(Integer minAge, Integer maxAge);

    @Query(value = "{'age' : {$gt : ?0, $lt : ?1}}",
    fields = "{addresses : 0}") //We can restrict the fields which we are not interested.
    //Example we don't want addresses in the output. Apart from addresses all the variables of Person class will come.
    //0 means no output. 1 means output to come
    List<Person> findPersonByAge(Integer minAge, Integer maxAge);
}
