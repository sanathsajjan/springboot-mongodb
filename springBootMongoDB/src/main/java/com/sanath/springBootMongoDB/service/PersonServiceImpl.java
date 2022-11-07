package com.sanath.springBootMongoDB.service;

import com.sanath.springBootMongoDB.collection.Person;
import com.sanath.springBootMongoDB.repository.PersonRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Person person) {
        return personRepository.save(person).getPersonId();
    }

    @Override
    public List<Person> getPersonsStartWith(String nameFilter) {
        return personRepository.findByFirstNameStartsWith(nameFilter);
    }

    @Override
    public void deletePerson(String id) {
        personRepository.deleteById(id);
    }

    @Override
    public List<Person> findPersonsByAge(Integer minAge, Integer maxAge) {
        return personRepository.findPersonByAge(minAge, maxAge);
    }

    //Create a criteria query to fetch the result
    @Override
    public Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        if (null != name && !name.isEmpty()) {
            criteriaList.add(Criteria.where("firstName").regex(name, "i"));
        }

        if (minAge != null && maxAge != null) {
            criteriaList.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        if (null != city && !city.isEmpty()) {
            //city attribute is not a part of Person class but it's a part of addresses attribute.
            criteriaList.add(Criteria.where("addresses.city").is(city));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(criteriaList.toArray(new Criteria[0]))));
        }

        Page<Person> personPage = PageableExecutionUtils.getPage(mongoTemplate.find(query,
                Person.class), pageable, () ->// Find the result based on the query and do pagination
                mongoTemplate.count(query.skip(0).limit(0), Person.class)//count gives the number of rec
        );

        return personPage;
    }

    //Get the oldest Person in all the cities
    //We are using aggregation to achieve this.
    //1. Unwind the operation: addresses from peron
    //2. Sort the person by age
    //3. Group by city again on the sorted output
    //4. By using aggregation we can get the desired output with the help of mongoTemplate
    @Override
    public List<Document> findOldestPersonByCity() {
        UnwindOperation unwindOperation = Aggregation.unwind("addresses");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "age");
        GroupOperation groupOperation = Aggregation.group("addresses.city")
                .first(Aggregation.ROOT)
                .as("oldestPersonByAgeAndCity");

        Aggregation aggregation = Aggregation.newAggregation(unwindOperation,
                sortOperation,
                groupOperation);

        List<Document> data = mongoTemplate.aggregate(aggregation, Person.class, Document.class).getMappedResults();
        return data;
    }

    @Override
    public List<Document> getPopulationInCities() {

        UnwindOperation unwindOperation = Aggregation.unwind("addresses");
        GroupOperation groupOperation = Aggregation.group("addresses.city")
                .count().as("popCount");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "popCount");

        //Projection because we dont want all the data in the output
        ProjectionOperation projectionOperation = Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("popCount").as("count")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(unwindOperation, groupOperation, sortOperation, projectionOperation);

        List<Document> data = mongoTemplate.aggregate(aggregation, Person.class, Document.class).getMappedResults();
        return data;
    }
}
