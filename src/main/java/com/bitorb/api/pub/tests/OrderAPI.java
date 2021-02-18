package com.bitorb.api.pub.tests;

import java.io.UnsupportedEncodingException;

interface OrderBookInterface {
    public Book getOrderBook();

    public Book getOrderBook(String symbol);

    public Book getOrderBook(String symbol, Integer level);

    public String getActiveOrder(String clOrdId) throws UnsupportedEncodingException;
}

