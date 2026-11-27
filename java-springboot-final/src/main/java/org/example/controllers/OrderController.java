package org.example.controllers;

import java.security.Principal;
import java.util.List;
import org.example.daos.OrderDao;
import org.example.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {

    @Autowired
    private OrderDao orderDao;


    /**
     * Retrieves all orders or filters orders by username.
     */
    @GetMapping
    public List<Order> getAll(
            @RequestParam(required = false) String username) {

        if (username != null) {
            return orderDao.getOrdersByUsername(username);
        }

        return orderDao.getAllOrders();
    }

    /**
     * Retrieves a specific order by its ID.
     */
    @GetMapping("/{id}")
    public Order get(@PathVariable int id) {
        Order order = orderDao.getOrderById(id);

        if (order == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order not found");
        }

        return order;
    }

    /**
     * Creates a new order for the authenticated user.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody Order order, Principal principal) {

        order.setUsername(principal.getName());

        return orderDao.createOrder(order);
    }

    /**
     * Updates an existing order.
     */
    
    @PutMapping("/{id}")
    public Order update(@PathVariable int id,
                        @RequestBody Order order) {

        order.setId(id);

        Order updatedOrder = orderDao.updateOrder(order);

        if (updatedOrder == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order not found");
        }

        return updatedOrder;
    }


    /**
     * Deletes an existing order by its ID.
     */
    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {

        int rowsAffected = orderDao.deleteOrder(id);

        if (rowsAffected == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order not found");
        }

        return rowsAffected;
    }

}
