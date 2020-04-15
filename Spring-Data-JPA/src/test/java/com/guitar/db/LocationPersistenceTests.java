package com.guitar.db;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.guitar.db.repository.LocationJpaRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.guitar.db.model.Location;
import com.guitar.db.repository.LocationRepository;

@ContextConfiguration(locations={"classpath:com/guitar/db/applicationTests-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LocationPersistenceTests {
	@Autowired
	private LocationRepository locationRepository;

	@Autowired
    private LocationJpaRepository locationJpaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
    public void testJpaRepository() {
	   List<Location> locations = locationJpaRepository.findAll();
	   Assert.assertNotNull(locations);
	}

	@Test
	@Transactional
	public void testSaveAndGetAndDelete() throws Exception {
		Location location = new Location();
		location.setCountry("Canada");
		location.setState("British Columbia");
		location = locationRepository.create(location);
		
		// clear the persistence context so we don't return the previously cached location object
		// this is a test only thing and normally doesn't need to be done in prod code
		entityManager.clear();

		Location otherLocation = locationRepository.find(location.getId());
		assertEquals("Canada", otherLocation.getCountry());
		assertEquals("British Columbia", otherLocation.getState());
		
		//delete BC location now
		locationRepository.delete(otherLocation);
	}

	@Test
	public void testFindWithLikeOld() throws Exception {
		List<Location> locs = locationRepository.getLocationByStateName("New");
		assertEquals(4, locs.size());
	}

	@Test
	@Transactional  //note this is needed because we will get a lazy load exception unless we are in a tx
	public void testFindWithChildren() throws Exception {
		Location arizona = locationRepository.find(3L);
		assertEquals("United States", arizona.getCountry());
		assertEquals("Arizona", arizona.getState());

		assertEquals(1, arizona.getManufacturers().size());

		assertEquals("Fender Musical Instruments Corporation", arizona.getManufacturers().get(0).getName());
	}


	/**** Tests Added By Tushar
	 * Declare method in JPA repository for like search. Framework will provide implementation  */
	@Test
	public void testFindWithLike() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateLike("New%");
		assertEquals(4, locs.size());
	}

	@Test
	public void testFindWithNotLike() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNotLike("Al%");
		assertEquals(48, locs.size());
	}

	@Test
	public void testFindWithOr() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateOrCountry("United States", "United States");
		assertEquals(50, locs.size());
	}

	@Test
	public void testFindWithAnd() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateAndCountry("Alabama", "United States");
		assertEquals(1, locs.size());
		assertEquals("Alabama", locs.get(0).getState());
	}

	@Test
	public void testFindWithAndInvalidCountry() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateAndCountry("Alabama", "India");
		assertEquals(0, locs.size());
	}

	/*** This test case is same as testFindWithAnd(). We just added IS and EQUALS in the method. Result will be same */
	@Test
	public void testFindWithAndEquals() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateIsAndCountryEquals("Alabama", "United States");
		assertEquals(1, locs.size());
		assertEquals("Alabama", locs.get(0).getState());
	}

	@Test
	public void testFindWithNot() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNot("Alabama");
		assertEquals(49, locs.size());
	}

	@Test
	public void testFindStartingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateStartingWith("Al");
		assertEquals(2, locs.size());
	}

	@Test
	public void testFindEndingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateEndingWith("tts");
		assertEquals(1, locs.size());
	}

	@Test
	public void testFindContaining() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateContaining("Dako");
		assertEquals(2, locs.size());
	}

	@Test
	public void testFindWithOrderBy() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateStartingWithOrderByStateAsc("New");
		locs.stream().forEach(s -> System.out.println(s.getState()));
	}

	@Test
	public void testFindWithTop() throws Exception {
		List<Location> locs = locationJpaRepository.findTop2ByStateStartingWith("New");
		locs.stream().forEach(s -> System.out.println(s.getState()));
	}

	@Test
	public void testFindWithDistinct() throws Exception {
		List<String> s = locationJpaRepository.findDistinctByCountryLike("United%");
		System.out.println(s.size());
	}

}
