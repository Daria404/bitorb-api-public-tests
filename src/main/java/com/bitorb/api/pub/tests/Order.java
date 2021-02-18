package com.bitorb.api.pub.tests;
import java.lang.reflect.Field;

public class Order {
    public String clOrdID;
    public String clientReqID;
    public String symbol;
    public double created;
    public String username;
    public String side;
    public double spread;
    public double leverage;
    public double margin;
    public double orderQty;
    public double fillQty;
    public double exitQty;
    public double entryPrice;
    public double exitPrice;
    public double liquidationPrice;
    public double stopLoss;
    public double takeProfit;
    public double trailingStopPrice;
    public double realisedPnL;
    public double eventTime;
    public boolean isClosed;

    @Override
    public String toString() {
        return "Order{" +
                "clOrdID='" + clOrdID + '\'' +
                ", clientReqID='" + clientReqID + '\'' +
                ", symbol='" + symbol + '\'' +
                ", created=" + created +
                ", username='" + username + '\'' +
                ", side='" + side + '\'' +
                ", spread=" + spread +
                ", leverage=" + leverage +
                ", margin=" + margin +
                ", orderQty=" + orderQty +
                ", fillQty=" + fillQty +
                ", exitQty=" + exitQty +
                ", entryPrice=" + entryPrice +
                ", exitPrice=" + exitPrice +
                ", liquidationPrice=" + liquidationPrice +
                ", stopLoss=" + stopLoss +
                ", takeProfit=" + takeProfit +
                ", trailingStopPrice=" + trailingStopPrice +
                ", realisedPnL=" + realisedPnL +
                ", eventTime=" + eventTime +
                ", isClosed=" + isClosed +
                '}';
    }

     /*   public String toString(Object obj) throws IllegalAccessException {
        StringBuilder objStringRepresentation = new StringBuilder();

        for (Field field : obj.getClass().getDeclaredFields()) {
            String key = field.getName();
            String value = field.get(obj).toString();
            objStringRepresentation.append(key).append(" : ").append(value).append("\n");
        }
        return (objStringRepresentation.toString());
    }*/
}
