package com.skdirect.utils;

public class JsonData {

    public static String TYPE_LAYOUT = "{\n" +
            "  \"RelativeLayoutExample\": {\n" +
            "    \"type\": \"FrameLayout\",\n" +
            "    \"layout_width\": \"match_parent\",\n" +
            "    \"layout_height\": \"wrap_content\",\n" +
            "    \"padding\": \"16dp\",\n" +
            "    \"paddingBottom\": \"0dp\",\n" +
            "    \"children\": [\n" +
            "      {\n" +
            "        \"type\": \"RelativeLayout\",\n" +
            "        \"layout_width\": \"match_parent\",\n" +
            "        \"layout_height\": \"100dp\",\n" +
            "        \"background\": \"#ffffff\",\n" +
            "        \"children\": [\n" +
            "          {\n" +
            "            \"type\": \"TextView\",\n" +
            "            \"id\": \"center\",\n" +
            "            \"layout_width\": \"wrap_content\",\n" +
            "            \"layout_height\": \"wrap_content\",\n" +
            "            \"text\": \"Center\",\n" +
            "            \"textColor\": \"#323232\",\n" +
            "            \"textSize\": \"14sp\",\n" +
            "            \"layout_centerVertical\": true,\n" +
            "            \"layout_centerHorizontal\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"ImageView\",\n" +
            "            \"src\": \"@drawable/star_filled\",\n" +
            "            \"layout_marginBottom\": \"4dp\",\n" +
            "            \"layout_above\": \"center\",\n" +
            "            \"layout_centerHorizontal\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"ImageView\",\n" +
            "            \"src\": \"@drawable/star_filled\",\n" +
            "            \"layout_marginTop\": \"4dp\",\n" +
            "            \"layout_below\": \"center\",\n" +
            "            \"layout_centerHorizontal\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"ImageView\",\n" +
            "            \"src\": \"@drawable/star_filled\",\n" +
            "            \"layout_marginLeft\": \"4dp\",\n" +
            "            \"layout_toRightOf\": \"center\",\n" +
            "            \"layout_centerVertical\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"ImageView\",\n" +
            "            \"src\": \"@drawable/star_filled\",\n" +
            "            \"layout_marginRight\": \"4dp\",\n" +
            "            \"layout_toLeftOf\": \"center\",\n" +
            "            \"layout_centerVertical\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"ImageView\",\n" +
            "            \"src\": \"https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png\",\n" +
            "            \"layout_height\": \"70dp\",\n" +
            "            \"layout_width\": \"120dp\",\n" +
            "            \"padding\": \"4dp\",\n" +
            "            \"background\": \"#000000\",\n" +
            "            \"scaleType\": \"fitCenter\",\n" +
            "            \"layout_alignParentTop\": true,\n" +
            "            \"layout_alignParentRight\": true\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"SimpleDataBindingExample\": {\n" +
            "    \"type\": \"FrameLayout\",\n" +
            "    \"layout_width\": \"match_parent\",\n" +
            "    \"layout_height\": \"wrap_content\",\n" +
            "    \"padding\": \"16dp\",\n" +
            "    \"paddingBottom\": \"0dp\",\n" +
            "    \"children\": [\n" +
            "      {\n" +
            "        \"type\": \"LinearLayout\",\n" +
            "        \"layout_width\": \"match_parent\",\n" +
            "        \"layout_height\": \"wrap_content\",\n" +
            "        \"orientation\": \"vertical\",\n" +
            "        \"background\": \"#ffffff\",\n" +
            "        \"children\": [\n" +
            "          {\n" +
            "            \"type\": \"TextView\",\n" +
            "            \"layout_width\": \"wrap_content\",\n" +
            "            \"layout_height\": \"wrap_content\",\n" +
            "            \"layout_marginBottom\": \"0dp\",\n" +
            "            \"text\": \"Profile\",\n" +
            "            \"style\": \"mini.blue\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"RelativeLayout\",\n" +
            "            \"layout_width\": \"match_parent\",\n" +
            "            \"layout_height\": \"wrap_content\",\n" +
            "            \"paddingLeft\": \"16dp\",\n" +
            "            \"paddingRight\": \"16dp\",\n" +
            "            \"paddingTop\": \"8dp\",\n" +
            "            \"children\": [\n" +
            "              {\n" +
            "                \"type\": \"TextView\",\n" +
            "                \"id\": \"name\",\n" +
            "                \"layout_width\": \"wrap_content\",\n" +
            "                \"layout_height\": \"wrap_content\",\n" +
            "                \"layout_marginRight\": \"4dp\",\n" +
            "                \"textSize\": \"18dp\",\n" +
            "                \"text\": \"@{user.name}\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"type\": \"TextView\",\n" +
            "                \"id\": \"location\",\n" +
            "                \"layout_width\": \"wrap_content\",\n" +
            "                \"layout_height\": \"wrap_content\",\n" +
            "                \"layout_below\": \"name\",\n" +
            "                \"layout_marginRight\": \"4dp\",\n" +
            "                \"textSize\": \"18dp\",\n" +
            "                \"text\": \"@{fn:format('%s, %s', @{user.location.city}, @{user.location.country})}\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"type\": \"TextView\",\n" +
            "                \"text\": \"@{fn:format('(%s)',@{user.level})}\",\n" +
            "                \"textColor\": \"#c48866\",\n" +
            "                \"layout_width\": \"wrap_content\",\n" +
            "                \"layout_height\": \"wrap_content\",\n" +
            "                \"layout_toRightOf\": \"name\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"LinearLayout\",\n" +
            "            \"layout_width\": \"match_parent\",\n" +
            "            \"layout_height\": \"wrap_content\",\n" +
            "            \"orientation\": \"vertical\",\n" +
            "            \"paddingLeft\": \"16dp\",\n" +
            "            \"paddingRight\": \"16dp\",\n" +
            "            \"children\": [\n" +
            "              {\n" +
            "                \"type\": \"HorizontalProgressBar\",\n" +
            "                \"layout_width\": \"match_parent\",\n" +
            "                \"layout_height\": \"wrap_content\",\n" +
            "                \"layout_marginTop\": \"8dp\",\n" +
            "                \"max\": \"@{user.experience.max}\",\n" +
            "                \"progress\": \"@{user.experience.current}\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"type\": \"TextView\",\n" +
            "                \"layout_width\": \"wrap_content\",\n" +
            "                \"layout_height\": \"wrap_content\",\n" +
            "                \"layout_marginTop\": \"8dp\",\n" +
            "                \"layout_marginBottom\": \"32dp\",\n" +
            "                \"padding\": \"8dp\",\n" +
            "                \"paddingTop\": \"16dp\",\n" +
            "                \"paddingBottom\": \"16dp\",\n" +
            "                \"text\": \"@{fn:number(@{user.credits})}\",\n" +
            "                \"style\": \"money.center\",\n" +
            "                \"background\": {\n" +
            "                  \"type\": \"shape\",\n" +
            "                  \"shape\": \"oval\",\n" +
            "                  \"children\": [\n" +
            "                    {\n" +
            "                      \"type\": \"gradient\",\n" +
            "                      \"startColor\": \"#44e73827\",\n" +
            "                      \"endColor\": \"@color/green\",\n" +
            "                      \"angle\": 270\n" +
            "                    }\n" +
            "                  ]\n" +
            "                },\n" +
            "                \"animation\": {\n" +
            "                  \"interpolator\": {\n" +
            "                    \"type\": \"anticipateOvershootInterpolator\"\n" +
            "                  },\n" +
            "                  \"type\": \"rotate\",\n" +
            "                  \"fromDegrees\": -720,\n" +
            "                  \"toDegrees\": 0,\n" +
            "                  \"pivotX\": \"50%\",\n" +
            "                  \"pivotY\": \"50%\",\n" +
            "                  \"duration\": 1500\n" +
            "                }\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"TextView\",\n" +
            "            \"layout_width\": \"wrap_content\",\n" +
            "            \"layout_height\": \"wrap_content\",\n" +
            "            \"textSize\": \"14sp\",\n" +
            "            \"data\": {\n" +
            "              \"date\": \"@{fn:date(@{sec.ll},'E, d MMM')}\",\n" +
            "              \"loc\": \"@{sec.loc}\"\n" +
            "            },\n" +
            "            \"text\": \"@{fn:format('last login: %s from %s',@{date},@{loc})}\",\n" +
            "            \"style\": \"blue\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"type\": \"TextView\",\n" +
            "            \"layout_width\": \"wrap_content\",\n" +
            "            \"layout_height\": \"wrap_content\",\n" +
            "            \"textSize\": \"14sp\",\n" +
            "            \"text\": \"@{user.type}\",\n" +
            "            \"visibility\": \"@{fn:ternary(@{user.type}, 'visible', 'gone')}\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"DataDrivenExample\": {\n" +
            "    \"type\": \"FrameLayout\",\n" +
            "    \"layout_width\": \"match_parent\",\n" +
            "    \"layout_height\": \"wrap_content\",\n" +
            "    \"padding\": \"16dp\",\n" +
            "    \"paddingBottom\": \"0dp\",\n" +
            "    \"children\": [\n" +
            "      {\n" +
            "        \"type\": \"LinearLayout\",\n" +
            "        \"orientation\": \"horizontal\",\n" +
            "        \"layout_width\": \"match_parent\",\n" +
            "        \"background\": \"#ffffff\",\n" +
            "        \"padding\": \"16dp\",\n" +
            "        \"children\": {\n" +
            "          \"@\": {\n" +
            "            \"collection\": \"@{fn:slice(@{user.tags},0,4)}\",\n" +
            "            \"layout\": {\n" +
            "              \"type\": \"TextView\",\n" +
            "              \"data\": {\n" +
            "                \"tag\": \"@{user.tags[$index]}\"\n" +
            "              },\n" +
            "              \"layout_marginRight\": \"8dp\",\n" +
            "              \"layout_height\": \"32dp\",\n" +
            "              \"textSize\": \"12sp\",\n" +
            "              \"textColor\": \"@color/colorAccent\",\n" +
            "              \"text\": \"@{tag}\",\n" +
            "              \"padding\": \"8dp\",\n" +
            "              \"gravity\": \"center\",\n" +
            "              \"onClick\": \"@{tag}\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"RecyclerViewExample\": {\n" +
            "    \"type\": \"FrameLayout\",\n" +
            "    \"layout_width\": \"match_parent\",\n" +
            "    \"layout_height\": \"wrap_content\",\n" +
            "    \"padding\": \"16dp\",\n" +
            "    \"paddingBottom\": \"0dp\",\n" +
            "    \"children\": [\n" +
            "      {\n" +
            "        \"type\": \"RecyclerView\",\n" +
            "        \"layout_width\": \"match_parent\",\n" +
            "        \"layout_height\": \"148dp\",\n" +
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
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"AlertDialogLayout\": {\n" +
            "    \"type\": \"LinearLayout\",\n" +
            "    \"orientation\": \"vertical\",\n" +
            "    \"layout_width\": \"80dp\",\n" +
            "    \"layout_height\": \"wrap_content\",\n" +
            "    \"padding\": \"20dp\",\n" +
            "    \"children\": [\n" +
            "      {\n" +
            "        \"type\": \"TextView\",\n" +
            "        \"layout_width\": \"match_parent\",\n" +
            "        \"layout_height\": \"wrap_content\",\n" +
            "        \"layout_marginTop\": \"16dp\",\n" +
            "        \"gravity\": \"center\",\n" +
            "        \"textColor\": \"#ffaa77\",\n" +
            "        \"textSize\": \"32sp\",\n" +
            "        \"text\": \"This is an alert!\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"TextView\",\n" +
            "        \"layout_width\": \"match_parent\",\n" +
            "        \"layout_height\": \"wrap_content\",\n" +
            "        \"gravity\": \"center\",\n" +
            "        \"textSize\": \"22sp\",\n" +
            "        \"text\": \"press ok to continue.\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    public static String LAYOUT  = "{\n" +
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
            "              \"type\": \"include\",\n" +
            "              \"layout\": \"SimpleDataBindingExample\",\n" +
            "              \"padding\": \"16dp\",\n" +
            "              \"background\": \"#000000\",\n" +
            "              \"layout_margin\": \"10dp\",\n" +
            "              \"paddingBottom\": \"0dp\"\n" +
            "            },\n" +
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

    public static String DATA = "";


}


