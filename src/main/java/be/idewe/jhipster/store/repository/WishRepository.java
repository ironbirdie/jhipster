package be.idewe.jhipster.store.repository;

import be.idewe.jhipster.store.domain.Wish;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Wish entity.
 */
@SuppressWarnings("unused")
public interface WishRepository extends JpaRepository<Wish,Long> {

    Page<Wish> findByWishListUserLogin(String currentUserLogin, Pageable pageable);
}
