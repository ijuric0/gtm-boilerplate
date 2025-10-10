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

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * A utility class to manage a consistent Toolbar and a collapsible Bottom Sheet
 * for displaying event data across different activities.
 */
public class ToolbarAndBottomSheet {
    private static EventListener eventListener;

    /**
     * An interface to allow activities to listen for events triggered
     * from this utility class.
     */
    public interface EventListener {
        void onEvent(String eventName, Bundle params);
    }

    /**
     * Sets the event listener.
     * @param listener The listener to be notified of events.
     */
    public static void setEventListener(EventListener listener) {
        eventListener = listener;
    }

    /**
     * Initializes the Toolbar and Bottom Sheet for a given activity.
     * @param activity The activity where the components will be initialized.
     */
    public static void initializeToolbarAndBottomSheet(AppCompatActivity activity) {
        // Set up the custom toolbar.
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        activity.setSupportActionBar(toolbar);

        // Set up the 'Home' text to navigate to MainActivity.
        TextView homeText = toolbar.findViewById(R.id.home_text);
        homeText.setOnClickListener(view -> {
            Intent homeIntent = new Intent(activity, MainActivity.class);
            activity.startActivity(homeIntent);
        });

        // Set up the cart icon to navigate to CartActivity.
        ImageView cartIcon = toolbar.findViewById(R.id.cart_icon);
        cartIcon.setOnClickListener(view -> {
            Intent cartIntent = new Intent(activity, CartActivity.class);
            activity.startActivity(cartIntent);
        });

        // Get references to bottom sheet views.
        LinearLayout bottomSheet = activity.findViewById(R.id.bottom_sheet);
        TextView eventJsonTextView = activity.findViewById(R.id.event_json_textview);
        ImageView arrowIcon = activity.findViewById(R.id.arrow_icon);

        // Set the initial state of the bottom sheet (collapsed).
        eventJsonTextView.setVisibility(View.GONE);
        arrowIcon.setImageResource(R.drawable.down_arrow);

        // Get screen dimensions to calculate animation heights.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Calculate the initial height of the bottom sheet in pixels.
        int initialBottomSheetHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                55,
                activity.getResources().getDisplayMetrics()
        );

        // Apply the initial height.
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomSheet.getLayoutParams();
        params.height = initialBottomSheetHeight;
        bottomSheet.setLayoutParams(params);

        // Set the click listener to expand or collapse the bottom sheet.
        bottomSheet.setOnClickListener(view -> {
            if (eventJsonTextView.getVisibility() == View.GONE) {
                // Expand with animation.
                ValueAnimator animator = ValueAnimator.ofInt(bottomSheet.getHeight(), screenHeight * 4 / 10);
                animator.addUpdateListener(valueAnimator -> {
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    params.height = value;
                    bottomSheet.setLayoutParams(params);
                });
                animator.setDuration(300);
                animator.start();

                eventJsonTextView.setVisibility(View.VISIBLE);
                arrowIcon.setImageResource(R.drawable.up_arrow);

                // Populate the bottom sheet with event JSONs.
                LinearLayout eventJsonWrapper = activity.findViewById(R.id.event_json_wrapper);
                eventJsonWrapper.removeAllViews(); // Clear previous views.
                for (String jsonString : MyApplication.eventJsonList) {
                    TextView jsonTextView = generateJsonTextView(activity, jsonString);
                    eventJsonWrapper.addView(jsonTextView);
                }
            } else {
                // Collapse with animation.
                ValueAnimator animator = ValueAnimator.ofInt(bottomSheet.getHeight(), initialBottomSheetHeight);
                animator.addUpdateListener(valueAnimator -> {
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    params.height = value;
                    bottomSheet.setLayoutParams(params);
                });
                animator.setDuration(300);
                animator.start();

                eventJsonTextView.setVisibility(View.GONE);
                arrowIcon.setImageResource(R.drawable.down_arrow);
            }
        });

        // Set the current activity as the event listener.
        setEventListener((EventListener) activity);
    }

    /**
     * Adds an event to the display list in the bottom sheet.
     * @param activity The current activity.
     * @param eventName The name of the event.
     * @param params The parameters of the event.
     */
    public static void addEventToJsonList(AppCompatActivity activity, String eventName, Bundle params) {
        if (eventListener != null) {
            eventListener.onEvent(eventName, params);
        }

        LinearLayout eventJsonWrapper = activity.findViewById(R.id.event_json_wrapper);
        String jsonString = getDemoJson(eventName, params);
        TextView jsonTextView = generateJsonTextView(activity, jsonString);
        eventJsonWrapper.addView(jsonTextView);
    }

    /**
     * Converts an event name and its parameters into a pretty-printed JSON string.
     * @param eventName The name of the event.
     * @param params The Bundle of event parameters.
     * @return A formatted JSON string.
     */
    public static String getDemoJson(String eventName, Bundle params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event_name", eventName);
        JsonObject paramsJson = new JsonObject();
        for (String key : params.keySet()) {
            paramsJson.addProperty(key, String.valueOf(params.get(key)));
        }
        jsonObject.add("params", paramsJson);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    /**
     * Concatenates all event JSON strings from the global list.
     * @param activity The current activity.
     * @return A single string containing all event JSONs.
     */
    public static String getJsonText(AppCompatActivity activity) {
        StringBuilder sb = new StringBuilder();
        for (String jsonString : MyApplication.eventJsonList) {
            sb.append(jsonString + "\n");
        }
        return sb.toString();
    }

    /**
     * Creates and styles a TextView to display a JSON string.
     * @param activity The current activity.
     * @param jsonString The JSON string to display.
     * @return A styled TextView.
     */
    private static TextView generateJsonTextView(AppCompatActivity activity, String jsonString) {
        TextView eventJsonTextView = new TextView(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);
        eventJsonTextView.setLayoutParams(params);
        eventJsonTextView.setText(jsonString);
        eventJsonTextView.setTextColor(Color.WHITE);
        eventJsonTextView.setBackgroundColor(Color.parseColor("#1e1628"));
        eventJsonTextView.setPadding(15, 10, 15, 10);
        eventJsonTextView.setAlpha(0.7f);
        return eventJsonTextView;
    }
}
