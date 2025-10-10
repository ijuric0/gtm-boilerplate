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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages the user's shopping cart, displaying items, total price,
 * and handling the checkout process.
 */
public class CartActivity extends AppCompatActivity implements ToolbarAndBottomSheet.EventListener {

    private FirebaseAnalytics firebaseAnalytics;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Set up the toolbar and bottom sheet UI components.
        ToolbarAndBottomSheet.initializeToolbarAndBottomSheet(this);

        // Get the list of items from the cart singleton.
        List<Product> cartItems = Cart.getInstance().getItems();

        // Calculate and display the total price.
        double cartTotal = calculateCartTotal(cartItems);
        TextView cartTotalTextView = findViewById(R.id.cart_total_textview);
        cartTotalTextView.setText("Total: $" + cartTotal);

        checkoutButton = findViewById(R.id.checkout_button);

        // Get the container for cart item views.
        LinearLayout cartItemsContainer = findViewById(R.id.cart_items_container);

        // Dynamically create and add a view for each item in the cart.
        for (Product product: cartItems) {
            View itemView = getLayoutInflater().inflate(R.layout.cart_item, null);
            TextView productNameTextView = itemView.findViewById(R.id.product_name_textview);
            productNameTextView.setText(product.getName());
            TextView productPriceTextView = itemView.findViewById(R.id.product_price_textview);
            productPriceTextView.setText(String.valueOf(product.getPrice()));
            ImageView productImageView = itemView.findViewById(R.id.product_imageview);
            productImageView.setImageResource(product.getImageResource());

            TextView quantityTextView = itemView.findViewById(R.id.quantity_textview);
            quantityTextView.setText(String.valueOf(product.getQuantity()));
            Button increaseButton = itemView.findViewById(R.id.increase_button);
            Button decreaseButton = itemView.findViewById(R.id.decrease_button);

            // Set listener to handle increasing item quantity.
            increaseButton.setOnClickListener(v -> {
                product.setQuantity(product.getQuantity() + 1);
                quantityTextView.setText(String.valueOf(product.getQuantity()));
                updateCartTotal();
            });

            // Set listener to handle decreasing item quantity or removing the item.
            decreaseButton.setOnClickListener(v -> {
                if (product.getQuantity() > 1) {
                    product.setQuantity(product.getQuantity() - 1);
                    quantityTextView.setText(String.valueOf(product.getQuantity()));
                    updateCartTotal();
                } else {
                    // If quantity is 1, remove the item completely.
                    Cart.getInstance().removeItem(product);
                    cartItemsContainer.removeView(itemView);
                    updateCartTotal();
                }
            });

            cartItemsContainer.addView(itemView);
        }

        // Set listener for the checkout button.
        checkoutButton.setOnClickListener(view -> {
            if (checkoutButton.getText().toString().equals("Add products")) {
                // If cart is empty, navigate back to the main product list.
                Intent mainIntent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                // If cart has items, proceed with checkout.
                performCheckoutProcess();

                String orderId = generateUniqueOrderId();

                // Log the purchase event to Firebase Analytics.
                Bundle purchaseBundle = getPurchaseBundle(cartTotal, orderId);
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, purchaseBundle);
                ToolbarAndBottomSheet.addEventToJsonList(this, FirebaseAnalytics.Event.PURCHASE, purchaseBundle);

                // Navigate to the success screen.
                Intent successIntent = new Intent(CartActivity.this, SuccessActivity.class);
                successIntent.putExtra("PURCHASE_JSON", ToolbarAndBottomSheet.getDemoJson(FirebaseAnalytics.Event.PURCHASE, purchaseBundle));
                startActivity(successIntent);

                // Clear the cart after successful checkout.
                Cart.getInstance().clearCart();
                cartItemsContainer.removeAllViews();
                updateCheckoutButtonState();
            }
        });

        // Log the view_cart event when the activity is created.
        Bundle viewCartBundle = getViewCartBundle(cartItems);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_CART, viewCartBundle);
        ToolbarAndBottomSheet.addEventToJsonList(this, FirebaseAnalytics.Event.VIEW_CART, viewCartBundle);

        // Set the initial state of the checkout button.
        updateCheckoutButtonState();
    }

    /**
     * Placeholder for the checkout logic.
     */
    private void performCheckoutProcess() {
        // Implement your checkout logic here (e.g., payment processing).
    }

    /**
     * Generates a unique ID for the order.
     * @return A random 16-character string.
     */
    private String generateUniqueOrderId() {
        return UUID.randomUUID().toString().substring(0, 16);
    }

    /**
     * Calculates the total price of all items in the cart.
     * @param cartItems The list of products in the cart.
     * @return The total price as a double.
     */
    private double calculateCartTotal(List<Product> cartItems) {
        double total = 0;
        for (Product product: cartItems) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    /**
     * Creates a Bundle with purchase details for Firebase Analytics.
     * @param cartTotal The total value of the purchase.
     * @param orderId The unique ID for the transaction.
     * @return A Bundle containing purchase event parameters.
     */
    private Bundle getPurchaseBundle(double cartTotal, String orderId) {
        Bundle bundle = new Bundle();

        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId);
        bundle.putString(FirebaseAnalytics.Param.AFFILIATION, "Store Name");
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, cartTotal);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");

        // Create a list of bundles, where each bundle represents an item.
        ArrayList<Bundle> items = new ArrayList<>(Cart.getInstance().getItems().size());
        for (Product product: Cart.getInstance().getItems()) {
            Bundle item = new Bundle();
            item.putString(FirebaseAnalytics.Param.ITEM_ID, product.getId());
            item.putString(FirebaseAnalytics.Param.ITEM_NAME, product.getName());
            item.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getCategory());
            item.putDouble(FirebaseAnalytics.Param.PRICE, product.getPrice());
            item.putLong(FirebaseAnalytics.Param.QUANTITY, product.getQuantity());
            item.putString(FirebaseAnalytics.Param.ITEM_VARIANT, product.getSku());
            items.add(item);
        }

        bundle.putSerializable(FirebaseAnalytics.Param.ITEMS, items);

        return bundle;
    }

    /**
     * Recalculates and updates the total price displayed on the UI.
     * Also updates the state of the checkout button.
     */
    private void updateCartTotal() {
        double cartTotal = calculateCartTotal(Cart.getInstance().getItems());
        TextView cartTotalTextView = findViewById(R.id.cart_total_textview);
        cartTotalTextView.setText("Total: $" + cartTotal);

        updateCheckoutButtonState();
    }

    /**
     * Creates a Bundle with cart items for the 'view_cart' Firebase Analytics event.
     * @param cartItems The list of products currently in the cart.
     * @return A Bundle containing the items for the view_cart event.
     */
    private Bundle getViewCartBundle(List<Product> cartItems) {
        Bundle bundle = new Bundle();

        // Create a list of bundles, where each bundle represents an item.
        ArrayList<Bundle> items = new ArrayList<>(cartItems.size());
        for (Product product: cartItems) {
            Bundle item = new Bundle();
            item.putString(FirebaseAnalytics.Param.ITEM_ID, product.getId());
            item.putString(FirebaseAnalytics.Param.ITEM_NAME, product.getName());
            item.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getCategory());
            item.putDouble(FirebaseAnalytics.Param.PRICE, product.getPrice());
            item.putLong(FirebaseAnalytics.Param.QUANTITY, product.getQuantity());
            item.putString(FirebaseAnalytics.Param.ITEM_VARIANT, product.getSku());
            items.add(item);
        }

        bundle.putSerializable(FirebaseAnalytics.Param.ITEMS, items);

        return bundle;
    }

    /**
     * Updates the checkout button's text and enabled state based on whether
     * the cart is empty or not.
     */
    private void updateCheckoutButtonState() {
        int cartItemCount = Cart.getInstance().getItems().size();
        if (cartItemCount > 0) {
            checkoutButton.setEnabled(true);
            checkoutButton.setText("Checkout");
        } else {
            checkoutButton.setText("Add products");
        }
    }

    /**
     * Callback for the EventListener interface. Adds event data to the global list.
     * @param eventName The name of the event.
     * @param params The bundle of parameters for the event.
     */
    @Override
    public void onEvent(String eventName, Bundle params) {
        String jsonString = ToolbarAndBottomSheet.getDemoJson(eventName, params);
        MyApplication.eventJsonList.add(0, jsonString);
    }
}
