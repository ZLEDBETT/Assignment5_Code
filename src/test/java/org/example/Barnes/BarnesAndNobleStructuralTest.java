package org.example.Barnes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class BarnesAndNobleStructuralTest {


    static class LimitedStockDatabase implements BookDatabase {
        private final Book limitedBook = new Book("999", 15, 2);

        @Override
        public Book findByISBN(String ISBN) {
            return limitedBook;
        }
    }


    static class RecordingBuyBookProcess implements BuyBookProcess {
        private final List<String> purchased = new ArrayList<>();

        @Override
        public void buyBook(Book book, int amount) {
            purchased.add(book + " x" + amount);
        }

        public List<String> getPurchased() {
            return purchased;
        }
    }

    @Test
    @DisplayName("structural-based: adds unavailable when requested > stock")
    void testUnavailableBookPath() {
        BookDatabase db = new LimitedStockDatabase();
        RecordingBuyBookProcess process = new RecordingBuyBookProcess();
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        Map<String, Integer> order = Map.of("999", 5);
        PurchaseSummary summary = store.getPriceForCart(order);

        Book expectedBook = db.findByISBN("999");


        assertEquals(2 * 15, summary.getTotalPrice());
        assertEquals(3, summary.getUnavailable().get(expectedBook));
        assertFalse(process.getPurchased().isEmpty());
    }

    @Test
    @DisplayName("structural-based: handles empty order without exception")
    void testEmptyOrder() {
        BookDatabase db = new LimitedStockDatabase();
        BuyBookProcess process = new RecordingBuyBookProcess();
        BarnesAndNoble store = new BarnesAndNoble(db, process);

        PurchaseSummary summary = store.getPriceForCart(Collections.emptyMap());
        assertNotNull(summary);
        assertEquals(0, summary.getTotalPrice());
        assertTrue(summary.getUnavailable().isEmpty());
    }
}

