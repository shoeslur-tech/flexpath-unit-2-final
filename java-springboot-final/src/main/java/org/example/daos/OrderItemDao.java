package org.example.daos;

import org.example.models.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.exceptions.DaoException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class OrderItemDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Retrieves all order items from the database.
     *
     * @return A list of all order items.
     */
    public List<OrderItem> getAllOrderItems() {
        String sql = "SELECT * FROM order_items ORDER BY id";

        return jdbcTemplate.query(sql, this::mapToOrderItem);
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param id The ID of the order item.
     * @return The order item, or null if not found.
     */
    public OrderItem getOrderItemById(int id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql, this::mapToOrderItem, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order item in the database.
     *
     * @param orderItem The order item to create.
     * @return The newly created order item.
     */
    public OrderItem createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, orderItem.getOrderId());
            statement.setInt(2, orderItem.getProductId());
            statement.setInt(3, orderItem.getQuantity());

            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new DaoException("Failed to retrieve the new order item ID.");
        }

        return getOrderItemById(generatedId.intValue());
    }

    /**
     * Updates an existing order item in the database.
     *
     * @param orderItem The order item to update.
     * @return The updated order item, or null if not found.
     */
    public OrderItem updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(
                sql,
                orderItem.getOrderId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getId()
        );

        if (rowsAffected == 0) {
            return null;
        }

        return getOrderItemById(orderItem.getId());
    }

    /**
     * Deletes an order item from the database.
     *
     * @param id The ID of the order item to delete.
     * @return The number of rows deleted.
     */
    public int deleteOrderItem(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Retrieves all order items associated with a specific order.
     *
     * @param orderId The ID of the order.
     * @return A list of order items belonging to the order.
     */
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ? ORDER BY id";

        return jdbcTemplate.query(sql, this::mapToOrderItem, orderId);
    }


    /**
     * Maps a database row to an OrderItem object.
     */
    private OrderItem mapToOrderItem(ResultSet resultSet, int rowNumber)
            throws SQLException {

        return new OrderItem(
                resultSet.getInt("id"),
                resultSet.getInt("order_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("quantity")
        );
    }

}
