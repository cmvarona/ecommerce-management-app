package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Sale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;


public class JdbcSaleDaoTest extends BaseDaoTests{

    private JdbcSaleDao dao;
    private static final Sale SALE_1 = mapValuesToSale(1, 1, LocalDate.parse("2022-01-01"), null);
    private static final Sale SALE_2 = mapValuesToSale(2, 1, LocalDate.parse("2022-02-01"),
            LocalDate.parse("2022-02-02"));
    private static final Sale SALE_3 = mapValuesToSale(3, 2, LocalDate.parse("2022-03-01"), null);
    private static final Sale SALE_4 = mapValuesToSale(4, 2, LocalDate.parse("2022-01-01"),
            LocalDate.parse("2022-01-02"));


    @Before
    public void setup() {
        dao = new JdbcSaleDao(dataSource);
    }

    @Test
    public void getSale_returns_correct_sale_for_id() {
        Sale s1 = SALE_1;
        Sale testSale = dao.getSale(1);
        Assert.assertNotNull(testSale);
        assertSalesMatch(s1, testSale);
    }

    @Test
    public void getSale_returns_null_when_id_not_found() {
        Sale sale = dao.getSale(5);
        Assert.assertNull(sale);
    }

    @Test
    public void getSalesUnshipped_returns_list_of_unshipped_sales() {
        List<Sale> sales = dao.getSalesUnshipped();
        Assert.assertEquals(2, sales.size());
        assertSalesMatch(SALE_1, sales.get(0));
        assertSalesMatch(SALE_3, sales.get(1));
    }

    @Test
    public void getSalesByCustomerId_returns_list_of_sales_for_given_customerId() {
        List<Sale> sales = dao.getSalesByCustomerId(SALE_4.getCustomerId());
        Assert.assertEquals(2, sales.size());
        assertSalesMatch(SALE_3, sales.get(0));
        assertSalesMatch(SALE_4, sales.get(1));
        // Check for empty list for customer with no sales, or non-existent id
        sales = dao.getSalesByCustomerId(3);
        Assert.assertEquals(0, sales.size());
        sales = dao.getSalesByCustomerId(5);
        Assert.assertEquals(0, sales.size());
    }

    @Test
    public void getSalesByProductId_returns_list_of_sales_for_given_productId() {
        List<Sale> sales = dao.getSalesByProductId(1);
        Assert.assertEquals(3, sales.size());
        assertSalesMatch(SALE_1, sales.get(0));
        assertSalesMatch(SALE_2, sales.get(1));
        assertSalesMatch(SALE_3, sales.get(2));
        // Check for empty list for product with no sales or non-existent product id
        sales = dao.getSalesByProductId(3);
        Assert.assertEquals(0, sales.size());
        sales = dao.getSalesByProductId(5);
        Assert.assertEquals(0, sales.size());
    }

    @Test
    public void createSale_returns_sale_with_id_and_correct_values() {
        Sale sale = new Sale(0, 4, LocalDate.parse("2023-03-23"), null, "Customer 4");
        Sale createdSale = dao.createSale(sale);
        Assert.assertNotNull(createdSale);
        Integer id = createdSale.getSaleId();
        Assert.assertTrue(id > 0);
        Assert.assertEquals(5, createdSale.getSaleId());
        Sale retrievedSale = dao.getSale(id);
        assertSalesMatch(createdSale, retrievedSale);
    }

    @Test
    public void updateSale_updates_sale_with_correct_values() {
        Sale s1 = dao.getSale(SALE_1.getSaleId());
        Assert.assertNotNull(s1);
        s1.setShipDate(LocalDate.parse("2022-01-03"));
        s1.setCustomerId(3);
        dao.updateSale(s1);
        Assert.assertEquals(LocalDate.parse("2022-01-03"), s1.getShipDate());
        Assert.assertEquals(3, s1.getCustomerId());
        // Check for customer name change if customer id changes

    }

    @Test
    public void deleted_sale_cannot_be_retrieved() {
        dao.deleteSale(SALE_1.getSaleId());
        Sale s = dao.getSale(SALE_1.getSaleId());
        Assert.assertNull(s);
    }


    private void assertSalesMatch(Sale expected, Sale actual) {
        Assert.assertEquals(expected.getSaleId(), actual.getSaleId());
        Assert.assertEquals(expected.getCustomerId(), actual.getCustomerId());
        Assert.assertEquals(expected.getSaleDate(), actual.getSaleDate());
        Assert.assertEquals(expected.getShipDate(), actual.getShipDate());
    }

    private static Sale mapValuesToSale(int saleId, int customerId, LocalDate saleDate, LocalDate shipDate) {
        Sale sale = new Sale();
        sale.setSaleId(saleId);
        sale.setCustomerId(customerId);
        sale.setSaleDate(saleDate);
        if (shipDate != null) {
            sale.setSaleDate(shipDate);
        }
        return sale;
    }

}


