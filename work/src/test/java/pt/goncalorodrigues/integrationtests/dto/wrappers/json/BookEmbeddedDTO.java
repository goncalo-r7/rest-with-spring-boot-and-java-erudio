package pt.goncalorodrigues.integrationtests.dto.wrappers.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.goncalorodrigues.data.dto.BookDTO;

import java.io.Serializable;
import java.util.List;

public class BookEmbeddedDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("books")
    private List<BookDTO> books;

    public BookEmbeddedDTO() {
    }

    public List<BookDTO> getBooks() {
        return books;
    }

    public void setBooks(List<BookDTO> books) {
        this.books = books;
    }
}
