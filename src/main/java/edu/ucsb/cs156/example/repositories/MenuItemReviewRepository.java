package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.MenuItemReview;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemReviewRepository extends CrudRepository<MenuItemReview, Long> {
   Iterable<MenuItemReview> findAllByQuarterYYYYQ(String quarterYYYYQ);
}