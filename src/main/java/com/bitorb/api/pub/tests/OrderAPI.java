package com.bitorb.api.pub.tests;

import java.io.UnsupportedEncodingException;

interface OrderAPI {
    public Order createOrder(CreateOrder createorder) throws InterruptedException;

    public Book getOrderBook();

    public Book getOrderBook(String symbol) throws UnsupportedEncodingException;

    public Book getOrderBook(String symbol, Integer level) throws UnsupportedEncodingException;

    public String getActiveOrder(String clOrdId) throws UnsupportedEncodingException;
}

