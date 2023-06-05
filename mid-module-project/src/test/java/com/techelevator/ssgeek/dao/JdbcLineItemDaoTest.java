package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;


public class JdbcLineItemDaoTest extends BaseDaoTests {

    private JdbcLineItemDao dao;
    private static final LineItem LINE_ITEM_1 = new LineItem(1, 1, 1, 1,
            "Product 1", new BigDecimal("9.99"));
    private static final LineItem LINE_ITEM_2 = new LineItem(2, 1, 2, 1,
            "Product 2", new BigDecimal("19.00"));
    private static final LineItem LINE_ITEM_3 = new LineItem(3, 1, 4, 1,
            "Product 4", new BigDecimal("0.99"));

    @Before
    public void setup() {
        dao = new JdbcLineItemDao(dataSource);
    }

    @Test
    public void getLineItemsBySale_returns_list_of_correct_lineItems_for_saleId() {
        List<LineItem> items = dao.getLineItemsBySale(1);
        Assert.assertEquals(3, items.size());
        // Check items in list
        assertLineItemsMatch(LINE_ITEM_1, items.get(0));
        assertLineItemsMatch(LINE_ITEM_2, items.get(1));
        assertLineItemsMatch(LINE_ITEM_3, items.get(2));
        // Check for empty list if sale does not have line-item or saleId does not exist
        items = dao.getLineItemsBySale(4);
        Assert.assertEquals(0, items.size());
        items = dao.getLineItemsBySale(5);
        Assert.assertEquals(0, items.size());
    }

    public void assertLineItemsMatch(LineItem expected, LineItem actual) {
        Assert.assertEquals(expected.getLineItemId(), actual.getLineItemId());
        Assert.assertEquals(expected.getSaleId(), actual.getSaleId());
        Assert.assertEquals(expected.getProductId(), actual.getProductId());
        Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals(expected.getProductName(), actual.getProductName());
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
    }
}
