package com.skdirect.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.flipkart.android.proteus.LayoutManager;
import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.StyleManager;
import com.flipkart.android.proteus.Styles;
import com.flipkart.android.proteus.exceptions.ProteusInflateException;
import com.flipkart.android.proteus.gson.ProteusTypeAdapterFactory;
import com.flipkart.android.proteus.support.design.DesignModule;
import com.flipkart.android.proteus.support.v4.SupportV4Module;
import com.flipkart.android.proteus.support.v7.CardViewModule;
import com.flipkart.android.proteus.support.v7.RecyclerViewModule;
import com.flipkart.android.proteus.support.v7.layoutmanager.ProteusLinearLayoutManager;
import com.flipkart.android.proteus.value.DrawableValue;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;
import com.google.gson.stream.JsonReader;
import com.skdirect.R;
import com.skdirect.api.CircleViewParser;
import com.skdirect.api.ImageLoaderTarget;
import com.skdirect.api.ProteusManager;
import com.skdirect.databinding.ActivityProteusBinding;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class ProteusActivity extends AppCompatActivity implements ProteusManager.Listener {
    ProteusActivity activity;
    ActivityProteusBinding binding;

    private ProteusManager proteusManager;
    private ProteusLayoutInflater layoutInflater;

    ObjectValue data;
    Layout layout;
    Styles styles;
    Map<String, Layout> layouts;

    private StyleManager styleManager = new StyleManager() {

        @Nullable
        @Override
        protected Styles getStyles() {
            return styles;
        }
    };

    private LayoutManager layoutManager = new LayoutManager() {

        @Nullable
        @Override
        protected Map<String, Layout> getLayouts() {
            return layouts;
        }
    };


    ProteusView view;
    private ViewGroup container;
    Proteus proteus;
    private ProteusLayoutInflater.ImageLoader loader = new ProteusLayoutInflater.ImageLoader() {
        @Override
        public void getBitmap(ProteusView view, String url, final DrawableValue.AsyncCallback callback) {
            Glide.with(ProteusActivity.this)
                    .load(url)
                    .placeholder(R.drawable.ic_direct_active)
                    .error(R.drawable.ic_direct_active)
                    .into(new ImageLoaderTarget(callback));
        }
    };
    public void showToast(){
        Utils.setToast(activity,"Showing Toast");
    }
    private ProteusLayoutInflater.Callback callback = new ProteusLayoutInflater.Callback() {

        @NonNull
        @Override
        public ProteusView onUnknownViewType(ProteusContext context, String type, Layout layout, ObjectValue data, int index) {
            // TODO: instead return some implementation of an unknown view
            throw new ProteusInflateException("Unknown view type '" + type + "' cannot be inflated");
        }

        @Override
        public void onEvent(String event, Value value, ProteusView view) {
            ObjectValue objectValue = value.getAsObject();
            Log.i("ProteusEvent", event+" - "+objectValue.get("type").getAsString());
            String type = objectValue.get("type").getAsString();
            if (event.equalsIgnoreCase("onClick")){
                if (type.equalsIgnoreCase("showToast")){
                    showToast();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_proteus);
        activity = this;
       // proteusManager = MyApplication.getInstance().getProteusManager();
        proteus = new ProteusBuilder()
                .register(SupportV4Module.create())
                .register(RecyclerViewModule.create())
                .register(CardViewModule.create())
                .register(DesignModule.create())
                .build();
        //ProteusContext context = proteusManager.getProteus().createContextBuilder(this).build();
        ProteusTypeAdapterFactory adapter = new ProteusTypeAdapterFactory(this);
        ProteusTypeAdapterFactory.PROTEUS_INSTANCE_HOLDER.setProteus(proteus);
        /*final String LAYOUT = "{\n" +
                "  \"type\": \"LinearLayout\",\n" +
                "  \"android\": \"http://schemas.android.com/apk/res/android\",\n" +
                "  \"id\": \"abcd\",\n" +
                "  \"layout_width\": \"100dp\",\n" +
                "  \"layout_height\": \"wrap_content\",\n" +
                "  \"layout_marginStart\": \"10dp\",\n" +
                "  \"layout_marginTop\": \"10dp\",\n" +
                "  \"background\": \"@drawable/bg_all_cet\",\n" +
                "  \"orientation\": \"vertical\",\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"type\": \"ImageView\",\n" +
                "      \"src\": \"https://storage.googleapis.com/gweb-uniblog-publish-prod/images/Google_Play_Prism.max-1100x1100.png\",\n" +
                "      \"id\": \"iv_image\",\n" +
                "      \"layout_width\": \"match_parent\",\n" +
                "      \"layout_height\": \"100dp\",\n" +
                "      \"layout_marginTop\": \"5dp\",\n" +
                "      \"scaleType\": \"centerInside\",\n" +
                "      \"src\": \"@drawable/ic_lost_items\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"TextView\",\n" +
                "      \"id\": \"tv_cetegory\",\n" +
                "      \"layout_width\": \"match_parent\",\n" +
                "      \"layout_height\": \"wrap_content\",\n" +
                "      \"layout_marginTop\": \"10dp\",\n" +
                "      \"ellipsize\": \"end\",\n" +
                "      \"gravity\": \"center\",\n" +
                "      \"maxLines\": \"1\",\n" +
                "      \"padding\": \"2dp\",\n" +
                "      \"text\": \"@{user.profile.name}\",\n" +
                "      \"textColor\": \"@color/black\",\n" +
                "      \"textSize\": \"11sp\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        */
        /*final String LAYOUT = "{\n" +
                "  \"type\": \"RelativeLayout\",\n" +
                "  \"android\": \"http://schemas.android.com/apk/res/android\",\n" +
                "  \"layout_width\": \"match_parent\",\n" +
                "  \"layout_height\": \"match_parent\",\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"type\": \"ScrollView\",\n" +
                "      \"layout_width\": \"match_parent\",\n" +
                "      \"layout_height\": \"match_parent\",\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"type\": \"LinearLayout\",\n" +
                "          \"layout_width\": \"match_parent\",\n" +
                "          \"layout_height\": \"match_parent\",\n" +
                "          \"orientation\": \"vertical\",\n" +
                "          \"children\": [\n" +
                "{\n" +
                "        \"type\": \"RecyclerView\",\n" +
                "        \"layout_width\": \"match_parent\",\n" +
                "        \"layout_height\": \"300dp\",\n" +
                "        \"background\": \"#ffffff\",\n" +
                "        \"data\": {\n" +
                "          \"items\": \"@{data.achievements}\"\n" +
                "        },\n" +
                "        \"layout_manager\": {\n" +
                "          \"type\": \"LinearLayoutManager\"\n" +
                "        },\n" +
                "        \"adapter\": {\n" +
                "          \"@\": {\n" +
                "            \"type\": \"SimpleListAdapter\",\n" +
                "            \"item-count\": \"@{items.$length}\",\n" +
                "            \"item-layout\": {\n" +
                "              \"type\": \"TextView\",\n" +
                "              \"data\": {\n" +
                "                \"item\": \"@{items[$index]}\"\n" +
                "              },\n" +
                "              \"layout_width\": \"match_parent\",\n" +
                "              \"padding\": \"12dp\",\n" +
                "              \"layout_marginBottom\": \"4dp\",\n" +
                "              \"gravity\": \"center\",\n" +
                "              \"text\": \"@{item}\",\n" +
                "              \"textSize\": \"14sp\",\n" +
                "              \"textColor\": \"#323232\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "            {\n" +
                "              \"type\": \"HorizontalScrollView\",\n" +
                "              \"layout_width\": \"match_parent\",\n" +
                "              \"layout_height\": \"wrap_content\",\n" +
                "              \"children\": [\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"orientation\": \"horizontal\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"onClick\": \"showToast\",\n" +
                "                          \"clickable\": \"true\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    }\n" +
                "                  ]\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"type\": \"LinearLayout\",\n" +
                "              \"layout_width\": \"match_parent\",\n" +
                "              \"layout_height\": \"match_parent\",\n" +
                "              \"orientation\": \"vertical\",\n" +
                "              \"children\": [\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"id\": \"imP\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";*/


        final String LAYOUT  = "{\n" +
                "  \"type\": \"RelativeLayout\",\n" +
                "  \"android\": \"http://schemas.android.com/apk/res/android\",\n" +
                "  \"layout_width\": \"match_parent\",\n" +
                "  \"layout_height\": \"match_parent\",\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"type\": \"ScrollView\",\n" +
                "      \"layout_width\": \"match_parent\",\n" +
                "      \"layout_height\": \"match_parent\",\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"type\": \"LinearLayout\",\n" +
                "          \"layout_width\": \"match_parent\",\n" +
                "          \"layout_height\": \"match_parent\",\n" +
                "          \"orientation\": \"vertical\",\n" +
                "          \"children\": [\n" +
                "            {\n" +
                "              \"type\": \"ImageView\",\n" +
                "              \"layout_width\": \"wrap_content\",\n" +
                "              \"layout_height\": \"wrap_content\",\n" +
                "              \"layout_marginTop\": \"5dp\",\n" +
                "              \"scaleType\": \"centerInside\",\n" +
                "               \"onClick\": {\n" +
                "                    \"type\": \"showToast\"\n" +
                "                  },\n" +
                "              \"src\": \"@drawable/ic_lost_items\"\n" +
                "            },\n" +
                " {\n" +
                "        \"type\": \"RecyclerView\",\n" +
                "        \"layout_width\": \"match_parent\",\n" +
                "        \"layout_height\": \"wrap_content\",\n" +
                "        \"layout_margin\": \"10dp\",\n" +
                "        \"background\": \"#ffffff\",\n" +
                "        \"data\": {\n" +
                "          \"items\": \"@{data.achievements}\",\n" +
                "          \"images\":\"@{data.image}\"\n" +
                "        },\n" +
                "        \"layout_manager\": {\n" +
                "          \"type\": \"LinearLayoutManagerHorizontal\"\n" +
                "        },\n" +
                "        \"adapter\": {\n" +
                "          \"@\": {\n" +
                "            \"type\": \"SimpleListAdapter\",\n" +
                "            \"item-count\": \"@{items.$length}\",\n" +
                "            \"item-layout\": {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"120dp\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"10dp\",\n" +
                " \"data\": {\n" +
                "                            \"item\": \"@{items[$index]}\",\n" +
                "                            \"image\": \"@{images[$index]}\"\n" +
                "                       },\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"10dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"100dp\",\n" +
                "                          \"layout_height\": \"100dp\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"onClick\": {\n" +
                "                              \"type\": \"showToast\"\n" +
                "                           },\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@{image}\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"@{item}\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    }]}\n" +
                "          }\n" +
                "        }\n" +
                "      },{\n" +
                "        \"type\": \"RecyclerView\",\n" +
                "        \"layout_width\": \"match_parent\",\n" +
                "        \"layout_height\": \"500dp\",\n" +
                "        \"layout_margin\": \"10dp\",\n" +
                "        \"background\": \"#ffffff\",\n" +
                "        \"data\": {\n" +
                "          \"items\": \"@{data.achievements}\",\n" +
                "          \"images\":\"@{data.image}\"\n" +
                "        },\n" +
                "        \"layout_manager\": {\n" +
                "          \"type\": \"LinearLayoutManager\"\n" +
                "        },\n" +
                "        \"adapter\": {\n" +
                "          \"@\": {\n" +
                "            \"type\": \"SimpleListAdapter\",\n" +
                "            \"item-count\": \"@{items.$length}\",\n" +
                "            \"item-layout\": {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"10dp\",\n" +
                " \"data\": {\n" +
                "                            \"item\": \"@{items[$index]}\",\n" +
                "                            \"image\": \"@{images[$index]}\"\n" +
                "                       },\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"10dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"100dp\",\n" +
                "                          \"layout_height\": \"100dp\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"onClick\": {\n" +
                "                              \"type\": \"showToast\"\n" +
                "                           },\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@{image}\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"@{item}\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    }]}\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "            {\n" +
                "              \"type\": \"HorizontalScrollView\",\n" +
                "              \"layout_width\": \"match_parent\",\n" +
                "              \"layout_height\": \"wrap_content\",\n" +
                "              \"children\": [\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"orientation\": \"horizontal\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"LinearLayout\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_margin\": \"5dp\",\n" +
                "                      \"background\": \"@drawable/bg_all_cet\",\n" +
                "                      \"orientation\": \"vertical\",\n" +
                "                      \"children\": [\n" +
                "                        {\n" +
                "                          \"type\": \"ImageView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"match_parent\",\n" +
                "                          \"layout_marginTop\": \"5dp\",\n" +
                "                          \"scaleType\": \"centerInside\",\n" +
                "                          \"src\": \"@drawable/ic_lost_items\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                          \"type\": \"TextView\",\n" +
                "                          \"layout_width\": \"match_parent\",\n" +
                "                          \"layout_height\": \"wrap_content\",\n" +
                "                          \"layout_marginTop\": \"10dp\",\n" +
                "                          \"ellipsize\": \"end\",\n" +
                "                          \"gravity\": \"center\",\n" +
                "                          \"maxLines\": \"1\",\n" +
                "                          \"padding\": \"2dp\",\n" +
                "                          \"text\": \"bhagwan\",\n" +
                "                          \"textColor\": \"@color/black\",\n" +
                "                          \"textSize\": \"11sp\"\n" +
                "                        }\n" +
                "                      ]\n" +
                "                    }\n" +
                "                  ]\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"type\": \"LinearLayout\",\n" +
                "              \"layout_width\": \"match_parent\",\n" +
                "              \"layout_height\": \"match_parent\",\n" +
                "              \"orientation\": \"vertical\",\n" +
                "              \"children\": [\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                },\n" +
                "                {\n" +
                "                  \"type\": \"LinearLayout\",\n" +
                "                  \"layout_width\": \"match_parent\",\n" +
                "                  \"layout_height\": \"wrap_content\",\n" +
                "                  \"layout_margin\": \"5dp\",\n" +
                "                  \"background\": \"@drawable/bg_all_cet\",\n" +
                "                  \"orientation\": \"vertical\",\n" +
                "                  \"children\": [\n" +
                "                    {\n" +
                "                      \"type\": \"ImageView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"match_parent\",\n" +
                "                      \"layout_marginTop\": \"5dp\",\n" +
                "                      \"scaleType\": \"centerInside\",\n" +
                "                      \"src\": \"@drawable/ic_lost_items\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                      \"type\": \"TextView\",\n" +
                "                      \"layout_width\": \"match_parent\",\n" +
                "                      \"layout_height\": \"wrap_content\",\n" +
                "                      \"layout_marginTop\": \"10dp\",\n" +
                "                      \"ellipsize\": \"end\",\n" +
                "                      \"gravity\": \"center\",\n" +
                "                      \"maxLines\": \"1\",\n" +
                "                      \"padding\": \"2dp\",\n" +
                "                      \"text\": \"bhagwan\",\n" +
                "                      \"textColor\": \"@color/black\",\n" +
                "                      \"textSize\": \"11sp\"\n" +
                "                    }\n" +
                "                  ]\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        final String DATA = "{\n" +
                "  \"0\": {\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"location\": {\n" +
                "      \"country\": \"India\",\n" +
                "      \"city\": \"Bengaluru\",\n" +
                "      \"pincode\": \"560103\"\n" +
                "    },\n" +
                "    \"credits\": 39550.00,\n" +
                "    \"level\": 4,\n" +
                "    \"achievements\": {\n" +
                "      \"current\": 16,\n" +
                "      \"max\": 48\n" +
                "    },\n" +
                "    \"experience\": {\n" +
                "      \"current\": 4561,\n" +
                "      \"max\": 9999\n" +
                "    },\n" +
                "    \"tags\": [\n" +
                "      \"alpha\",\n" +
                "      \"beta\",\n" +
                "      \"gamma\",\n" +
                "      \"delta\",\n" +
                "      \"niner\"\n" +
                "    ],\n" +
                "    \"type\": null\n" +
                "  },\n" +
                "  \"theme\": {\n" +
                "    \"color\": \"#88ee99\"\n" +
                "  },\n" +
                "  \"sec\": {\n" +
                "    \"ll\": \"2017-01-01 12:01:37\",\n" +
                "    \"loc\": \"BLR, IND\"\n" +
                "  },\n" +
                "  \"data\": {\n" +
                "    \"achievements\": [\n" +
                "      \"Malcontent\",\n" +
                "      \"Trusty Hardware\",\n" +
                "      \"Anchor's Aweigh!\",\n" +
                "      \"Heavy Weapons\",\n" +
                "      \"Revenge!\",\n" +
                "      \"Submissive\",\n" +
                "      \"Zero-Point Energy\",\n" +
                "      \"Hallowed Ground\",\n" +
                "      \"Where Cubbage Fears to Tread\",\n" +
                "      \"Barnacle Bowling\",\n" +
                "      \"Bug Hunt\",\n" +
                "      \"Defiant\",\n" +
                "      \"Warden Freeman\",\n" +
                "      \"Follow Freeman\",\n" +
                "      \"Radiation Levels Detected\",\n" +
                "      \"Plaza Defender\",\n" +
                "      \"Blast from the Past\",\n" +
                "      \"One Man Army\",\n" +
                "      \"Fight the Power\",\n" +
                "      \"Giant Killer\",\n" +
                "      \"Singularity Collapse\",\n" +
                "      \"OSHA Violation\",\n" +
                "      \"Targetted Advertising\",\n" +
                "      \"Atomizer\",\n" +
                "      \"What cat?\",\n" +
                "      \"Counter-Sniper\",\n" +
                "      \"Two Points\",\n" +
                "      \"Vorticough\",\n" +
                "      \"Flushed\",\n" +
                "      \"Hack Attack!\",\n" +
                "      \"Zombie Chopper\",\n" +
                "      \"Keep Off the Sand!\",\n" +
                "      \"Lambda Locator\"\n" +
                "    ],\n" +
                "    \"image\": [\n" +
                "      \"https://storage.googleapis.com/gweb-uniblog-publish-prod/images/Google_Play_Prism.max-1100x1100.png\",\n" +
                "      \"Trusty Hardware\",\n" +
                "      \"Anchor's Aweigh!\",\n" +
                "      \"Heavy Weapons\",\n" +
                "      \"https://storage.googleapis.com/gweb-uniblog-publish-prod/images/Google_Play_Prism.max-1100x1100.png\",\n" +
                "      \"Submissive\",\n" +
                "      \"Zero-Point Energy\",\n" +
                "      \"Hallowed Ground\",\n" +
                "      \"Where Cubbage Fears to Tread\",\n" +
                "      \"Barnacle Bowling\",\n" +
                "      \"Bug Hunt\",\n" +
                "      \"Defiant\",\n" +
                "      \"Warden Freeman\",\n" +
                "      \"Follow Freeman\",\n" +
                "      \"Radiation Levels Detected\",\n" +
                "      \"Plaza Defender\",\n" +
                "      \"Blast from the Past\",\n" +
                "      \"One Man Army\",\n" +
                "      \"Fight the Power\",\n" +
                "      \"Giant Killer\",\n" +
                "      \"Singularity Collapse\",\n" +
                "      \"OSHA Violation\",\n" +
                "      \"Targetted Advertising\",\n" +
                "      \"Atomizer\",\n" +
                "      \"What cat?\",\n" +
                "      \"Counter-Sniper\",\n" +
                "      \"Two Points\",\n" +
                "      \"Vorticough\",\n" +
                "      \"Flushed\",\n" +
                "      \"Hack Attack!\",\n" +
                "      \"Zombie Chopper\",\n" +
                "      \"Keep Off the Sand!\",\n" +
                "      \"Lambda Locator\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        Layout layout;
        ObjectValue data;
        try {
            layout = adapter.LAYOUT_TYPE_ADAPTER.read(new JsonReader(new StringReader(LAYOUT)));
            data = adapter.OBJECT_TYPE_ADAPTER.read(new JsonReader(new StringReader(DATA)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProteusContext context = proteus.createContextBuilder(this).setCallback(callback).setImageLoader(loader).build();
        ProteusLayoutInflater inflater = context.getInflater();

// Inflate the layout
        ProteusView view = inflater.inflate(layout, data, binding.contentMain, 0);

// Add the inflated layout into the container
        binding.contentMain.addView(view.getAsView());



       /* LinearLayout ll_main_category = (LinearLayout) view.getViewManager().findViewById("abcd");
        ll_main_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setToast(activity,"ShowToast");
            }
        });*/
        //layoutInflater = context.getInflater();
        //alert();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onError(@NonNull Exception e) {

    }

    private void alert() {
  /*      String objectValue = "{\\n\" +\n" +
                "\"  \\\"type\\\": \\\"TextView\\\",\\n\" +\n" +
                "\"  \\\"textSize\\\": \\\"28sp\\\",\\n\" +\n" +
                "\"  \\\"text\\\": \\\"Hello World!\\\"\\n\" +\n" +
                "\"}";
        String data = "{}";
        try {
            layout = new ProteusTypeAdapterFactory(this).LAYOUT_TYPE_ADAPTER.read(new JsonReader(new StringReader(objectValue)));
            data = new ProteusTypeAdapterFactory(this).OBJECT_TYPE_ADAPTER.read(new JsonReader(new StringReader(data)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ProteusView view = layoutInflater.inflate("AlertDialogLayout", data);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view.getAsView())
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }*/
    }
}