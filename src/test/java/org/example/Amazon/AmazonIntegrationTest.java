package org.example.Amazon;
import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.*;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AmazonIntegrationTest {

    private static Database database;
    private static ShoppingCartAdaptor cart;
    private Amazon amazon;

    @BeforeAll
    static void setupDatabase() {
        database = new Database();
        cart = new ShoppingCartAdaptor(database);
    }

    @BeforeEach
    void resetBeforeEach() {
        database.resetDatabase();
        List<PriceRule> rules = Arrays.asList(
                new RegularCost(),
                new DeliveryPrice(),
                new ExtraCostForElectronics()
        );
        amazon = new Amazon(cart, rules);
    }

    @Test
    @DisplayName("specification-based")
    void testCalculateTotalWithMixedItems() {
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 2, 10.0));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Headphones", 1, 50.0));

        double total = amazon.calculate();

        // Expected: Regular cost = 2*10 + 1*50 = 70
        // Delivery cost = 5 (3 items)
        // Extra electronics = 7.5
        assertEquals(70 + 5 + 7.5, total, 0.001);
    }

    @Test
    @DisplayName("structural-based")
    void testDeliveryPriceDifferentRanges() {
        amazon.addToCart(new Item(ItemType.OTHER, "Pen", 1, 1.0));
        assertEquals(6, amazon.calculate(), 0.001);

        database.resetDatabase();
        amazon.addToCart(new Item(ItemType.OTHER, "Box", 5, 1.0));
        assertEquals(10, amazon.calculate(), 0.001);

        database.resetDatabase();
        for (int i = 0; i < 11; i++)
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
        assertEquals(31.0, amazon.calculate(), 0.001);
    }

    @Test
    @DisplayName("structural-based")
    void testEmptyCartReturnsZero() {
        assertEquals(0.0, amazon.calculate(), 0.001);
    }
}
