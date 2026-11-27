package org.example.daos;

import org.example.models.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.dao.EmptyResultDataAccessException;
import org.example.exceptions.DaoException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
        /**
     * Retrieves all orders from the database.
     *
     * @return A list of all orders.
     */
    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders ORDER BY id";
        return jdbcTemplate.query(sql, this::mapToOrder);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order.
     * @return The order, or null if not found.
     */
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql, this::mapToOrder, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order in the database.
     *
     * @param order The order to create.
     * @return The newly created order.
     */
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (username) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, order.getUsername());

            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new DaoException("Failed to retrieve the new order ID.");
        }

        return getOrderById(generatedId.intValue());
    }

    /**
     * Updates an existing order in the database.
     *
     * @param order The order to update.
     * @return The updated order, or null if not found.
     */
    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET username = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(
                sql, order.getUsername(), order.getId());

        if (rowsAffected == 0) {
            return null;
        }

        return getOrderById(order.getId());
    }

    /**
     * Deletes an order from the database.
     *
     * @param id The ID of the order to delete.
     * @return The number of rows deleted.
     */
    public int deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Retrieves all orders associated with a username.
     *
     * @param username The username to search for.
     * @return A list of orders belonging to the user.
     */
    public List<Order> getOrdersByUsername(String username) {
        String sql = "SELECT * FROM orders WHERE username = ? ORDER BY id";

        return jdbcTemplate.query(sql, this::mapToOrder, username);
    }



    /**
     * Maps a database row to an Order object.
     */
    private Order mapToOrder(ResultSet resultSet, int rowNumber)
            throws SQLException {

        return new Order(
                resultSet.getInt("id"),
                resultSet.getString("username")
        );
    }
}
