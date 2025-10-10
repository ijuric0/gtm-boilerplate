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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom ArrayAdapter for displaying Product objects in a ListView.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    /**
     * Constructor for the ProductAdapter.
     *
     * @param context The current context.
     * @param products The list of products to display.
     */
    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position.
        Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item, parent, false);
        }

        // Lookup view for data population.
        ImageView productImageView = convertView.findViewById(R.id.product_imageview);
        TextView productNameTextView = convertView.findViewById(R.id.product_name_textview);
        TextView productPriceTextView = convertView.findViewById(R.id.product_price_textview);

        // Populate the data into the template view using the data object.
        productImageView.setImageResource(product.getImageResource());
        productNameTextView.setText(product.getName());
        productPriceTextView.setText(String.format("$%.2f", product.getPrice()));

        // Return the completed view to render on screen.
        return convertView;
    }
}
