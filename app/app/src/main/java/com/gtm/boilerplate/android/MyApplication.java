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

import android.app.Application;
import java.util.List;
import java.util.ArrayList;

/**
 * Custom Application class to hold global application state.
 * This is used to maintain a list of analytics events that can be displayed
 * across different activities.
 */
public class MyApplication extends Application {

    // A static list to hold JSON representations of tracked analytics events.
    public static List<String> eventJsonList;

    // Static initializer block to create the ArrayList instance when the class is loaded.
    static {
        eventJsonList = new ArrayList<>();
    }

    /**
     * Called when the application is starting, before any other application objects have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
