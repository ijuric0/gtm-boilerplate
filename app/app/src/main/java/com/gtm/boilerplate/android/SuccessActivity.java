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
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.UUID;

/**
 * Displays a success message after a purchase is completed, including a unique order ID.
 */
public class SuccessActivity extends AppCompatActivity implements ToolbarAndBottomSheet.EventListener {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // Initialize Firebase Analytics.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Set up the shared toolbar and bottom sheet UI.
        ToolbarAndBottomSheet.initializeToolbarAndBottomSheet(this);

        // Generate and display a unique order ID.
        String orderId = generateUniqueOrderId();
        TextView orderIdTextView = findViewById(R.id.order_id_textview);
        orderIdTextView.setText("Order ID: " + orderId);

        // Retrieve the JSON string of the purchase event passed from the previous activity.
        String purchaseJson = getIntent().getStringExtra("PURCHASE_JSON");

    }

    /**
     * Generates a unique identifier for the order.
     * @return A random 16-character string.
     */
    private String generateUniqueOrderId() {
        return UUID.randomUUID().toString().substring(0, 16);
    }

    /**
     * Callback for the EventListener interface. This method is required by the interface
     * but is not used in this specific activity.
     * @param eventName The name of the event.
     * @param params The bundle of parameters for the event.
     */
    @Override
    public void onEvent(String eventName, Bundle params) {
        // Not used in this activity, but required to implement the EventListener interface.
    }
}
