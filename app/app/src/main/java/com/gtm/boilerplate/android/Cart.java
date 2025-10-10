/**
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soteria.firebaseapp.android;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the shopping cart using a Singleton pattern to ensure only one instance
 * of the cart exists throughout the application.
 */
public class Cart {
    // The single instance of the Cart.
    private static Cart instance;
    // List to hold the products added to the cart.
    private List<Product> items;

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the list of items.
     */
    private Cart() {
        items = new ArrayList<>();
    }

    /**
     * Provides a global point of access to the Cart instance.
     * Creates the instance if it doesn't exist yet (thread-safe).
     *
     * @return The single instance of the Cart.
     */
    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    /**
     * Adds a product to the cart.
     *
     * @param product The product to be added.
     */
    public void addItem(Product product) {
        items.add(product);
    }

    /**
     * Retrieves the list of all items currently in the cart.
     *
     * @return A List of Products in the cart.
     */
    public List<Product> getItems() {
        return items;
    }

    /**
     * Clears all items from the cart.
     */
    public void clearCart() {
        items.clear();
    }

    /**
     * Finds a product in the cart by its ID.
     *
     * @param productId The ID of the product to find.
     * @return The Product object if found, otherwise null.
     */
    public Product findProduct(String productId) {
        for (Product p : items) {
            if (p.getId().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Removes a specific product from the cart.
     *
     * @param product The product to be removed.
     */
    public void removeItem(Product product) {
        items.remove(product);
    }
}
