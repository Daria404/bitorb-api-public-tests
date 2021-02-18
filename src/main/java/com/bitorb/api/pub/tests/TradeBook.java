package com.bitorb.api.pub.tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.Wire;
import net.openhft.chronicle.wire.WireType;
import net.openhft.chronicle.wire.Wires;
import net.openhft.chronicle.wire.WriteMarshallable;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OrderBook extends WebSocketListener implements OrderBookInterface {
    private String innerContain;
    private Order order;
    private Book book;

    @Override
    public Book getOrderBook() {
        return book;
    }

    @Override
    public Book getOrderBook(String symbol) {
        return book;
    }

    @Override
    public Book getOrderBook(String symbol, Integer level) {
        return book;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        boolean test = webSocket.send(
                "{" +
                        "op: subscribe, " +
                        "args: { " +
                        "streams: trade,account,book" + // funding, account
                        "symbols: BTC_USD_P0" + // If no specific symbol(s) then will receive all symbols
                        "}" +
                        "}");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Bytes bytes = Bytes.elasticByteBuffer();
        bytes.write(text);
        Wire wire = WireType.JSON.apply(bytes);
        String key = wire.read("key").text();
        JSONParser parser = new JSONParser();
//        System.out.println(text);
        switch (key) {
            case "order": {
                order = parser.fromJSONtoObj(text, Order.class);
//                System.out.println(order.toString());
                break;
            }
            case "tob": {
                Book book = parser.fromJSONtoObj(text, Book.class);
                System.out.println(book.toString());
                break;
            }
        }
    }


    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, "bye");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        t.printStackTrace();
    }

    private static final String HOST = "localhost:9090";
    private static final String APISECRET = "mmsecret";
    private static final String USER = "marketmaker";
    private static final String APIKEY = "marketmaker";
    private final OkHttpClient client = new OkHttpClient();
    private final OkHttpClient websocketClient = new OkHttpClient.Builder().pingInterval(java.time.Duration.ofSeconds(5)).build();
    private static final String REQUEST_PATH = "/api/v1/order";
    private static final String SUBSCRIBE_PATH = "/api/v1/subscribe";


    public void webSocket() throws InterruptedException {

        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String socketURL = "ws://" + HOST + SUBSCRIBE_PATH;

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + SUBSCRIBE_PATH);

        final Request request = new Request.Builder()
                .url(socketURL)
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();
        WebSocket webSocket = websocketClient.newWebSocket(request, this);

        Thread.sleep(5000);
    }

    public Order createOrder() throws InterruptedException {
        final String body = asJson(w -> w
                .write("clientReqID").int64(1)
                .write("symbol").text("BTC_USD_P0")
                .write("side").text("BUY")
                .write("qty").float64(1.0)
                .write("leverage").float64(1.0)
                .write("price").float64(42200)
                .write("ordType").character('2')
        ).toString();

        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH;

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + body);

        final Request request = new Request.Builder()
                .url(contractURL)
                .post(RequestBody.create(MediaType.get("application/json"), body))
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();

        try (Response resp = client.newCall(request).execute()) {
            Thread.sleep(5000);
            assertTrue(resp.toString(), resp.isSuccessful());
        } catch (IOException ex) {
            fail("Error: " + ex);
        }
        return order;
    }

    @Override
    public String getActiveOrder(String clOrdId) throws UnsupportedEncodingException {
        String orderString = " ";
        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH + "?clOrdId="+ URLEncoder.encode(clOrdId, StandardCharsets.UTF_8.toString());;

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH);

        final Request request = new Request.Builder()
                .url(contractURL)
                .get()
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();

        try (Response resp = client.newCall(request).execute()) {
            Thread.sleep(5000);
            assertTrue(resp.toString(), resp.isSuccessful());
            assert resp.body() != null;
            orderString =  resp.body().string();
        } catch (IOException | InterruptedException ex) {
            fail("Error: " + ex);
        }
        return orderString;
    }

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
        OrderBook book = new OrderBook();
        book.webSocket();
        Order currentOrder = book.createOrder();
        String orderID = currentOrder.clOrdID;
        System.out.println(book.getOrderBook());
        System.out.println(book.getActiveOrder(orderID));

    }

    private CharSequence asJson(WriteMarshallable o) {
        Bytes bytes = Wires.acquireBytes();
        WireType.JSON.apply(bytes).getValueOut().marshallable(o);
        return bytes;
    }

}


