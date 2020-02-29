package com.ecommerce.ecommApp.products.elasticsearch;

public class ElasticsearchObject {
    // Object creation
    private static ElasticSearchUtil elasticsearchUtil = null;

    // Initializing an object with memory is not initialized
    public static ElasticSearchUtil getElasticsearchObject() {
        if (elasticsearchUtil == null)
            return elasticsearchUtil = new ElasticSearchUtil();
        return elasticsearchUtil;
    }
}
