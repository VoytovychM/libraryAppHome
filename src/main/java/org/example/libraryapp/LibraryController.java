package org.example.libraryapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController// bean
public class LibraryController {

    private List<Book> library;

    public LibraryController() {
        library = new ArrayList<>();
        library.add(new Book("Thinking, Fast and Slow", "Daniel Kahneman", "Psychology", 2, "2"));
        library.add(new Book("The laws of human nature", "Robert Greene", "Psychology", 1, "2"));
        library.add(new Book("Clean Code", "Robert C. Martin", "Java", 5, "3"));
        library.add(new Book("Harry Potter", "Daniel Peter", "Fantasy", 4, "4"));
        library.add(new Book("Pride and Prejudice", "Jane Austen", "Romance", 2, "5"));
        library.add(new Book("Jane Eyre", "Charlotte Bronte", "Romance", 15, "6"));
        library.add(new Book("Primary Colors", "", "Politics", 1, "1"));
        library.add(new Book("Go Ask Alice", "", "Romance", 1, "11"));
        library.add(new Book("", "Bubu", "Fairy Tail", 1, "3"));
    }

    @GetMapping("/home")
    public String helloMessage() {
        return "Hello from my excellent website!";
    }

    @GetMapping("/all")
    public List<Book> getAll() {
        return library;
    }

    @GetMapping("/all/{category}")
    public List<Book> getAllByCategory(@PathVariable String category) {
        List<Book> result = library.stream().filter(book -> book.getCategory().equals(category)).toList();
        if (result.isEmpty()) {
            throw new RuntimeException("No book by category" + category + " found. Please check the correction of the data");
        }
        return result;
    }

    @GetMapping("/searchByTitle")
    public List<Book> getAllbyTitle(@RequestParam String title, @RequestParam(required = false) Integer amount) {
        return library.stream().filter(book -> book.getTitle().startsWith(title))
                .filter(book -> amount == null || book.getAvailableAmount() >= amount)
                .toList();
    }

    @PostMapping("/all")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        library.add(book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteByIsbn(@RequestParam String isbn) {
        library.removeIf(book -> book.getIsbn().equals(isbn));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/all")
    public ResponseEntity<Book> updateBook(@RequestBody Book book) {
        if (library.contains(book)) {
            int index = library.indexOf(book);
            library.set(index, book);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } else {
            library.add(book);
            return new ResponseEntity<>(book, HttpStatus.CREATED);
        }
    }

    @PatchMapping("/all")
    public ResponseEntity<Book> updateAmountOfBooks(@RequestParam String isbn, @RequestParam Integer amount) {
        Optional<Book> book = library.stream().filter(b -> b.getIsbn().equals(isbn)).peek(b -> b.setAvailableAmount(amount)).findAny();
        if (book.isPresent()) {
            return new ResponseEntity<>(book.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getOneBookByIsbn")
    public ResponseEntity<Book> getOneByIsbn(@RequestParam String isbn) {
        return library.stream()
                .filter(book -> book.getIsbn().startsWith(isbn))
                .findFirst().map(book -> new ResponseEntity<>(book, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/countAllBooksIncCopies")
    public ResponseEntity<Long> countAllBooksIncCopies() {
        Long total = library.stream().mapToLong(Book::getAvailableAmount).sum();
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @GetMapping("/countBooksByCategory")
    public ResponseEntity<Long> countBooksByCategory(@RequestParam String category) {
        Long totalByCategory = library.stream().filter(book -> category.equals(book.getCategory())).count();
        return new ResponseEntity<>(totalByCategory, HttpStatus.OK);
    }

    @PatchMapping("/fillEmptyFieldAuthorByUnknown")
    public List<Book> fillEmptyFieldAuthorByUnknown() {
        List<Book> result = library.stream().filter(book -> book.getAuthor().equals("")).map(book -> {
            book.setAuthor("Unknown");
            return book;
        }).toList();
        return result;
    }
   @DeleteMapping("/deleteAllWithoutTitle")
   public ResponseEntity<?> deleteAllWithoutTitle() {
       library.removeIf(book -> !StringUtils.hasText(book.getTitle()));
       return new ResponseEntity<>(HttpStatus.ACCEPTED);
   }

}

