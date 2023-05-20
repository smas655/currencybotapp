package com.example.currencybotapp.service;

import com.example.currencybotapp.model.CurrencyModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CurrencyService {

    public String getCurrencyRate(String fromCurrency, String toCurrency, double amount) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.apilayer.com/currency_data/convert?to=" + toCurrency + "&from=" + fromCurrency + "&amount=" + amount;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", "3sY6tQAsXDbP7oWONnxEgjRl9bxocADk")
                .method("GET", null)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            CurrencyModel currencyModel = parseCurrencyModel(responseBody);

            if (currencyModel.isSuccess()) {
                return "Converted amount: " + currencyModel.getAmount() + " " + currencyModel.getFrom() + " = " +
                        currencyModel.getResult() + " " + currencyModel.getTo();
            } else {
                return "Currency conversion failed. Please try again.";
            }
        }
    }

    private CurrencyModel parseCurrencyModel(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);

        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setAmount(jsonObject.getInt("amount"));
        currencyModel.setFrom(jsonObject.getString("from"));
        currencyModel.setTo(jsonObject.getString("to"));
        currencyModel.setResult(jsonObject.getString("result"));
        currencyModel.setSuccess(jsonObject.getBoolean("success"));

        return currencyModel;
    }
}
