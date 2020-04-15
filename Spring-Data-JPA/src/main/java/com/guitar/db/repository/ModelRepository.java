package com.guitar.db.repository;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.guitar.db.model.Model;

@Repository
public class ModelRepository {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ModelJpaRepository modelJpaRepository;

	/**
	 * Create
	 */
	/*public Model create(Model mod) {
		entityManager.persist(mod);
		entityManager.flush();
		return mod;
	}*/

	/**
	 * Update
	 */
	/*public Model update(Model mod) {
		mod = entityManager.merge(mod);
		entityManager.flush();
		return mod;
	}*/

	/**
	 * Delete
	 */
	/*public void delete(Model mod) {
		entityManager.remove(mod);
		entityManager.flush();
	}*/

	/**
	 * Find
	 */
	/*public Model find(Long id) {
		return entityManager.find(Model.class, id);
	}*/

	/*** CRUD operations with proxy JPA repository  */
	public Model create(Model mod) {
		modelJpaRepository.saveAndFlush(mod);
		return mod;
	}

	public Model update(Model mod) {
		modelJpaRepository.saveAndFlush(mod);
		return mod;
	}

	public void delete(Model mod) {
		modelJpaRepository.delete(mod);
	}

	public Model find(Long id) {
		return modelJpaRepository.findOne(id);
	}

	/**
	 * Custom finder
	 */
	public List<Model> getModelsInPriceRange(BigDecimal lowest, BigDecimal highest) {
		@SuppressWarnings("unchecked")
		List<Model> mods = entityManager
				.createQuery("select m from Model m where m.price >= :lowest and m.price <= :highest")
				.setParameter("lowest", lowest)
				.setParameter("highest", highest).getResultList();
		return mods;
	}

	/**
	 * Custom finder
	 */
	public List<Model> getModelsByPriceRangeAndWoodType(BigDecimal lowest, BigDecimal highest, String wood) {
		@SuppressWarnings("unchecked")
		List<Model> mods = entityManager
				.createQuery("select m from Model m where m.price >= :lowest and m.price <= :highest and m.woodType like :wood")
				.setParameter("lowest", lowest)
				.setParameter("highest", highest)
				.setParameter("wood", "%" + wood + "%").getResultList();
		return mods;
	}

	/**
	 * NamedQuery finder
	 */
	public List<Model> getModelsByType(String modelType) {
		@SuppressWarnings("unchecked")
		List<Model> mods = entityManager
				.createNamedQuery("Model.findAllModelsByType")
				.setParameter("name", modelType).getResultList();
		return mods;
	}

	/**
	 * Count
	 */
	public Long getModelCount() {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(Model.class)));
		return entityManager.createQuery(cq).getSingleResult();
	}
}
