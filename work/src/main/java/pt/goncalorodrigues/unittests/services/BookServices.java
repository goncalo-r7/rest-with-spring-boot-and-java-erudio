package pt.goncalorodrigues.unittests.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.goncalorodrigues.controllers.BookController;
import pt.goncalorodrigues.controllers.PersonController;
import pt.goncalorodrigues.data.dto.BookDTO;
import pt.goncalorodrigues.exceptions.RequiredObjectIsNullException;
import pt.goncalorodrigues.exceptions.ResourceNotFoundException;
import pt.goncalorodrigues.model.Book;
import pt.goncalorodrigues.repository.BookRepository;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pt.goncalorodrigues.mapper.ObjectMapper.parseListObjects;
import static pt.goncalorodrigues.mapper.ObjectMapper.parseObject;

@Service
public class BookServices {
    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    BookRepository repository;

    public List<BookDTO> findAll(){
        logger.info("Finding all books!");

        var dtos = parseListObjects(repository.findAll(), BookDTO.class);
        dtos.forEach(this::addHateoasLinks);
        return dtos;
    }

    public BookDTO findById(Long id){
        logger.info("Finding book by ID!");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        var dto = parseObject(entity, BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO create(BookDTO book){
        if(book == null) throw new RequiredObjectIsNullException();
        logger.info("Creating a book!");
        var entity = repository.save(parseObject(book, Book.class));

        var dto = parseObject(entity, BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO update(BookDTO book){
        if(book == null) throw new RequiredObjectIsNullException();
        logger.info("Updating a book!");

        var entity = repository.findById(book.getId()).orElseThrow(() -> new ResourceNotFoundException("Book does not exist"));
        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id){
        logger.info("Deleting a book!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book does not exist"));
        repository.delete(entity);
    }

    private void addHateoasLinks(BookDTO dto) {  // converte o méthod em url
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
