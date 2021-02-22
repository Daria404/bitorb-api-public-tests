package com.bitorb.api.pub.tests;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.WireType;
import net.openhft.chronicle.wire.Wires;
import net.openhft.chronicle.wire.WriteMarshallable;

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

    private CharSequence asJson(WriteMarshallable o) {
        Bytes bytes = Wires.acquireBytes();
        WireType.JSON.apply(bytes).getValueOut().marshallable(o);
        return bytes;
    }

    public String getBody() {
        final String body = asJson(w -> w
                .write("clientReqID").int64(this.clientReqID)
                .write("symbol").text(this.symbol)
                .write("side").text(this.side)
                .write("qty").float64(this.qty)
                .write("leverage").float64(this.leverage)
                .write("price").float64(this.price)
        ).toString();
        return body;
    }
}
