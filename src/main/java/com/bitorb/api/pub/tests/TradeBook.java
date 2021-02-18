package com.bitorb.api.pub.tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TradeBook extends WebSocketListener implements OrderAPI {
    private Order order;
    private Book book;
    JSONParser parser = new JSONParser();

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
//        System.out.println(text);
        switch (key) {
            case "order": {
                order = parser.fromJSONtoObj(text, Order.class);
                break;
            }
            case "tob": {
                book = parser.fromJSONtoObj(text, Book.class);
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
    private static final String REQUEST_PATH = "/api/v1";
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

    public Order createOrder(CreateOrder createOrder) throws InterruptedException {
        String body = createOrder.getBody();
        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH + "/order";

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + "/order" + body);

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
        String orderString = "Order";
        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH + "/order?clOrdId=" + URLEncoder.encode(clOrdId, StandardCharsets.UTF_8.toString());
        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + "/order");
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
            orderString += resp.body().string();
        } catch (IOException | InterruptedException ex) {
            fail("Error: " + ex);
        }
        return orderString;
    }

    @Override
    public Book getOrderBook() {
        String responseText;
        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH + "/orderBook";
        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + "/orderBook");
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
            responseText = (resp.body().string());
            book = parser.fromJSONtoObj(responseText, Book.class);
        } catch (IOException | InterruptedException ex) {
            fail("Error: " + ex);
        }
        return book;
    }

    @Override
    public Book getOrderBook(String symbol) throws UnsupportedEncodingException {
        String responseText = "";
        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH + "/orderBook?symbol=" + URLEncoder.encode(symbol, StandardCharsets.UTF_8.toString());
        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + "/orderBook");
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
            responseText = (resp.body().string());
            book = parser.fromJSONtoObj(responseText, Book.class);
        } catch (IOException | InterruptedException ex) {
            fail("Error: " + ex);
        }
        return book;
    }

    @Override
    public Book getOrderBook(String symbol, Integer level) throws UnsupportedEncodingException {
        String responseText = "";
        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH + "/orderBook?symbol="
                + URLEncoder.encode(symbol, StandardCharsets.UTF_8.toString())
                + "&level=" + URLEncoder.encode(String.valueOf(level), StandardCharsets.UTF_8.toString());
        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + "/orderBook");
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
            responseText = (resp.body().string());
            book = parser.fromJSONtoObj(responseText, Book.class);
        } catch (IOException | InterruptedException ex) {
            fail("Error: " + ex);
        }
        return book;
    }

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
        TradeBook tBook = new TradeBook();
        CreateOrder createOrder = new CreateOrder();
        tBook.webSocket();
        Order currentOrder = tBook.createOrder(createOrder);
        String orderID = currentOrder.clOrdID;
        System.out.println(tBook.getActiveOrder(orderID));
//        System.out.println(tBook.getOrderBook());
//        System.out.println(tBook.getOrderBook("BTC_USD_P0"));
        System.out.println(tBook.getOrderBook("BTC_USD_P0", 1));

    }

}


