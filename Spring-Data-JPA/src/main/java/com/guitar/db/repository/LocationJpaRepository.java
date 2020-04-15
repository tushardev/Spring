package com.guitar.db.repository;

import com.guitar.db.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationJpaRepository extends JpaRepository<Location, Long> {

    List<Location> findByStateLike(String stateName);

    List<Location> findByStateNotLike(String stateName);

    List<Location> findByStateOrCountry(String state, String country);

    List<Location> findByStateAndCountry(String state, String country);

    List<Location> findByStateIsAndCountryEquals(String state, String country);

    List<Location> findByStateNot(String state);

    List<Location> findByStateStartingWith(String state);

    List<Location> findByStateEndingWith(String state);

    List<Location> findByStateContaining(String state);

    List<Location> findByStateStartingWithOrderByStateAsc(String state);

    List<Location> findTop2ByStateStartingWith(String state);

    List<String> findDistinctByCountryLike(String country);

}


/***** Provide method other than crud operation with some specific condition using
 * proper JPQL method name syntax. It will be provided by framework.
 * */
