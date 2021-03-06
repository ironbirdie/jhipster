package be.idewe.jhipster.store.web.rest;

import be.idewe.jhipster.store.JhipsterstoreApp;

import be.idewe.jhipster.store.domain.User;
import be.idewe.jhipster.store.domain.Wish;
import be.idewe.jhipster.store.domain.WishList;
import be.idewe.jhipster.store.repository.UserRepository;
import be.idewe.jhipster.store.repository.WishListRepository;
import be.idewe.jhipster.store.repository.WishRepository;
import be.idewe.jhipster.store.service.UserService;
import be.idewe.jhipster.store.service.WishService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the WishResource REST controller.
 *
 * @see WishResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterstoreApp.class)
@WithMockUser
public class WishResourceIntTest {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);

    @Inject
    private WishRepository wishRepository;

    @Inject
    private WishService wishService;

    @Inject
    private WishListRepository wishListRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restWishMockMvc;

    private Wish wish;

    private WishList wishList;

    private User user;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WishResource wishResource = new WishResource();
        ReflectionTestUtils.setField(wishResource, "wishService", wishService);
        this.restWishMockMvc = MockMvcBuilders.standaloneSetup(wishResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wish createEntity(EntityManager em) {
        Wish wish = new Wish()
                .productId(DEFAULT_PRODUCT_ID)
                .price(DEFAULT_PRICE);
        return wish;
    }

    @Before
    public void initTest() {
        user = userRepository.findOneByLogin("user").get();

        System.out.println(user);
        wishList = wishListRepository.saveAndFlush(new WishList().user(user).creationDate(LocalDate.now()).name("name"));
        System.out.println(wishList);
        wish = createEntity(em);
    }

    @Test
    @Transactional
    public void createWish() throws Exception {
        int databaseSizeBeforeCreate = wishRepository.findAll().size();

        // Create the Wish
        restWishMockMvc.perform(post("/api/wishes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wish.wishList(wishList))))
                .andExpect(status().isCreated());

        // Validate the Wish in the database
        List<Wish> wishes = wishRepository.findAll();
        assertThat(wishes).hasSize(databaseSizeBeforeCreate + 1);
        Wish testWish = wishes.get(wishes.size() - 1);
        assertThat(testWish.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testWish.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testWish.getWishList()).isEqualTo(wishList);
    }

    @Test
    @Transactional
    public void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = wishRepository.findAll().size();
        // set the field null
        wish.setProductId(null);

        // Create the Wish, which fails.

        restWishMockMvc.perform(post("/api/wishes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wish)))
                .andExpect(status().isBadRequest());

        List<Wish> wishes = wishRepository.findAll();
        assertThat(wishes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = wishRepository.findAll().size();
        // set the field null
        wish.setPrice(null);

        // Create the Wish, which fails.

        restWishMockMvc.perform(post("/api/wishes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(wish)))
                .andExpect(status().isBadRequest());

        List<Wish> wishes = wishRepository.findAll();
        assertThat(wishes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWishes() throws Exception {
        // Initialize the database
        wishRepository.saveAndFlush(wish.wishList(wishList));

        // Get all the wishes
        restWishMockMvc.perform(get("/api/wishes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(wish.getId().intValue())))
                .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].wishList.id").value(hasItem(wishList.getId().intValue())));
    }

    @Test
    @Transactional
    public void getWish() throws Exception {
        // Initialize the database
        wishRepository.saveAndFlush(wish.wishList(wishList));

        // Get the wish
        restWishMockMvc.perform(get("/api/wishes/{id}", wish.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(wish.getId().intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.wishList.id").value(wishList.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingWish() throws Exception {
        // Get the wish
        restWishMockMvc.perform(get("/api/wishes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWish() throws Exception {
        // Initialize the database
        wishService.save(wish.wishList(wishList));

        int databaseSizeBeforeUpdate = wishRepository.findAll().size();

        // Update the wish
        Wish updatedWish = wishRepository.findOne(wish.getId());
        updatedWish
                .productId(UPDATED_PRODUCT_ID)
                .price(UPDATED_PRICE);

        restWishMockMvc.perform(put("/api/wishes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWish)))
                .andExpect(status().isOk());

        // Validate the Wish in the database
        List<Wish> wishes = wishRepository.findAll();
        assertThat(wishes).hasSize(databaseSizeBeforeUpdate);
        Wish testWish = wishes.get(wishes.size() - 1);
        assertThat(testWish.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testWish.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void deleteWish() throws Exception {
        // Initialize the database
        wishService.save(wish.wishList(wishList));

        int databaseSizeBeforeDelete = wishRepository.findAll().size();

        // Get the wish
        restWishMockMvc.perform(delete("/api/wishes/{id}", wish.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Wish> wishes = wishRepository.findAll();
        assertThat(wishes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
