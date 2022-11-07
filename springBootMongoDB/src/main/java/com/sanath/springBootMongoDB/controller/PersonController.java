package com.sanath.springBootMongoDB.controller;

import com.sanath.springBootMongoDB.collection.Person;
import com.sanath.springBootMongoDB.service.PersonService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    public String save(@RequestBody Person person) {
        return personService.save(person);
    }

    @GetMapping
    public List<Person> getPersonsStartWith(@RequestParam("name") String name) {
        return personService.getPersonsStartWith(name);
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable String id){
        personService.deletePerson(id);
    }

    @GetMapping("/age")
    public List<Person> getPersonByAge(@RequestParam Integer minAge, @RequestParam Integer maxAge){
        return personService.findPersonsByAge(minAge, maxAge);
    }

    //Pagination using MongoTemplate part of mongo dependency
    //If the user is not given any filter then it will give all the result based on the page size
    @GetMapping("/search")
    public Page<Person> search(@RequestParam(required = false) String name,//Optional field
                               @RequestParam(required = false) Integer minAge,
                               @RequestParam(required = false) Integer maxAge,
                               @RequestParam(required = false) String city,
                               @RequestParam(defaultValue = "0") Integer page,
                               @RequestParam(defaultValue = "10") Integer size){//Page size
        Pageable pageable = PageRequest.of(page, size);
        return personService.search(name, minAge, maxAge, city, pageable);
    }

    //To find the result of old person in all the cities using aggregator
    //Return type is of Document type because mongodb stores the data in json format
    //Note: Document is from org.bson
    @GetMapping("/oldestPersonByCity")
    public List<Document> getOldestPersonByCity(){
        return personService.findOldestPersonByCity();
    }

    @GetMapping("/retrievePopulation")
    public List<Document> populationInCities(){
        return personService.getPopulationInCities();
    }
}
