package ru.job4j.pooh;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ru.job4j.pooh.Resp.*;

public class QueueService implements Service {

    private final Map<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp = null;
        String httpRequestType = req.httpRequestType();
        String sourceName = req.getSourceName();
        Optional<ConcurrentLinkedQueue<String>> value = Optional.ofNullable(queue.get(sourceName));

        if (GET.equals(httpRequestType)) {
            if (value.isEmpty()) {
                resp = new Resp("Очередь с именем " + sourceName + " - пуста", STATUS_204);
            } else {
                resp = new Resp(value.get().poll(), STATUS_200);
            }
        }
        if (POST.equals(httpRequestType)) {
            ConcurrentLinkedQueue<String> concurrentLinkedQueue;
            if (value.isEmpty()) {
                concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
            } else {
                concurrentLinkedQueue = value.get();
            }
            concurrentLinkedQueue.add(req.getParam());
            queue.putIfAbsent(sourceName, concurrentLinkedQueue);
            resp = new Resp(req.getParam(), STATUS_200);
        }
        return resp;
    }
}
