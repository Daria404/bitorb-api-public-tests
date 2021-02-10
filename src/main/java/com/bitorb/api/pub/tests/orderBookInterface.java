package com.bitorb.api.pub.tests;

import java.lang.System;
import okhttp3.*;

public interface orderBookInterface {
    public String getOrderBook();

    public String getOrderBook(String symbol);

    public String getOrderBook(String symbol, Integer level);
}

