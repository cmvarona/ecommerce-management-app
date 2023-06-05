package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDao implements ProductDao{

    private JdbcTemplate jdbc;

    public JdbcProductDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public Product getProduct(int productId) {
        Product p = null;
        String sql = "SELECT * FROM product WHERE product_id = ?";
        SqlRowSet results = jdbc.queryForRowSet(sql, productId);
        if (results.next()) {
            p = mapRowToProduct(results);
        }
        return p;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY product_id";
        SqlRowSet results = jdbc.queryForRowSet(sql);
        while (results.next()) {
            Product p = mapRowToProduct(results);
            products.add(p);
        }
        return products;
    }

    @Override
    public List<Product> getProductsWithNoSales() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product p " +
                "LEFT JOIN line_item l ON p.product_id = l.product_id " +
                "WHERE sale_id IS NULL";
        SqlRowSet results = jdbc.queryForRowSet(sql);
        while (results.next()) {
            Product p = mapRowToProduct(results);
            products.add(p);
        }
        return products;
    }

    @Override
    public Product createProduct(Product newProduct) {
        String sql = "INSERT INTO product" +
                "(name, description, price, image_name)" +
                "VALUES (?, ?, ?, ?) RETURNING product_id";
        Integer newId = jdbc.queryForObject(sql, Integer.class, newProduct.getName(), newProduct.getDescription(),
                newProduct.getPrice(), newProduct.getImageName());
        newProduct.setProductId(newId);
        return getProduct(newId);
    }

    @Override
    public void updateProduct(Product updatedProduct) {
        String sql = "UPDATE product" +
                "SET name = ?, description = ?, price = ?, image_name = ?" +
                "WHERE product_id = ?";
        jdbc.update(sql, updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice(),
                updatedProduct.getProductId(), updatedProduct.getProductId());
    }

    @Override
    public void deleteProduct(int productId) {
        String deleteLineItemSql = "DELETE FROM line_item WHERE product_id =?";
        jdbc.update(deleteLineItemSql, productId);
        String deleteProductSql = "DELETE FROM product WHERE product_id = ?";
        jdbc.update(deleteProductSql, productId);
    }


    private Product mapRowToProduct(SqlRowSet results) {
        Product product = new Product();
        product.setProductId(results.getInt("product_id"));
        product.setName(results.getString("name"));
        product.setDescription(results.getString("description"));
        product.setPrice(results.getBigDecimal("price"));
        if (results.getString("image_name") != null) {
            product.setImageName(results.getString("image_name"));
        }
        return product;
    }
}
