package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcCustomerDao implements CustomerDao{

    private JdbcTemplate jdbc;

    public JdbcCustomerDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public Customer getCustomer(int customerId) {
        Customer c = null;
        String sql = "SELECT customer_id, name, street_address1, street_address2, city, state, zip_code" +
                " FROM customer WHERE customer_id = ? ORDER BY customer_id";
        SqlRowSet results = jdbc.queryForRowSet(sql, customerId);
        if (results.next()) {
            c = mapRowToCustomer(results);
        }
        return c;
    }

    @Override
    public List<Customer> getCustomers() {
        String sql = "SELECT * FROM customer";
        List<Customer> customers = new ArrayList<>();
        SqlRowSet results = jdbc.queryForRowSet(sql);
        while (results.next()) {
            Customer c = mapRowToCustomer(results);
            customers.add(c);
        }
        return customers;
    }

    @Override
    public Customer createCustomer(Customer newCustomer) {
        String sql = "INSERT INTO customer (name, street_address1, street_address2, city, state, zip_code)" +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING customer_id;";
        Integer newId = jdbc.queryForObject(sql, Integer.class, newCustomer.getName(),
                newCustomer.getStreetAddress1(), newCustomer.getStreetAddress2(), newCustomer.getCity(),
                newCustomer.getState(), newCustomer.getZipCode());
        newCustomer.setCustomerId(newId);
        return getCustomer(newId);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        String sql = "UPDATE customer SET name = ?, street_address1 = ?, street_address2 = ?, city = ?," +
                "state = ?, zip_code = ? WHERE customer_id = ?";
        jdbc.update(sql, updatedCustomer.getName(), updatedCustomer.getStreetAddress1(),
                updatedCustomer.getStreetAddress2(), updatedCustomer.getCity(), updatedCustomer.getState(),
                updatedCustomer.getZipCode(), updatedCustomer.getCustomerId());
    }

    private Customer mapRowToCustomer(SqlRowSet results) {
        Customer customer = new Customer();
        customer.setCustomerId(results.getInt("customer_id"));
        customer.setName(results.getString("name"));
        customer.setStreetAddress1(results.getString("street_address1"));
        if (results.getString("street_address2") != null) {
            customer.setStreetAddress2(results.getString("street_address2"));
        }
        customer.setCity(results.getString("city"));
        customer.setState(results.getString("state"));
        customer.setZipCode(results.getString("zip_code"));
        return customer;
    }
}
