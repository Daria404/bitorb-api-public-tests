package com.bitorb.api.pub.tests;

import javafx.util.Builder;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.Wire;
import net.openhft.chronicle.wire.WireType;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.*;

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
    private String RESOURCE_PATH;
    private static Logger log = Logger.getLogger(TradeBook.class.getName());

    public void webSocket() {

        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String socketURL = "ws://" + HOST + SUBSCRIBE_PATH;

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + SUBSCRIBE_PATH);

        final Request request = new Request.Builder()
                .url(socketURL)
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();
        try {
            WebSocket webSocket = websocketClient.newWebSocket(request, this);
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        }

    }

    public Order createOrder(CreateOrder createOrder) {
        String body = createOrder.getBody();
        RESOURCE_PATH = "/order";

        String expires = "" + (System.currentTimeMillis() + 60_000);
        final String contractURL = "http://" + HOST + REQUEST_PATH + RESOURCE_PATH;

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + RESOURCE_PATH + body);

        Request request = buildPostRequest(contractURL, body, expires, signature);

        try (Response resp = client.newCall(request).execute()) {

            checkResponseCode(resp);
            Thread.sleep(1000);
        } catch (IOException | InterruptedException ex) {
            fail("Error: " + ex);
        } finally {
            return order;
        }

    }

    @Override
    public String getActiveOrder(String clOrdId) {
        String contractURL = "";
        String responseText = "";
        String orderString = "";
        RESOURCE_PATH = "/order";
        String expires = "" + (System.currentTimeMillis() + 60_000);

        try {
            contractURL = "http://" + HOST + REQUEST_PATH + "/order?clOrdId="
                    + URLEncoder.encode(clOrdId, StandardCharsets.UTF_8.toString());

        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        }

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + RESOURCE_PATH);
        Request request = buildGetRequest(contractURL, expires, signature);

        try (Response resp = client.newCall(request).execute()) {
            checkResponseCode(resp);
            orderString = "Order" + resp.body().string();
        } catch (Exception ex) {
            System.out.println("Get active order - " + ex.getMessage());
        }
        return orderString;
    }

    @Override
    public Book getOrderBook() {
        return getOrderBook("BTC_USD_P0", null);
    }

    @Override
    public Book getOrderBook(String symbol) {
        return getOrderBook(symbol, null);
    }

    @Override
    public Book getOrderBook(String symbol, Integer level) {
        String responseText = "";
        String contractURL = "";
        RESOURCE_PATH = "/orderBook";
        String expires = "" + (System.currentTimeMillis() + 60_000);

        try {
            contractURL = "http://" + HOST + REQUEST_PATH + "/orderBook?symbol="
                    + URLEncoder.encode(symbol, StandardCharsets.UTF_8.toString())
                    + "&level=" + URLEncoder.encode(String.valueOf(level), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        }

        final String signature = HashUtils.getSecretHash(APISECRET, APIKEY + expires + REQUEST_PATH + RESOURCE_PATH);

        Request request = buildGetRequest(contractURL, expires, signature);

        try (Response resp = client.newCall(request).execute()) {
            checkResponseCode(resp);
            responseText = (resp.body().string());
            return parser.fromJSONtoObj(responseText, Book.class);
        } catch (Exception ex) {
            System.out.println("Get orderBook - " + ex.getMessage());
            return parser.fromJSONtoObj(responseText, Book.class);
        }
    }

    public static void checkResponseCode(Response resp) {
        if (resp.code() == 500) {
            throw new ResponseExceptions.WebserverInternalErrorException("Webserver internal error", 500);
        } else if (resp.code() == 401) {
            throw new ResponseExceptions.UnauthorizedException("Check your api key, api signature and api expires are in sync", 401);
        } else if (resp.code() == 400) {
            throw new ResponseExceptions.InvalidParameterException("Invalid parameter", 400);
        } else if (resp.code() == 200 && resp.body() == null) {
            throw new ResponseExceptions.EmptyBodyException("Response body is empty", 200);
        }
    }

    public static Request buildPostRequest(String URL, String body, String expires, String signature) {
        return new Request.Builder()
                .url(URL)
                .post(RequestBody.create(MediaType.get("application/json"), body))
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();
    }

    public static Request buildGetRequest(String URL, String expires, String signature) {
        return new Request.Builder()
                .url(URL)
                .get()
                .header("api-key", APIKEY)
                .header("api-expires", expires)
                .header("api-signature", signature)
                .build();
    }


    public static void main(String[] args) {
        TradeBook tBook = new TradeBook();
        CreateOrder createOrder = new CreateOrder(100, "BTC_USD_P0", "BUY", 40000.0, 1.0, 2.0);
        tBook.webSocket();
        Order currentOrder = tBook.createOrder(createOrder);
        String orderID = currentOrder.clOrdID;
        System.out.println(tBook.getActiveOrder("orderID"));
//        System.out.println(tBook.getOrderBook());
//        System.out.println(tBook.getOrderBook("BTC_USD_P0"));
        System.out.println(tBook.getOrderBook("BTC_USD_P0", 1));

        tBook.client.dispatcher().executorService().shutdown();
        tBook.client.connectionPool().evictAll();
        tBook.websocketClient.dispatcher().executorService().shutdown();
        tBook.websocketClient.connectionPool().evictAll();
    }

}


