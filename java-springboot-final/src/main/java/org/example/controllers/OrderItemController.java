package org.example.controllers;

import org.example.daos.OrderItemDao;
import org.example.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/order-items")
@PreAuthorize("isAuthenticated()")
public class OrderItemController {

    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * Retrieves all order items or filters them by order ID.
     *
     * @param orderId Optional order ID to filter by.
     * @return A list of order items.
     */
    @GetMapping
    public List<OrderItem> getAll(
            @RequestParam(required = false) Integer orderId) {

        if (orderId != null) {
            return orderItemDao.getOrderItemsByOrderId(orderId);
        }

        return orderItemDao.getAllOrderItems();
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param id The ID of the order item.
     * @return The requested order item.
     */
    @GetMapping("/{id}")
    public OrderItem get(@PathVariable int id) {
        OrderItem orderItem = orderItemDao.getOrderItemById(id);

        if (orderItem == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order item not found");
        }

        return orderItem;
    }

    /**
     * Creates a new order item.
     *
     * @param orderItem The order item to create.
     * @return The newly created order item.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItem create(@RequestBody OrderItem orderItem) {
        return orderItemDao.createOrderItem(orderItem);
    }

    /**
     * Updates an existing order item.
     *
     * @param id The ID of the order item to update.
     * @param orderItem The updated order item information.
     * @return The updated order item.
     */
    @PutMapping("/{id}")
    public OrderItem update(@PathVariable int id,
                            @RequestBody OrderItem orderItem) {

        orderItem.setId(id);

        OrderItem updatedOrderItem =
                orderItemDao.updateOrderItem(orderItem);

        if (updatedOrderItem == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order item not found");
        }

        return updatedOrderItem;
    }

    /**
     * Deletes an order item by its ID.
     *
     * @param id The ID of the order item to delete.
     * @return The number of rows deleted.
     */
    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {
        int rowsAffected = orderItemDao.deleteOrderItem(id);

        if (rowsAffected == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order item not found");
        }

        return rowsAffected;
    }


}
