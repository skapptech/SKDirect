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
import com.skdirect.databinding.ActivityProteusBinding;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class ProteusActivity extends AppCompatActivity {
    ProteusActivity activity;
    ActivityProteusBinding binding;
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
                "  \"app\": \"http://schemas.android.com/apk/res-auto\",\n" +
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
                "              \"layout_marginLeft\": \"10dp\",\n" +
                "              \"layout_marginTop\": \"10dp\",\n" +
                "              \"layout_marginRight\": \"10dp\",\n" +
                "              \"background\": \"@drawable/ic_top\",\n" +
                "              \"padding\": \"20dp\",\n" +
                "              \"src\": \"@drawable/ic_shoping\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"type\": \"RecyclerView\",\n" +
                "              \"layout_width\": \"match_parent\",\n" +
                "              \"layout_height\": \"match_parent\",\n" +
                "              \"layout_marginLeft\": \"10dp\",\n" +
                "              \"layout_marginRight\": \"10dp\",\n" +
                "              \"layout_marginTop\": \"-25dp\",\n" +
                "              \"background\": \"@drawable/ic_middle\",\n" +
                "              \"data\": {\n" +
                "                \"storeCategoryList\": \"@{data.StoreCategoryList}\"\n" +
                "              },\n" +
                "              \"layout_manager\": {\n" +
                "                \"type\": \"GridLayoutManager\"\n" +
                "              },\n" +
                "              \"adapter\": {\n" +
                "                \"@\": {\n" +
                "                  \"type\": \"SimpleListAdapter\",\n" +
                "                  \"item-count\": \"@{storeCategoryList.$length}\",\n" +
                "                  \"item-layout\": {\n" +
                "                    \"type\": \"LinearLayout\",\n" +
                "                    \"android\": \"http://schemas.android.com/apk/res/android\",\n" +
                "                    \"app\": \"http://schemas.android.com/apk/res-auto\",\n" +
                "                    \"layout_width\": \"match_parent\",\n" +
                "                    \"layout_height\": \"wrap_content\",\n" +
                "                    \"layout_margin\": \"20dp\",\n" +
                "                    \"data\": {\n" +
                "                      \"catNameJson\": \"@{storeCategoryList[$index]}\",\n" +
                "                      \"catName\": \"@{catNameJson.CategoryName}\"\n" +
                "                    },\n" +
                "                    \"orientation\": \"vertical\",\n" +
                "                    \"children\": [\n" +
                "                      {\n" +
                "                        \"type\": \"LinearLayout\",\n" +
                "                        \"layout_width\": \"match_parent\",\n" +
                "                        \"layout_height\": \"wrap_content\",\n" +
                "                        \"layout_marginLeft\": \"20dp\",\n" +
                "                        \"layout_marginRight\": \"20dp\",\n" +
                "                        \"layout_marginTop\": \"10dp\",\n" +
                "                        \"background\": \"@drawable/ic_banner\",\n" +
                "                        \"children\": [\n" +
                "                          {\n" +
                "                            \"type\": \"TextView\",\n" +
                "                            \"id\": \"tv_mall_category_name\",\n" +
                "                            \"layout_width\": \"match_parent\",\n" +
                "                            \"layout_height\": \"wrap_content\",\n" +
                "                            \"layout_gravity\": \"center\",\n" +
                "                            \"gravity\": \"center\",\n" +
                "                            \"text\": \"@{catName}\",\n" +
                "                            \"textColor\": \"@color/black\",\n" +
                "                            \"textSize\": \"18dp\"\n" +
                "                          }\n" +
                "                        ]\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"type\": \"RecyclerView\",\n" +
                "                        \"layout_width\": \"match_parent\",\n" +
                "                        \"layout_height\": \"wrap_content\",\n" +
                "                        \"layout_marginLeft\": \"20dp\",\n" +
                "                        \"layout_marginRight\": \"20dp\",\n" +
                "                        \"data\": {\n" +
                "                          \"storeListArray\": \"@{catNameJson.StoreList}\"\n" +
                "                        },\n" +
                "                        \"layout_manager\": {\n" +
                "                          \"type\": \"GridLayoutManagerHorizontal\"\n" +
                "                        },\n" +
                "                        \"adapter\": {\n" +
                "                          \"@\": {\n" +
                "                            \"type\": \"SimpleListAdapter\",\n" +
                "                            \"item-count\": \"@{storeListArray.$length}\",\n" +
                "                            \"item-layout\": {\n" +
                "                              \"type\": \"LinearLayout\",\n" +
                "                              \"layout_width\": \"wrap_content\",\n" +
                "                              \"layout_height\": \"wrap_content\",\n" +
                "                              \"data\": {\n" +
                "                                \"storeListJson\": \"@{storeListArray[$index]}\",\n" +
                "                                \"sellerName\": \"@{storeListJson.SellerName}\",\n" +
                "                                \"sellerImage\": \"@{storeListJson.ImagePath}\"\n" +
                "                              },\n" +
                "                              \"orientation\": \"vertical\",\n" +
                "                              \"children\": [\n" +
                "                                {\n" +
                "                                  \"type\": \"LinearLayout\",\n" +
                "                                  \"id\": \"LL_apparels\",\n" +
                "                                  \"layout_width\": \"wrap_content\",\n" +
                "                                  \"layout_height\": \"wrap_content\",\n" +
                "                                  \"layout_marginLeft\": \"10dp\",\n" +
                "                                  \"layout_marginTop\": \"10dp\",\n" +
                "                                  \"orientation\": \"vertical\",\n" +
                "                                  \"children\": [\n" +
                "                                    {\n" +
                "                                      \"type\": \"RelativeLayout\",\n" +
                "                                      \"layout_width\": \"match_parent\",\n" +
                "                                      \"layout_height\": \"wrap_content\",\n" +
                "                                      \"children\": [\n" +
                "                                        {\n" +
                "                                          \"type\": \"CardView\",\n" +
                "                                          \"layout_width\": \"150dp\",\n" +
                "                                          \"layout_height\": \"130dp\",\n" +
                "                                          \"cardCornerRadius\": \"10dp\",\n" +
                "                                          \"cardElevation\": \"0dp\",\n" +
                "                                          \"layout_marginTop\": \"60dp\",\n" +
                "                                          \"children\": [\n" +
                "                                            {\n" +
                "                                              \"type\": \"ImageView\",\n" +
                "                                              \"id\": \"iv_image\",\n" +
                "                                              \"layout_width\": \"150dp\",\n" +
                "                                              \"layout_height\": \"130dp\",\n" +
                "                                              \"adjustViewBounds\": \"true\",\n" +
                "                                              \"scaleType\": \"centerCrop\",\n" +
                "                                              \"src\": \"@{sellerImage}\"\n" +
                "                                            }\n" +
                "                                          ]\n" +
                "                                        },\n" +
                "                                        {\n" +
                "                                          \"type\": \"LinearLayout\",\n" +
                "                                          \"layout_width\": \"wrap_content\",\n" +
                "                                          \"layout_height\": \"wrap_content\",\n" +
                "                                          \"background\": \"@drawable/ic_mall_shop\",\n" +
                "                                          \"orientation\": \"vertical\",\n" +
                "                                          \"children\": [\n" +
                "                                            {\n" +
                "                                              \"type\": \"TextView\",\n" +
                "                                              \"id\": \"tv_store_list\",\n" +
                "                                              \"layout_width\": \"match_parent\",\n" +
                "                                              \"layout_height\": \"match_parent\",\n" +
                "                                              \"layout_marginTop\": \"5dp\",\n" +
                "                                              \"gravity\": \"center\",\n" +
                "                                              \"text\": \"@{sellerName}\",\n" +
                "                                              \"textColor\": \"@color/white\",\n" +
                "                                              \"textSize\": \"12dp\"\n" +
                "                                            }\n" +
                "                                          ]\n" +
                "                                        }\n" +
                "                                      ]\n" +
                "                                    }\n" +
                "                                  ]\n" +
                "                                }\n" +
                "                              ]\n" +
                "                            }\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"type\": \"ImageView\",\n" +
                "              \"layout_width\": \"wrap_content\",\n" +
                "              \"layout_height\": \"wrap_content\",\n" +
                "              \"layout_marginLeft\": \"10dp\",\n" +
                "              \"layout_marginTop\": \"-2dp\",\n" +
                "              \"layout_marginRight\": \"10dp\",\n" +
                "              \"background\": \"@drawable/ic_bottom\"\n" +
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
                "    \"StoreCategoryList\": [{\n" +
                "\t\t\"CategoryId\": 15,\n" +
                "\t\t\"CategoryName\": \"Furniture\",\n" +
                "\t\t\"StoreList\": [{\n" +
                "\t\t\t\"SellerName\": \"Ravindra\",\n" +
                "\t\t\t\"SellerId\": 34830,\n" +
                "\t\t\t\"ImagePath\": \"https://res.cloudinary.com/dinsfrmzp/image/upload/v1610367725/~/images/SKDirect/fae24f10-ad13-4ede-b175-2a7d5a33e211_Product.jpg.jpg\",\n" +
                "\t\t\t\"ShopName\": \"Ravindra Shop\",\n" +
                "\t\t\t\"StoreView\": 62\n" +
                "\t\t}, {\n" +
                "\t\t\t\"SellerName\": \"SKDirectTest\",\n" +
                "\t\t\t\"SellerId\": 34808,\n" +
                "\t\t\t\"ImagePath\": \"https://res.cloudinary.com/dinsfrmzp/image/upload/v1615460266/~/images/SKDirect/50d57d92-2cc4-404a-a820-9d25546ac3c4.jpg\",\n" +
                "\t\t\t\"ShopName\": \"Retailer\",\n" +
                "\t\t\t\"StoreView\": 238\n" +
                "\t\t}]\n" +
                "\t}, {\n" +
                "\t\t\"CategoryId\": 1,\n" +
                "\t\t\"CategoryName\": \"Grocery\",\n" +
                "\t\t\"StoreList\": [{\n" +
                "\t\t\t\"SellerName\": \"Shivam buyer test\",\n" +
                "\t\t\t\"SellerId\": 34929,\n" +
                "\t\t\t\"ImagePath\": \"https://res.cloudinary.com/shopkirana/image/upload/v1616699359/Direct/dkwbhu0tu9sgf6j9ezhj.jpg\",\n" +
                "\t\t\t\"ShopName\": \"Shivam Shop\",\n" +
                "\t\t\t\"StoreView\": 263\n" +
                "\t\t}, {\n" +
                "\t\t\t\"SellerName\": \"Anurag Shop\",\n" +
                "\t\t\t\"SellerId\": 34886,\n" +
                "\t\t\t\"ImagePath\": \"https://res.cloudinary.com/shopkirana/image/upload/v1616743643/Direct/rsn1iat4bjzjltcipswf.jpg\",\n" +
                "\t\t\t\"ShopName\": \"Anurag Shop\",\n" +
                "\t\t\t\"StoreView\": 0\n" +
                "\t\t}]\n" +
                "\t}],\n" +
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