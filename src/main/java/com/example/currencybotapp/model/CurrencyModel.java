package com.example.currencybotapp.model;

import lombok.Data;

@Data
public class CurrencyModel {
    private int amount;
    private String from;
    private String to;
    private String result;
    private Boolean success;

    public boolean isSuccess() {
        return success != null && success;
    }
}
