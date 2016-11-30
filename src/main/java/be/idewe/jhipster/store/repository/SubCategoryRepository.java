package be.idewe.jhipster.store.repository;

import be.idewe.jhipster.store.domain.SubCategory;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SubCategory entity.
 */
@SuppressWarnings("unused")
public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {

}
