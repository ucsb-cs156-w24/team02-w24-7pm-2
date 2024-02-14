package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.Articles;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
//entity title and its @Id field is passed to CrudRepository
public interface ArticlesRepository extends CrudRepository<Articles, Long> {
  //Iterable<UCSBDate> findAllByQuarterYYYYQ(String quarterYYYYQ);

}