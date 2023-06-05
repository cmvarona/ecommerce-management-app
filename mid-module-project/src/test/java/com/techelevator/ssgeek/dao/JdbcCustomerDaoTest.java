package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JdbcCustomerDaoTest extends BaseDaoTests {

    private JdbcCustomerDao dao;
    private static final Customer CUSTOMER_1 = mapValuesToCustomer(1, "Customer 1", "Addr 1-1",
            "Addr 1-2", "City 1", "S1", "11111");
    private static final Customer CUSTOMER_2 = mapValuesToCustomer(2, "Customer 2", "Addr 2-1",
            "Addr 2-2", "City 2", "S2", "22222");
    private static final Customer CUSTOMER_3 = mapValuesToCustomer(3, "Customer 3", "Addr 3-1",
            null, "City 3", "S3", "33333");
    private static final Customer CUSTOMER_4 = mapValuesToCustomer(4, "Customer 4", "Addr 4-1",
            null, "City 4", "S4", "44444");

    @Before
    public void setup() {
        dao = new JdbcCustomerDao(dataSource);
    }

    @Test
    public void getCustomer_returns_correct_customer_for_id() {
        Customer c1 = CUSTOMER_1;
        Customer testCustomer = dao.getCustomer(CUSTOMER_1.getCustomerId());
        Assert.assertNotNull(testCustomer);
        assertCustomersMatch(c1, testCustomer);
    }

    @Test
    public void getCustomer_returns_null_when_id_not_found() {
        Customer testCustomer = dao.getCustomer(7);
        Assert.assertNull(testCustomer);
    }

    @Test
    public void getCustomers_returns_list_of_all_customers() {
        List<Customer> customers = dao.getCustomers();
        Assert.assertEquals(4, customers.size());
        assertCustomersMatch(CUSTOMER_1, customers.get(0));
        assertCustomersMatch(CUSTOMER_2, customers.get(1));
        assertCustomersMatch(CUSTOMER_3, customers.get(2));
        assertCustomersMatch(CUSTOMER_4, customers.get(3));
    }

    @Test
    public void createCustomer_returns_customer_with_id_and_correct_values() {
        Customer testCustomer = new Customer(0, "Customer 5", "Address 5-1",
                null, "City 5", "S4", "55555");
        Customer createdCustomer = dao.createCustomer(testCustomer);
        Assert.assertNotNull(createdCustomer);
        Integer id = createdCustomer.getCustomerId();
        Assert.assertTrue(id > 0);
        Assert.assertEquals(5, createdCustomer.getCustomerId());
        assertCustomersMatch(testCustomer, createdCustomer);
        Customer retrievedCustomer = dao.getCustomer(id);
        assertCustomersMatch(createdCustomer, retrievedCustomer);
    }


    @Test
    public void updateCustomer_returns_customer_with_updated_values() {
        Customer c1 = dao.getCustomer(CUSTOMER_1.getCustomerId());
        Assert.assertNotNull(c1);
        c1.setCity("City 10");
        dao.updateCustomer(c1);
        Assert.assertEquals("City 10", c1.getCity());
    }

    private static Customer mapValuesToCustomer(int customerId, String name, String address1, String address2,
               String city, String state, String zip) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setName(name);
        customer.setStreetAddress1(address1);
        if (address2 != null) {
            customer.setStreetAddress2(address2);
        }
        customer.setCity(city);
        customer.setState(state);
        customer.setZipCode(zip);
        return customer;
    }

    private void assertCustomersMatch(Customer expected, Customer actual) {
        Assert.assertEquals(expected.getCustomerId(), actual.getCustomerId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getStreetAddress1(), actual.getStreetAddress1());
        Assert.assertEquals(expected.getStreetAddress2(), actual.getStreetAddress2());
        Assert.assertEquals(expected.getCity(), actual.getCity());
        Assert.assertEquals(expected.getState(), actual.getState());
        Assert.assertEquals(expected.getZipCode(), actual.getZipCode());
    }
}
