package org.example.Amazon;
import org.example.Amazon.Cost.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    @Test
    @DisplayName("specification-based")
    void testAmazonWithMockedRules() {
        ShoppingCart mockCart = mock(ShoppingCart.class);
        List<Item> mockItems = List.of(new Item(ItemType.OTHER, "Book", 1, 10.0));
        when(mockCart.getItems()).thenReturn(mockItems);

        PriceRule rule1 = mock(PriceRule.class);
        PriceRule rule2 = mock(PriceRule.class);

        when(rule1.priceToAggregate(mockItems)).thenReturn(20.0);
        when(rule2.priceToAggregate(mockItems)).thenReturn(5.0);

        Amazon amazon = new Amazon(mockCart, List.of(rule1, rule2));
        double result = amazon.calculate();

        assertEquals(25.0, result);
        verify(rule1, times(1)).priceToAggregate(mockItems);
        verify(rule2, times(1)).priceToAggregate(mockItems);
    }

    @Test
    @DisplayName("structural-based")
    void testRegularCost() {
        RegularCost rule = new RegularCost();
        List<Item> items = List.of(
                new Item(ItemType.OTHER, "Book", 2, 10.0),
                new Item(ItemType.ELECTRONIC, "Camera", 1, 100.0)
        );

        double total = rule.priceToAggregate(items);
        assertEquals(120.0, total, 0.001);
    }

    @Test
    @DisplayName("structural-based")
    void testExtraCostForElectronics() {
        ExtraCostForElectronics rule = new ExtraCostForElectronics();

        List<Item> withElectronic = List.of(new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000.0));
        List<Item> noElectronic = List.of(new Item(ItemType.OTHER, "Book", 1, 10.0));

        assertEquals(7.5, rule.priceToAggregate(withElectronic), 0.001);
        assertEquals(0.0, rule.priceToAggregate(noElectronic), 0.001);
    }

    @Test
    @DisplayName("specification-based")
    void testDeliveryPriceTiers() {
        DeliveryPrice rule = new DeliveryPrice();
        assertEquals(0, rule.priceToAggregate(List.of()), 0.001);
        assertEquals(6, rule.priceToAggregate(List.of(new Item(ItemType.OTHER, "A", 1, 1.0))), 0.001);
        assertEquals(12.5, rule.priceToAggregate(List.of(
                new Item(ItemType.OTHER, "A", 1, 1.0),
                new Item(ItemType.OTHER, "B", 1, 1.0),
                new Item(ItemType.OTHER, "C", 1, 1.0),
                new Item(ItemType.OTHER, "D", 1, 1.0)
        )), 0.001);
    }
}
