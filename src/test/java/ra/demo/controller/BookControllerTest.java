package ra.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ra.demo.exception.BookNotFound;
import ra.demo.model.entity.Book;
import ra.demo.service.BookService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(BookController.class)
@SuppressWarnings("removal")
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void getAllBooks_returnList() throws Exception {
        List<Book> books = List.of(
                Book.builder().id(1L).title("A").author("AA").category("C1").quantity(5).build(),
                Book.builder().id(2L).title("B").author("BB").category("C2").quantity(3).build()
        );
        when(bookService.getBooks()).thenReturn(books);

        mockMvc.perform(get("/api/v1/books").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    void getBookById_found() throws Exception {
        Book book = Book.builder().id(1L).title("A").author("AA").category("C1").quantity(5).build();
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/v1/books/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getBookById_notFound() throws Exception {
        when(bookService.getBookById(99L)).thenThrow(new BookNotFound("Không tồn tại sách có mã 99"));

        mockMvc.perform(get("/api/v1/books/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
