package com.bitorb.api.pub.tests;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.WireType;
import net.openhft.chronicle.wire.Wires;
import net.openhft.chronicle.wire.WriteMarshallable;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class TestExample  {
    private static final String HOST = "trade.bitorb.com";
    private static final String REQUEST_PATH = "/api/v1/order";
    private static final String contracturl = "http://" + HOST  + ":9090" + REQUEST_PATH;
    private static final String APISECRET = "apisecret"; //TODO
    private static final String APIKEY = "ABCKEY"; //TODO
    private static final String USER = "testuser";
    private final OkHttpClient client = new OkHttpClient();

    @Test
    public void testCreateOrder() {
        final long clReqId = 1;

        final String body = asJson(w -> w
                .write("clientReqID").int64(clReqId)
                .write("symbol").text("BTC_USD_P0")
                .write("side").text("SELL")
                .write("qty").float64(100.0)
                .write("leverage").float64(100.0)
                .write("price").float64(8000.0)
        ).toString();

        String expires = "" + (System.currentTimeMillis() + 60_000);

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + body);

        final Request request = new Request.Builder()
                .url(contracturl)
                .post(RequestBody.create(MediaType.get("application/json"), body))
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();

        try (Response resp = client.newCall(request).execute()) {
            assertTrue(resp.isSuccessful());
//            assertEquals(Side.SELL, createOrder.side());
//            assertEquals(100.0, createOrder.qty(), 0.000001);
//            assertEquals(100.0, createOrder.leverage(), 0.000001);
//            assertEquals(CurrencyPair.fromCharSequence("BTC_USD_P0"), createOrder.symbol());
//            assertEquals(TESTUSER, createOrder.username().toString());
        } catch (IOException ex) {
            fail("Error: " + ex);
        }
    }

    @Test
    public void testMarketClose() {
        final String clOrdID = "todo";
        final long clReqId = 1;

        final String body = asJson(w -> w
                .write("clientReqID").int64(clReqId)
                .write("clOrdID").text(clOrdID)
        ).toString();

        String expires = "" + (System.currentTimeMillis() + 60_000);

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + "/marketClose" + body);

        final Request request = new Request.Builder()
                .url(contracturl + "/marketClose")
                .post(RequestBody.create(MediaType.get("application/json"), body))
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();

        try (Response resp = client.newCall(request).execute()) {
            assertTrue(resp.isSuccessful());
//            assertEquals(clOrdID, unwindOrder.clOrdID());
//            assertTrue(Double.isNaN(unwindOrder.qty()));
//            assertEquals(TESTUSER, unwindOrder.username().toString());
        } catch (IOException ex) {
            fail("Error: " + ex);
        }

    }

    @Test
    public void testAmendOrder() {
        final String clOrdID = "todo";
        final long clReqId = 1;

        final String body = asJson(w -> w
                .write("clientReqID").int64(clReqId)
                .write("clOrdID").text(clOrdID)
                .write("leverage").float64(34.56)
        ).toString();

        String expires = "" + (System.currentTimeMillis() + 60_000);

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + body);

        final Request request = new Request.Builder()
                .url(contracturl)
                .put(RequestBody.create(MediaType.get("application/json"), body))
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();

        try (Response resp = client.newCall(request).execute()) {
            assertTrue(resp.isSuccessful());
//                assertEquals(clOrdID, amendOrder.clOrdID());
//                assertEquals(34.56, amendOrder.leverage(), 0.000001);
//                assertEquals(0, amendOrder.price(), 0);
//                assertEquals(TESTUSER, amendOrder.username().toString());
        } catch (IOException ex) {
            fail("Error: " + ex);
        }

    }

    @Test
    public void testCancelOrder() {
        final String clOrdID = "todo";
        final long clReqId = 1;

        final String body = asJson(w -> w
                .write("clOrdID").text(clOrdID)
                .write("clientReqID").int64(clReqId)
        ).toString();

        String expires = "" + (System.currentTimeMillis() + 60_000);

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + body);

        final Request request = new Request.Builder()
                .url(contracturl)
                .delete(RequestBody.create(MediaType.get("application/json"), body))
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();

        try (Response resp = client.newCall(request).execute()) {
            assertTrue(resp.isSuccessful());
//            assertEquals(clOrdID, cancelOrder.clOrdID());
//            assertEquals(TESTUSER, cancelOrder.username().toString());
        } catch (IOException ex) {
            fail("Error: " + ex);
        }
    }

    private CharSequence asJson(WriteMarshallable o) {
        Bytes bytes = Wires.acquireBytes();
        WireType.JSON.apply(bytes).getValueOut().marshallable(o);
        return bytes;
    }
}
