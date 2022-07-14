package com.alpha.spentwise.customDataObjects;

import java.util.Map;

public class CurrencyJsonResponse {

    private Map<String, CurrencyDetails> results;

    public Map<String, CurrencyDetails> getResults() {
        return results;
    }

    public void setResults(Map<String, CurrencyDetails> results) {
        this.results = results;
    }
}
