package com.bitorb.api.pub.tests;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Book {
    public String symbol;
    public String ccy;
    public double mid;

    public Collection<Level> asks;
    public Collection<Level> bids;

    @Override
    public String toString() {
        return "Book{" +
                "symbol='" + symbol + '\'' +
                ", ccy='" + ccy + '\'' +
                ", mid=" + mid +
                ", asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}

