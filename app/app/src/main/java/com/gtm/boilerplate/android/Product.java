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

import java.io.Serializable;

/**
 * Represents a product in the store.
 * Implements Serializable to allow product objects to be passed between activities.
 */
public class Product implements Serializable {
    private String id;
    private String name;
    private String category;
    private double price;
    private int imageResource;
    private int quantity = 1; // Default quantity is 1 when a product is created.
    private String sku; // Stock Keeping Unit.

    /**
     * Constructs a new Product object.
     *
     * @param id The unique identifier for the product.
     * @param name The name of the product.
     * @param category The category the product belongs to.
     * @param price The price of the product.
     * @param imageResource The drawable resource ID for the product's image.
     * @param sku The SKU of the product.
     */
    public Product(String id, String name, String category, double price, int imageResource, String sku) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.imageResource = imageResource;
        this.sku = sku;
    }

    // Standard getters and setters for the product properties.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
