package ru.job4j.pooh;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ru.job4j.pooh.Req.GET;
import static ru.job4j.pooh.Req.POST;
import static ru.job4j.pooh.Resp.STATUS_200;
import static ru.job4j.pooh.Resp.STATUS_204;

public class TopicService implements Service {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp = null;
        String httpRequestType = req.httpRequestType();
        String sourceName = req.getSourceName();
        String subscriber = req.getParam();
        Optional<ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topic;
        Optional<ConcurrentLinkedQueue<String>> subscriberQueue;
        ConcurrentLinkedQueue<String> concurrentLinkedQueue;
        if (GET.equals(httpRequestType)) {
            topic = Optional.ofNullable(topics.get(sourceName));
            String text;
            if (topic.isEmpty()) {
                topics.putIfAbsent(sourceName, new ConcurrentHashMap<>());
                topic = Optional.ofNullable(topics.get(sourceName));
            }
            subscriberQueue = Optional.ofNullable(topic.get().get(subscriber));
            if (subscriberQueue.isEmpty()) {
                topic.get().putIfAbsent(subscriber, new ConcurrentLinkedQueue<>());
                text = "";
            } else {
                concurrentLinkedQueue = topic.get().get(subscriber);
                text = concurrentLinkedQueue.poll();
            }
            resp = new Resp(text, STATUS_200);
        }
        if (POST.equals(httpRequestType)) {
            topic = Optional.ofNullable(topics.get(sourceName));
            if (topic.isEmpty()) {
                resp = new Resp("Топик с названием " + sourceName + " - отсутствует", STATUS_204);
            } else {
                ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> as =
                        topic.get();
                for (ConcurrentLinkedQueue<String> queue : as.values()) {
                    queue.add(req.getParam());
                }
                resp = new Resp(req.getParam(), STATUS_200);
            }
        }
        return resp;
    }
}