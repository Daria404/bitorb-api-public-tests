package com.bitorb.api.pub.tests;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.WireType;
import net.openhft.chronicle.wire.Wires;
import net.openhft.chronicle.wire.WriteMarshallable;

public class CreateOrder {

    public String getBody() {
        final String body = this.body;
        return body;
    }
    final String body = asJson(w -> w
            .write("clientReqID").int64(1)
            .write("symbol").text("BTC_USD_P0")
            .write("side").text("BUY")
            .write("qty").float64(1.0)
            .write("leverage").float64(1.0)
            .write("price").float64(42200)
            .write("ordType").character('2')
    ).toString();

    private CharSequence asJson(WriteMarshallable o) {
        Bytes bytes = Wires.acquireBytes();
        WireType.JSON.apply(bytes).getValueOut().marshallable(o);
        return bytes;
    }
}
