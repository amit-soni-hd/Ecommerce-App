package com.ecommerce.ecommApp.products;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryBuilder {

    private JSONObject jsonQuery = new JSONObject("{\"query\":{\"bool\":{}}}");
    private JSONObject jsonObject;

    public QueryBuilder(String json) throws JSONException {
        jsonObject = new JSONObject(json);
    }

    public String build() throws JSONException {

        if (jsonObject.has("price")) {
            JSONObject range = jsonObject.getJSONObject("price");
            int gte = range.getInt("gte");
            int lte = range.getInt("lte");
            String filter = String.format("{\"range\":{\"price\":{\"gte\":%s,\"lte\":%s}}}", gte, lte);
            JSONObject filterObject = new JSONObject(filter);
            jsonQuery.getJSONObject("query").getJSONObject("bool").put("filter", filterObject);
        }

        if (jsonObject.has("category")) {
            String categoryValue = jsonObject.getString("category");
            String mustValue = String.format("[{\"match\":{\"category\":{\"query\":\"%s\",\"fuzziness\":\"AUTO\"}}}]", categoryValue);
            JSONArray mustArray = new JSONArray(mustValue);
            jsonQuery.getJSONObject("query").getJSONObject("bool").put("must", mustArray);
        }

        if (jsonObject.has("brand")) {
            String categoryValue = jsonObject.getString("brand");
            String mustValue = String.format("[{\"match\":{\"brand\":{\"query\":\"%s\",\"fuzziness\":\"AUTO\"}}}]", categoryValue);
            JSONArray mustArray = new JSONArray(mustValue);
            jsonQuery.getJSONObject("query").getJSONObject("bool").put("must", mustArray);
        }

        if (jsonObject.has("search_text")) {
            String text = jsonObject.getString("search_text");
            if (!jsonObject.has("category")) {
                //no category object
                String shouldValue = String.format("[{\"match\":{\"product_name\":{\"query\":\"%s\",\"fuzziness\":\"AUTO\"}}},{\"match\":{\"product_description\":{\"query\":\"%s\",\"fuzziness\":\"AUTO\"}}},{\"match\":{\"category\":{\"query\":\"%s\",\"fuzziness\":\"AUTO\"}}}]"
                        , text, text, text);
                JSONArray shouldArray = new JSONArray(shouldValue);
                jsonQuery.getJSONObject("query").getJSONObject("bool").put("should", shouldArray);
            } else {
                //it has category
                String shouldValue = String.format("[{\"match\":{\"product_name\":{\"query\":\"%s\",\"fuzziness\":\"AUTO\"}}},{\"match\":{\"product_description\":{\"query\":\"%s\",\"fuzziness\":\"AUTO\"}}}]"
                        , text, text);
                JSONArray shouldArray = new JSONArray(shouldValue);
                jsonQuery.getJSONObject("query").getJSONObject("bool").put("should", shouldArray);
            }
            jsonQuery.getJSONObject("query").getJSONObject("bool").put("minimum_should_match", 1);
        }
        return jsonQuery.toString();
    }
}