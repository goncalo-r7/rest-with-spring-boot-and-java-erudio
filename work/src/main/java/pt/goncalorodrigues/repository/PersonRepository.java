package pt.goncalorodrigues.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.goncalorodrigues.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    // <Tipo de entidade, tipo do id>
}
