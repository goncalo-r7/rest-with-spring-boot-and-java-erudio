package pt.goncalorodrigues.integrationtests.controllers.withjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import pt.goncalorodrigues.config.TestConfigs;
import pt.goncalorodrigues.data.dto.BookDTO;
import pt.goncalorodrigues.integrationtests.dto.PersonDTO;
import pt.goncalorodrigues.integrationtests.dto.wrappers.json.WrapperBookDTO;
import pt.goncalorodrigues.integrationtests.dto.wrappers.json.WrapperPersonDTO;
import pt.goncalorodrigues.integrationtests.testcontainers.AbstractIntegrationTest;
import pt.goncalorodrigues.model.Book;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookDTO();
    }


    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockBook();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .when()
                    .post()
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                    .body()
                        .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Miguel Torga", createdBook.getAuthor());
        assertEquals("Livro de Teste", createdBook.getTitle());
        assertEquals(55d, createdBook.getPrice());
        assertEquals(new Date(2026, Calendar.MARCH, 23), createdBook.getLaunchDate());
    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        book.setTitle("Novo titulo");

        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(book)
                .when()
                    .put()
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                    .body()
                        .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Miguel Torga", createdBook.getAuthor());
        assertEquals("Novo titulo", createdBook.getTitle());
        assertEquals(55d, createdBook.getPrice());
        assertEquals(new Date(2026, Calendar.MARCH, 23), createdBook.getLaunchDate());
    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {
        var content = given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", book.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                    .body()
                        .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertEquals("Miguel Torga", createdBook.getAuthor());
        assertEquals("Novo titulo", createdBook.getTitle());
        assertEquals(55d, createdBook.getPrice());
        assertEquals(new Date(2026, Calendar.MARCH, 23), createdBook.getLaunchDate());
    }

    @Test
    @Order(4)
    void deleteTest() throws JsonProcessingException {
        given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", book.getId())
            .when()
                .delete("{id}")
            .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("page", 0, "size", 5, "direction", "asc")
                .when()
                    .get()
                .then()
                    .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract()
                .body()
                .asString();

        WrapperBookDTO wrapper = objectMapper.readValue(content, WrapperBookDTO.class);
        List<BookDTO> books = wrapper.getEmbedded().getBooks();

        BookDTO bookOne = books.getFirst();
        book = bookOne;

        assertNotNull(bookOne.getId());
        assertTrue(bookOne.getId() > 0);

        assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", bookOne.getAuthor());
        assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", bookOne.getTitle());
        assertEquals(54d, bookOne.getPrice());
        assertEquals(new Date(2017-1900, Calendar.NOVEMBER, 7), bookOne.getLaunchDate());

        BookDTO bookThree = books.get(2);
        book = bookThree;

        assertNotNull(bookThree.getId());
        assertTrue(bookThree.getId() > 0);

        assertEquals("Steve McConnell", bookThree.getAuthor());
        assertEquals("Code complete", bookThree.getTitle());
        assertEquals(58d, bookThree.getPrice());
        assertEquals(new Date(2017-1900, Calendar.NOVEMBER, 7), bookThree.getLaunchDate());
    }

    private void mockBook() {
        book.setAuthor("Miguel Torga");
        book.setPrice(55d);
        book.setLaunchDate(new Date(2026, Calendar.MARCH, 23));
        book.setTitle("Livro de Teste");
    }
}