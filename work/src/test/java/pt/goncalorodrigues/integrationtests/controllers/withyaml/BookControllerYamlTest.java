package pt.goncalorodrigues.integrationtests.controllers.withyaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import pt.goncalorodrigues.config.TestConfigs;
import pt.goncalorodrigues.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import pt.goncalorodrigues.integrationtests.dto.BookDTO;
import pt.goncalorodrigues.integrationtests.dto.PersonDTO;
import pt.goncalorodrigues.integrationtests.dto.wrappers.xml.PagedModelBook;
import pt.goncalorodrigues.integrationtests.dto.wrappers.xml.PagedModelPerson;
import pt.goncalorodrigues.integrationtests.testcontainers.AbstractIntegrationTest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static BookDTO book;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();

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

        var createdBook = given().config(
                RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(book, objectMapper)
                .when()
                    .post()
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(BookDTO.class, objectMapper);

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

        var createdBook = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(book, objectMapper)
                .when()
                    .put()
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(BookDTO.class, objectMapper);

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
        var createdBook = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", book.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(BookDTO.class, objectMapper);

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
        .pathParam("id", book.getId())
        .when()
            .delete("{id}")
        .then()
            .statusCode(204);
    }

    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {
        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .queryParams("page", 0, "size", 5, "direction", "asc")
                .when()
                    .get()
                .then()
                    .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PagedModelBook.class, objectMapper);

        List<BookDTO> books = response.getContent();

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