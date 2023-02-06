package ru.job4j.pooh;

import java.util.List;

public class Req {
    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;


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
        List<String> str = List.of(content.split(System.lineSeparator()));
        String[] firstStr = str.get(0).split(" ");
        String httpRequestType = firstStr[0];
        String[] modeAndName = firstStr[1].split("/");
        String poohMode = modeAndName[1];
        String sourceName = modeAndName[2];
        String param;
        if ("GET".equals(httpRequestType)) {
            param = modeAndName.length > 3 ? modeAndName[3] : "";
        } else {
            param = str.get(str.size() - 1);
        }
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
}
