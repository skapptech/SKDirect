/*
 * Copyright 2019 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.support.v7.layoutmanager;

import android.content.Context;
import android.graphics.LinearGradient;

import com.flipkart.android.proteus.support.v7.RecyclerViewModule;
import com.flipkart.android.proteus.support.v7.widget.ProteusRecyclerView;
import com.flipkart.android.proteus.value.ObjectValue;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * @author adityasharat
 */
public class ProteusLinearLayoutManager extends GridLayoutManager {

    private static final String ATTRIBUTE_ORIENTATION = "orientation";
    public static final LayoutManagerBuilder<ProteusLinearLayoutManager> BUILDER = new LayoutManagerBuilder<ProteusLinearLayoutManager>() {

        @NonNull
        @Override
        public ProteusLinearLayoutManager create(@NonNull ProteusRecyclerView view, @NonNull ObjectValue config, @NonNull String type) {
            int orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.VERTICAL);
            int spanCount=1;

            if (type.equalsIgnoreCase(RecyclerViewModule.LAYOUT_MANAGER_GRID)) {
                orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.VERTICAL);
                spanCount = 1;
            } else if (type.equalsIgnoreCase(RecyclerViewModule.LAYOUT_MANAGER_GRID_HORIZONTAL)) {
                orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.HORIZONTAL);
                spanCount = 1;
            }else  if (type.equalsIgnoreCase(RecyclerViewModule.LAYOUT_MANAGER_GRID2)) {
                orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.VERTICAL);
                spanCount = 2;
            } else if (type.equalsIgnoreCase(RecyclerViewModule.LAYOUT_MANAGER_GRID_HORIZONTAL2)) {
                orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.HORIZONTAL);
                spanCount = 2;
            }else  if (type.equalsIgnoreCase(RecyclerViewModule.LAYOUT_MANAGER_GRID3)) {
                orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.VERTICAL);
                spanCount = 3;
            } else if (type.equalsIgnoreCase(RecyclerViewModule.LAYOUT_MANAGER_GRID_HORIZONTAL3)) {
                orientation = config.getAsInteger(ATTRIBUTE_ORIENTATION, GridLayoutManager.HORIZONTAL);
                spanCount = 3;
            }
            return new ProteusLinearLayoutManager(view.getContext(),spanCount, orientation, false);
        }
    };

    public ProteusLinearLayoutManager(Context context,int spanCount ,int orientation, boolean reverseLayout) {
        super(context, spanCount,orientation, reverseLayout);
    }
}
