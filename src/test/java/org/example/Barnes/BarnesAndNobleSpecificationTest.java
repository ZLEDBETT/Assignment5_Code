package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class BarnesAndNobleSpecificationTest {


    static class StubBookDatabase implements BookDatabase {
        private final Map<String, Book> books = new HashMap<>();

        public StubBookDatabase() {
            books.put("123", new Book("123", 10, 5));
            books.put("456", new Book("456", 20, 2));
        }

        @Override
        public Book findByISBN(String ISBN) {
            return books.get(ISBN);
        }
    }


    static class StubBuyBookProcess implements BuyBookProcess {
        @Override
        public void buyBook(Book book, int amount) {
            // no-op for testing
        }
    }

    @Test
    @DisplayName("specification-based: returns correct total when all books available")
    void testAllBooksAvailable() {
        BookDatabase db = new StubBookDatabase();
        BuyBookProcess process = new StubBuyBookProcess();

        BarnesAndNoble store = new BarnesAndNoble(db, process);

        Map<String, Integer> order = Map.of("123", 3, "456", 2);
        PurchaseSummary summary = store.getPriceForCart(order);

        assertEquals(3 * 10 + 2 * 20, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }

    @Test
    @DisplayName("specification-based: returns null when order is null")
    void testNullOrder() {
        BookDatabase db = new StubBookDatabase();
        BuyBookProcess process = new StubBuyBookProcess();

        BarnesAndNoble store = new BarnesAndNoble(db, process);
        assertNull(store.getPriceForCart(null));
    }
}