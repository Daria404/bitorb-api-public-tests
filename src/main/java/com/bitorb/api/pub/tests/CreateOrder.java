package com.bitorb.api.pub.tests;

import com.google.gson.Gson;

public class CreateOrder {
    public long clientReqID;
    public String symbol;
    public String side;
    public double price;
    public double leverage;
    public double qty;


    public CreateOrder(long clientReqID, String symbol, String side, double price, double leverage, double qty) {
        this.clientReqID = clientReqID;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.leverage = leverage;
        this.qty = qty;
    }

    public String getBody() {
        Gson g = new Gson();
        return g.toJson(this, CreateOrder.class);
    }
}
