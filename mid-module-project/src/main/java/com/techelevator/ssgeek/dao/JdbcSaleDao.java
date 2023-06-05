package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import com.techelevator.ssgeek.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSaleDao implements SaleDao {

    private JdbcTemplate jdbc;
    public JdbcSaleDao (DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public Sale getSale(int saleId) {
        Sale sale = null;
        String sql = "SELECT sale_id, s.customer_id, sale_date, ship_date, name FROM sale s " +
                "JOIN customer c ON c.customer_id = s.customer_id " +
                " WHERE sale_id = ?";
        SqlRowSet results = jdbc.queryForRowSet(sql, saleId);
        if (results.next()) {
            sale =  mapRowToSale(results);
        }
        return sale;
    }

    @Override
    public List<Sale> getSalesUnshipped() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, s.customer_id, sale_date, ship_date, name FROM sale s " +
                "JOIN customer c ON c.customer_id = s.customer_id " +
                " WHERE ship_date IS NULL";
        SqlRowSet results = jdbc.queryForRowSet(sql);
        while (results.next()) {
            Sale sale = mapRowToSale(results);
            sales.add(sale);
        }
        return sales;
    }

    @Override
    public List<Sale> getSalesByCustomerId(int customerId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, s.customer_id, sale_date, ship_date, name FROM sale s " +
                "JOIN customer c ON c.customer_id = s.customer_id " +
                " WHERE s.customer_id = ?";
        SqlRowSet results = jdbc.queryForRowSet(sql, customerId);
        while (results.next()) {
            Sale sale = mapRowToSale(results);
            sales.add(sale);
        }
        return sales;
    }

    @Override
    public List<Sale> getSalesByProductId(int productId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT s.sale_id, s.customer_id, sale_date, ship_date, name FROM sale s " +
                "JOIN customer c ON c.customer_id = s.customer_id " +
                "JOIN line_item l ON l.sale_id = s.sale_id" +
                " WHERE product_id = ?";
        SqlRowSet results = jdbc.queryForRowSet(sql, productId);
        while (results.next()) {
            Sale sale = mapRowToSale(results);
            sales.add(sale);
        }
        return sales;
    }

    @Override
    public Sale createSale(Sale newSale) {
        String sql = "INSERT INTO sale (customer_id, sale_date, ship_date) " +
                "VALUES (?, ?, ?) RETURNING sale_id";
        Integer newId = jdbc.queryForObject(sql, Integer.class, newSale.getCustomerId(), newSale.getSaleDate(),
                newSale.getShipDate());
        newSale.setSaleId(newId);
        return getSale(newId);
    }

    @Override
    public void updateSale(Sale updatedSale) {
        String sql = "UPDATE sale s " +
                "SET customer_id = ?, sale_date = ?, ship_date = ? " +
                "FROM customer c " +
                "WHERE s.customer_id = c.customer_id " +
                "AND sale_id = ?";
        jdbc.update(sql, updatedSale.getCustomerId(), updatedSale.getSaleDate(), updatedSale.getShipDate(),
                updatedSale.getSaleId());

    }

    @Override
    public void deleteSale(int saleId) {
        String deleteLineItemSql = "DELETE FROM line_item " +
                "WHERE sale_id = ?";
        jdbc.update(deleteLineItemSql, saleId);
        String deleteSaleSql = "DELETE FROM sale " +
                "WHERE sale_id = ?";
        jdbc.update(deleteSaleSql, saleId);
    }

    private Sale mapRowToSale(SqlRowSet results) {
        Sale sale = new Sale();
        sale.setSaleId(results.getInt("sale_id"));
        sale.setCustomerId(results.getInt("customer_id"));
        sale.setSaleDate(results.getDate("sale_date").toLocalDate());
        LocalDate shipDate = null;
        if (results.getDate("ship_date") != null) {
            sale.setSaleDate(results.getDate("ship_date").toLocalDate());
        }
        sale.setCustomerName(results.getString("name"));
        return sale;
    }
}
