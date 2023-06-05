package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcLineItemDao implements LineItemDao {

    private JdbcTemplate jdbc;
    public JdbcLineItemDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public List<LineItem> getLineItemsBySale(int saleId) {
        List<LineItem> items = new ArrayList<>();
        String sql = "SELECT line_item_id, sale_id, l.product_id, quantity, name, price " +
                "FROM line_item l " +
                "JOIN product p ON p.product_id = l.product_id " +
                "WHERE sale_id = ?";
        SqlRowSet results = jdbc.queryForRowSet(sql, saleId);
        while (results.next()) {
            LineItem item = mapRowToLineItem(results);
            items.add(item);
        }
        return items;
    }
    private LineItem mapRowToLineItem(SqlRowSet results) {
        LineItem item = new LineItem();
        item.setLineItemId(results.getInt("line_item_id"));
        item.setSaleId(results.getInt("sale_id"));
        item.setProductId(results.getInt("product_id"));
        item.setQuantity(results.getInt("quantity"));
        item.setProductName(results.getString("name"));
        item.setPrice(results.getBigDecimal("price"));
        return item;
    }
}
