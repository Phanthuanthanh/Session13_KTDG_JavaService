package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ra.demo.exception.BookNotFound;
import ra.demo.model.dto.request.BookDTO;
import ra.demo.model.entity.Book;
import ra.demo.repository.BookResporitory;
import ra.demo.service.BookService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookResporitory bookResporitory;

    @Override
    public List<Book> getBooks() {
        log.debug("getBooks request");
        List<Book> books = bookResporitory.findAll();
        log.info("getBooks success, count={}", books.size());
        return books;
    }

    @Override
    public Book getBookById(Long id) {
        log.debug("getBookById request, id={}", id);
        return bookResporitory.findById(id).orElseThrow(() -> {
            log.error("getBookById error, id={} not found", id);
            return new BookNotFound("Không tồn tại sách có mã " + id);
        });
    }

    @Override
    public Book insertBook(BookDTO bookDTO) {
        log.debug(
                "insertBook request, title={}, author={}, category={}, quantity={}",
                bookDTO.getTitle(),
                bookDTO.getAuthor(),
                bookDTO.getCategory(),
                bookDTO.getQuantity()
        );
        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .category(bookDTO.getCategory())
                .quantity(bookDTO.getQuantity())
                .build();
        Book saved = bookResporitory.save(book);
        log.info("insertBook success, id={}", saved.getId());
        return saved;
    }

    @Override
    public Book updateBook(Long id, BookDTO bookDTO) {
        bookResporitory.findById(id).orElseThrow(()-> new BookNotFound("Không tồn tại sách có mã "+id));
        Book book = Book.builder()
                .id(id)
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .category(bookDTO.getCategory())
                .quantity(bookDTO.getQuantity())
                .build();
        return bookResporitory.save(book);
    }

    @Override
    public boolean deleteBook(Long id) {
        bookResporitory.findById(id).orElseThrow(()-> new BookNotFound("Không tồn tại sách có mã "+id));
        bookResporitory.deleteById(id);
        return true;
    }

    @Override
    public Book updatePartialBook(Long id, BookDTO bookDTO) {
        Book book = bookResporitory.findById(id).orElseThrow(() -> new BookNotFound("Không tồn tại sách có mã " + id));
        if(!bookDTO.getTitle().isBlank()){
            book.setTitle(bookDTO.getTitle());
        }
        if(!bookDTO.getAuthor().isBlank()){
            book.setAuthor(bookDTO.getAuthor());
        }
        if(!bookDTO.getCategory().isBlank()){
            book.setCategory(bookDTO.getCategory());
        }
        if(bookDTO.getQuantity()!=null && bookDTO.getQuantity()>=0){
            book.setQuantity(bookDTO.getQuantity());
        }
        return bookResporitory.save(book);
    }
}
