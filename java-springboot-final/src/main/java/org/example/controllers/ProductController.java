package org.example.controllers;

import org.example.models.Product;
import org.example.daos.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Controller for products.
 * Handles HTTP requests related to products.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/products")
@PreAuthorize("isAuthenticated()")
public class ProductController {

    @Autowired
    private ProductDao productDao;

    /**
     * Gets all products.
     *
     * @return A list of all products.
     */
    @GetMapping
    public List<Product> getAll() {
        return productDao.getAllProducts();
    }

    /**
     * Gets a product by its ID.
     *
     * @param id The ID of the product.
     * @return The product with the given ID.
     */
    @GetMapping(path = "/{id}")
    public Product get(@PathVariable int id) {
        Product product = productDao.getProductById(id);

        if (product == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found");
        }

        return product;
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return The created product.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productDao.createProduct(product);
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the product.
     * @param product The updated product information.
     * @return The updated product.
     */
    @PutMapping(path = "/{id}")
    public Product update(@PathVariable int id, @RequestBody Product product) {

        product.setId(id);

        Product updatedProduct = productDao.updateProduct(product);

        if (updatedProduct == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found");
        }

        return updatedProduct;
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return The number of rows deleted.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {

        int rowsAffected = productDao.deleteProduct(id);

        if (rowsAffected == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found");
        }

        return rowsAffected;
    }
}
