package ru.job4j.pooh;

public class Resp {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String STATUS_200 = "200";
    public static final String STATUS_204 = "204";
    private final String text;
    private final String status;

    public Resp(String text, String status) {
        this.text = text;
        this.status = status;
    }


    public String text() {
        return text;
    }

    public String status() {
        return status;
    }
}
