package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class JdbcProductDaoTest extends BaseDaoTests{

    private JdbcProductDao dao;
    private static final Product PRODUCT_1 = mapValuesToProduct(1, "Product 1",
            "Description 1", new BigDecimal("9.99"), "product-1.png");
    private static final Product PRODUCT_2 = mapValuesToProduct(2, "Product 2",
            "Description 2", new BigDecimal("19.00"), "product-2.png");
    private static final Product PRODUCT_3 = mapValuesToProduct(3, "Product 3",
            "Description 3", new BigDecimal("123.45"), "product-3.png");
    private static final Product PRODUCT_4 = mapValuesToProduct(4, "Product 4",
            "Description 4", new BigDecimal("0.99"), "product-4.png");

    @Before
    public void setup() {
        dao = new JdbcProductDao(dataSource);
    }

    @Test
    public void getProduct_returns_correct_product_for_productId() {
        Product p1 = PRODUCT_1;
        Product testProduct = dao.getProduct(PRODUCT_1.getProductId());
        Assert.assertNotNull(testProduct);
        assertProductsMatch(p1, testProduct);
    }

    @Test
    public void getProduct_returns_null_when_id_not_found() {
        Product testProduct = dao.getProduct(5);
        Assert.assertNull(testProduct);
    }

    @Test
    public void getProducts_returns_list_of_all_products() {
        List<Product> products = dao.getProducts();
        Assert.assertEquals(4, products.size());
        assertProductsMatch(PRODUCT_1, products.get(0));
        assertProductsMatch(PRODUCT_2, products.get(1));
        assertProductsMatch(PRODUCT_3, products.get(2));
        assertProductsMatch(PRODUCT_4, products.get(3));
    }

    @Test
    public void getProductsWithNoSales_returns_list_of_products_with_no_sales() {
        List<Product> products = dao.getProductsWithNoSales();
        Assert.assertEquals(1, products.size());
        assertProductsMatch(PRODUCT_3, products.get(0));
    }

    @Test
    public void createProduct_returns_product_with_id_and_correct_values() {
        Product product = new Product(0, "Product 5", "Description 5",
                new BigDecimal("10.42"), null);
        Product createdProduct = dao.createProduct(product);
        Assert.assertNotNull(createdProduct);
        Integer id = createdProduct.getProductId();
        Assert.assertTrue(id > 0);
        Assert.assertEquals(5, createdProduct.getProductId());
        Product retrievedProduct = dao.getProduct(id);
        assertProductsMatch(createdProduct, retrievedProduct);
    }

    @Test
    public void updateProduct_updates_product_with_appropriate_values() {
        Product p1 = dao.getProduct(PRODUCT_1.getProductId());
        Assert.assertNotNull(p1);
        p1.setPrice(new BigDecimal("14.99"));
        p1.setName("New Name");
        Assert.assertEquals(new BigDecimal("14.99"), p1.getPrice());
        Assert.assertEquals("New Name", p1.getName());
    }

    @Test
    public void deleted_product_cannot_be_retrieved() {
        dao.deleteProduct(PRODUCT_2.getProductId());
        Product p = dao.getProduct(PRODUCT_2.getProductId());
        Assert.assertNull(p);
    }

    private static Product mapValuesToProduct(int productId, String name, String description, BigDecimal price,
               String imageName) {
        Product product = new Product();
        product.setProductId(productId);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        if (imageName != null) {
            product.setImageName(imageName);
        }
        return product;
    }

    private void assertProductsMatch(Product expected, Product actual) {
        Assert.assertEquals(expected.getProductId(), actual.getProductId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
        Assert.assertEquals(expected.getImageName(), actual.getImageName());
    }


}

