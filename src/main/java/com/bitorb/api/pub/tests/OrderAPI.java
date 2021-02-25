package com.bitorb.api.pub.tests;

interface OrderAPI {
    Order createOrder(CreateOrder createorder);

    Book getOrderBook();

    Book getOrderBook(String symbol);

    Object getOrderBook(String symbol, Integer level);

    String getActiveOrder(String clOrdId);
}