package pt.goncalorodrigues.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pt.goncalorodrigues.integrationtests.testcontainers.AbstractIntegrationTest;
import pt.goncalorodrigues.model.Person;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // JUnit carrega ambiente spring
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // por padrao
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(1)
    void disablePerson() {
        Pageable pageable = PageRequest.of(0, 12,
                Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPeopleByName("nelson", pageable).getContent().get(0);

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("South Africa", person.getAddress());
        assertEquals("Nelson", person.getFirstName());
        assertEquals("Mandela", person.getLastName());
        assertEquals("Male", person.getGender());
        assertTrue(person.isEnabled());
    }

    @Test
    @Order(2)
    void findPeopleByName() {
        Long id = person.getId();
        repository.disablePerson(id);

        var result = repository.findById(id);
        person = result.get();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("South Africa", person.getAddress());
        assertEquals("Nelson", person.getFirstName());
        assertEquals("Mandela", person.getLastName());
        assertEquals("Male", person.getGender());
        assertFalse(person.isEnabled());
    }
}