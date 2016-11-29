package be.idewe.jhipster.store.web.rest;

import be.idewe.jhipster.store.security.SecurityUtils;
import be.idewe.jhipster.store.service.UserService;
import com.codahale.metrics.annotation.Timed;
import be.idewe.jhipster.store.domain.WishList;

import be.idewe.jhipster.store.repository.WishListRepository;
import be.idewe.jhipster.store.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing WishList.
 */
@RestController
@RequestMapping("/api")
public class WishListResource {

    private final Logger log = LoggerFactory.getLogger(WishListResource.class);

    @Inject
    private WishListRepository wishListRepository;

    @Inject
    private UserService userService;

    /**
     * POST  /wish-lists : Create a new wishList.
     *
     * @param wishList the wishList to create
     * @return the ResponseEntity with status 201 (Created) and with body the new wishList, or with status 400 (Bad Request) if the wishList has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/wish-lists")
    @Timed
    public ResponseEntity<WishList> createWishList(@Valid @RequestBody WishList wishList) throws URISyntaxException {
        log.debug("REST request to save WishList : {}", wishList);
        if (wishList.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("wishList", "idexists", "A new wishList cannot already have an ID")).body(null);
        }
        WishList result = wishListRepository.save(wishList.user(userService.getUserWithAuthorities()).creationDate(LocalDate.now()));
        return ResponseEntity.created(new URI("/api/wish-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("wishList", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /wish-lists : Updates an existing wishList.
     *
     * @param wishList the wishList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated wishList,
     * or with status 400 (Bad Request) if the wishList is not valid,
     * or with status 500 (Internal Server Error) if the wishList couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/wish-lists")
    @Timed
    public ResponseEntity<WishList> updateWishList(@Valid @RequestBody WishList wishList) throws URISyntaxException {
        log.debug("REST request to update WishList : {}", wishList);
        if (wishList.getId() == null) {
            return createWishList(wishList);
        }
        WishList result = wishListRepository.save(wishList);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("wishList", wishList.getId().toString()))
            .body(result);
    }

    /**
     * GET  /wish-lists : get all the wishLists.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of wishLists in body
     */
    @GetMapping("/wish-lists")
    @Timed
    public List<WishList> getAllWishLists() {
        log.debug("REST request to get all WishLists");
        // TODO check this out later

        System.out.println(SecurityUtils.getCurrentUserLogin());
        List<WishList> wishLists = wishListRepository.findByUserIsCurrentUser();
        return wishLists;
    }

    /**
     * GET  /wish-lists/:id : get the "id" wishList.
     *
     * @param id the id of the wishList to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the wishList, or with status 404 (Not Found)
     */
    @GetMapping("/wish-lists/{id}")
    @Timed
    public ResponseEntity<WishList> getWishList(@PathVariable Long id) {
        log.debug("REST request to get WishList : {}", id);
        WishList wishList = wishListRepository.findOne(id);
        return Optional.ofNullable(wishList)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /wish-lists/:id : delete the "id" wishList.
     *
     * @param id the id of the wishList to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/wish-lists/{id}")
    @Timed
    public ResponseEntity<Void> deleteWishList(@PathVariable Long id) {
        log.debug("REST request to delete WishList : {}", id);
        wishListRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("wishList", id.toString())).build();
    }

}
