package com.guitar.db;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.guitar.db.repository.ModelJpaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.guitar.db.model.Model;
import com.guitar.db.repository.ModelRepository;

@ContextConfiguration(locations={"classpath:com/guitar/db/applicationTests-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ModelPersistenceTests {
	@Autowired
	private ModelRepository modelRepository;

	@Autowired
	private ModelJpaRepository modelJpaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	public void testSaveAndGetAndDelete() throws Exception {
		Model m = new Model();
		m.setFrets(10);
		m.setName("Test Model");
		m.setPrice(BigDecimal.valueOf(55L));
		m.setWoodType("Maple");
		m.setYearFirstMade(new Date());
		m = modelRepository.create(m);
		
		// clear the persistence context so we don't return the previously cached location object
		// this is a test only thing and normally doesn't need to be done in prod code
		entityManager.clear();

		Model otherModel = modelRepository.find(m.getId());
		assertEquals("Test Model", otherModel.getName());
		assertEquals(10, otherModel.getFrets());
		
		//delete BC location now
		modelRepository.delete(otherModel);
	}

	@Test
	public void testGetModelsInPriceRange() throws Exception {
		List<Model> mods = modelRepository.getModelsInPriceRange(BigDecimal.valueOf(1000L), BigDecimal.valueOf(2000L));
		assertEquals(4, mods.size());
	}

	@Test
	public void testGetModelsByPriceRangeAndWoodType() throws Exception {
		List<Model> mods = modelRepository.getModelsByPriceRangeAndWoodType(BigDecimal.valueOf(1000L), BigDecimal.valueOf(2000L), "Maple");
		assertEquals(3, mods.size());
	}

	@Test
	public void testGetModelsByType() throws Exception {
		List<Model> mods = modelRepository.getModelsByType("Electric");
		assertEquals(4, mods.size());
	}

	/***** Added by Tushar
	 * Provide custom method declaration in JPA repository and framework will provide implementation
	 */

	@Test
	public void testFindByLessThan() throws Exception {
		List<Model> mods = modelJpaRepository.findByPriceLessThan(BigDecimal.valueOf(800L));
		assertEquals(1, mods.size());
	}

	@Test
	public void testFindByLessThanEqAndGreaterThanEq() throws Exception {
		List<Model> mods = modelJpaRepository.findByPriceLessThanEqualAndPriceGreaterThanEqual(BigDecimal.valueOf(2000L), BigDecimal.valueOf(1000L));
		assertEquals(4, mods.size());
	}

	@Test
	public void testFindByGreaterThan() throws Exception {
		List<Model> mods = modelJpaRepository.findByPriceGreaterThan(BigDecimal.valueOf(2000L));
		assertEquals(1, mods.size());
	}

	@Test
	public void testFindIn() throws Exception {
		List<String> types = Arrays.asList("Electric","Semi-Hollow Body Electric");
		List<Model> mods = modelJpaRepository.findByModelTypeNameIn(types);
		assertEquals(5, mods.size());
	}

	/*** This test is for method using @Query in JPA repository **/
	@Test
	public void testQueryGetModelsByPriceRangeAndWoodType() throws Exception {
		List<Model> mods = modelJpaRepository.queryByPriceRangeAndWoodType(BigDecimal.valueOf(1000L), BigDecimal.valueOf(2000L), "%Maple%");
		assertEquals(3, mods.size());
	}

	@Test
	public void testNativeQueryGetModelsByType() throws Exception {
		List<Model> mods = modelJpaRepository.nativeQueryModelByName("King");
		assertEquals(1, mods.size());
	}

	/*** This test is for method using JPA Named Query **/
	@Test
	public void testJPANamedQueryGetModelsByType() throws Exception {
		List<Model> mods = modelJpaRepository.findModelByType("Electric");
		assertEquals(4, mods.size());
	}

	/*** This test is for method using @Query in JPA repository **/
	@Test
	public void testQueryGetModelsByPriceRangeAndWoodTypePaginationAndSort() throws Exception {
		Sort sort = new Sort(Sort.Direction.ASC,"name");
		Pageable page = new PageRequest(0,2, sort);

		Page<Model> mods = modelJpaRepository.queryByPriceRangeAndWoodTypePaginationAndSort(BigDecimal.valueOf(1000L), BigDecimal.valueOf(2000L),
				"%Maple%", page);
		assertEquals(2, mods.getSize());

		System.out.println("Total Elements: " + mods.getTotalElements() + " Total Pages: " + mods.getTotalPages());

		mods.getContent().stream().forEach(s -> System.out.println(s.getName()));
	}
}

/*** Run the testQueryGetModelsByPriceRangeAndWoodTypePaginationAndSort() on both ASC and DESC to understand
 * how paging and sorting work. It works on complete result set.
 * Observe the test results.
 */