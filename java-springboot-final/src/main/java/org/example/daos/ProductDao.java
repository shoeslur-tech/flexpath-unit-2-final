
package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;

    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Get all products from the database
    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM products ORDER BY id";
        return jdbcTemplate.query(sql, this::mapToProduct);
    }


    // Get a product by its ID
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    this::mapToProduct,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    // Create a new product
    public Product createProduct(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());

            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new DaoException("Failed to retrieve the new product ID.");
        }

        return getProductById(generatedId.intValue());
    }


    // Update an existing product
    public Product updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(
                sql,
                product.getName(),
                product.getPrice(),
                product.getId()
        );

        if (rowsAffected == 0) {
            return null;
        }

        return getProductById(product.getId());
    }


    // Delete a product by its ID
    public int deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }

    // Convert a database row into a Product object
    private Product mapToProduct(ResultSet resultSet, int rowNumber)
            throws SQLException {

        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("price")
        );
    }
}
