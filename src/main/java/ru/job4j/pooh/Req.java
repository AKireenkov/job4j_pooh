package ru.job4j.pooh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Req {
    public static final String GET = "GET";
    public static final String POST = "POST";
    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    private static Map<String, String> headers = new HashMap<>();
    private static Map<String, String> startingLineElements = new HashMap<>();
    private static List<String> requestStrings = new ArrayList<>();

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        return parseRequest(content);
    }

    private static Req parseRequest(String content) {
        requestStrings = List.of(content.split(System.lineSeparator()));
        parseHeaders();
        parseStartingLine();
        String httpRequestType = startingLineElements.get("httpRequestType");
        String poohMode = startingLineElements.get("mode");
        String sourceName = startingLineElements.get("name");
        String param = parseMessageBody();
        return new Req(httpRequestType, poohMode, sourceName, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }

    private static void parseStartingLine() {
        String[] temp = requestStrings.get(0).split(" ");
        String[] url = temp[1].split("/");
        startingLineElements.put("httpRequestType", temp[0]);
        startingLineElements.put("mode", url[1]);
        startingLineElements.put("name", url[2]);
        String param = url.length > 3 ? url[3] : "";
        startingLineElements.put("param", param);
    }

    private static void parseHeaders() {
        headers = requestStrings.stream().filter(str -> str.contains(":")).collect(Collectors.toMap(
                k -> k.split(":", 2)[0],
                v -> v.split(":", 2)[1]
        ));
    }

    private static String parseMessageBody() {
        String text = startingLineElements.get("param") != null ? startingLineElements.get("param") : "";
        Pattern pattern = Pattern.compile("[^\\w =]");
        if (headers.containsKey("Content-Length")
                || headers.containsKey("Transfer-Encoding")) {
            text = requestStrings.stream().filter(string -> !pattern.asPredicate().test(string))
                    .collect(Collectors.joining());
        }
        return text;
    }
}
