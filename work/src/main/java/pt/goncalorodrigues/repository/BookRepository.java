package pt.goncalorodrigues.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.goncalorodrigues.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
