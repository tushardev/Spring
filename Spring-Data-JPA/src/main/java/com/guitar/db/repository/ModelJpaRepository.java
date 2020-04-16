package com.guitar.db.repository;

import com.guitar.db.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ModelJpaRepository extends JpaRepository<Model, Long> {

    List<Model> findByPriceLessThan(BigDecimal price);

    List<Model> findByPriceLessThanEqualAndPriceGreaterThanEqual(BigDecimal upper, BigDecimal lower);

    List<Model> findByPriceGreaterThan(BigDecimal price);

    List<Model> findByModelTypeNameIn(List<String> modelTypes);

    @Query("select m from Model m where m.price >= :lowest and m.price <= :highest and m.woodType like :wood")
    List<Model> queryByPriceRangeAndWoodType(@Param("lowest") BigDecimal lowest,
                                             @Param("highest") BigDecimal highest,
                                             @Param("wood") String wood);

    @Query(value = "Select * from Model where name = ?1", nativeQuery = true)
    List<Model> nativeQueryModelByName(String name);

    @Query(name = "Model.findAllModelsByType")
    List<Model> findModelByType(@Param("name") String name);

    @Query("select m from Model m where m.price >= :lowest and m.price <= :highest and m.woodType like :wood")
    Page<Model> queryByPriceRangeAndWoodTypePaginationAndSort(@Param("lowest") BigDecimal lowest,
                                                       @Param("highest") BigDecimal highest,
                                                       @Param("wood") String wood,
                                                       Pageable page);

}

/*** In case of JPA named queries - you can keep the method name same as named query OR
 * add @Query annotation with name parameter as named query name
 */
