package com.ecommerce.ecommApp.commons.Util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class ElasticSearchQueryBuilder {
    public static String builtSearchQuery(String jsonInput){
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonInput);
        SearchSourceBuilder b= new SearchSourceBuilder();
        BoolQueryBuilder qb = QueryBuilders.boolQuery().minimumShouldMatch(1);
        System.out.println(jsonObject);
        if(jsonObject.has("category")&& jsonObject.has("search_text")){
            qb.should(QueryBuilders.matchQuery("product_name",jsonObject.get("search_text").getAsString()).fuzziness(Fuzziness.AUTO));
            qb.should(QueryBuilders.matchQuery("product_description",jsonObject.get("search_text").getAsString()).fuzziness(Fuzziness.AUTO));
            qb.must(QueryBuilders.matchQuery("category",jsonObject.get("category").getAsString()).fuzziness(Fuzziness.AUTO));
        }
        else if(jsonObject.has("category")){
            qb.must(QueryBuilders.matchQuery("category",jsonObject.get("category").getAsString()).fuzziness(Fuzziness.AUTO));
        }
        else if(jsonObject.has("search_text")){
            qb.should(QueryBuilders.matchQuery("product_name",jsonObject.get("search_text").getAsString()).fuzziness(Fuzziness.AUTO));
            qb.should(QueryBuilders.matchQuery("product_description",jsonObject.get("search_text").getAsString()).fuzziness(Fuzziness.AUTO));
            qb.should(QueryBuilders.matchQuery("category",jsonObject.get("search_text").getAsString()).fuzziness(Fuzziness.AUTO));
        }
        if(jsonObject.has("price")){
            JsonObject price=jsonObject.getAsJsonObject("price");
            qb.filter(QueryBuilders.rangeQuery("price").lte(price.get("lte").getAsFloat()).gte(price.get("gte").getAsFloat()));
        }
        if(jsonObject.has("size")) {
            JsonArray sizes = jsonObject.getAsJsonArray("size");
            String sizelist = ".*(";
            for (int i = 0; i < sizes.size(); i++) {
                if(i!=0)
                    sizelist=sizelist.concat("|");
                sizelist=sizelist.concat(sizes.get(i).getAsString());
            }
            sizelist=sizelist.concat(")");
            qb.must(QueryBuilders.regexpQuery("size", sizelist));
        }
        b.query(qb);
        return b.toString();
    }

    public static void main(String[] args) {
        String json = "{\"search_text\":\"Jewellery\",\"price\":{\"lte\":2000,\"gte\":1000}}";
        System.out.println(builtSearchQuery(json));

    }
}
