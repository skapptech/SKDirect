package com.skdirect.utils;

public class JsonData {

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

    public static String DATA = "{\n" +
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
}


