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

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Displays the details of a single product and allows the user to add it to the cart.
 */
public class ProductDetailsActivity extends AppCompatActivity implements ToolbarAndBottomSheet.EventListener {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize Firebase Analytics.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Set up the shared toolbar and bottom sheet UI.
        ToolbarAndBottomSheet.initializeToolbarAndBottomSheet(this);

        // Retrieve the product object passed from the previous activity.
        Product product = (Product) getIntent().getSerializableExtra("PRODUCT");

        // Populate the UI elements with the product's details.
        TextView productNameTextView = findViewById(R.id.product_name_textview);
        productNameTextView.setText(product.getName());

        ImageView productImageView = findViewById(R.id.product_imageview);
        int imageResourceId = product.getImageResource();
        Log.d("ProductDetailsActivity", "Image resource ID: " + imageResourceId);
        productImageView.setImageResource(imageResourceId);

        TextView productPriceTextView = findViewById(R.id.product_price_textview);
        productPriceTextView.setText("$" + product.getPrice());

        // Log the 'view_item' event to Firebase Analytics.
        Bundle viewItemParams = getViewItemBundle(product);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, viewItemParams);
        ToolbarAndBottomSheet.addEventToJsonList(this, FirebaseAnalytics.Event.VIEW_ITEM, viewItemParams);

        // Set up the "Add to Cart" button.
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        addToCartButton.setOnClickListener(view -> {
            // Check if the product is already in the cart.
            Product existingProduct = Cart.getInstance().findProduct(product.getId());

            if (existingProduct != null) {
                // If it exists, increment its quantity.
                existingProduct.setQuantity(existingProduct.getQuantity() + 1);
            } else {
                // Otherwise, add the new product to the cart.
                Cart.getInstance().addItem(product);
            }

            // Log the 'add_to_cart' event to Firebase Analytics.
            Bundle addToCartParams = getAddToCartBundle(product);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, addToCartParams);
            ToolbarAndBottomSheet.addEventToJsonList(this, FirebaseAnalytics.Event.ADD_TO_CART, addToCartParams);

            // Animate the cart icon in the toolbar to give visual feedback.
            Toolbar toolbar = findViewById(R.id.my_toolbar);
            ImageView cartIcon = toolbar.findViewById(R.id.cart_icon);
            Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
            cartIcon.startAnimation(bounceAnimation);
        });
    }

    /**
     * Creates a Bundle with item details for a 'view_item' Firebase Analytics event.
     * @param product The product being viewed.
     * @return A Bundle containing the item parameters.
     */
    private Bundle getViewItemBundle(Product product) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, product.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, product.getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getCategory());
        bundle.putDouble(FirebaseAnalytics.Param.PRICE, product.getPrice());
        return bundle;
    }

    /**
     * Creates a Bundle with item details for an 'add_to_cart' Firebase Analytics event.
     * @param product The product being added to the cart.
     * @return A Bundle containing the item parameters.
     */
    private Bundle getAddToCartBundle(Product product) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, product.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, product.getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getCategory());
        bundle.putDouble(FirebaseAnalytics.Param.PRICE, product.getPrice());
        bundle.putLong(FirebaseAnalytics.Param.QUANTITY, 1);
        return bundle;
    }

    /**
     * Callback for the EventListener interface. Adds event data to the global list for display.
     * @param eventName The name of the event.
     * @param params The bundle of parameters for the event.
     */
    @Override
    public void onEvent(String eventName, Bundle params) {
        String jsonString = ToolbarAndBottomSheet.getDemoJson(eventName, params);
        MyApplication.eventJsonList.add(0, jsonString);
    }
}
