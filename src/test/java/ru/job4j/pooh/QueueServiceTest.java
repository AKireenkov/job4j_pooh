package ru.job4j.pooh;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class QueueServiceTest {

    @Test
    void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("temperature=18"));
    }

    @Test
    void whenQueueEmptyThenGet() {
        QueueService queueService = new QueueService();
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("Очередь с именем weather - пуста"));
        assertThat(result.status(), is("204"));
    }

    @Test
    void whenPostInDifferentQueueThenGet() {
        QueueService queueService = new QueueService();
        String paramForFirstPostMethod = "java";
        String paramForSecondPostMethod = "temperature=18";

        queueService.process(
                new Req("POST", "queue", "programming", paramForFirstPostMethod)
        );
        queueService.process(
                new Req("POST", "queue", "weather", paramForSecondPostMethod)
        );

        Resp resultFirst = queueService.process(
                new Req("GET", "queue", "programming", null)
        );
        Resp resultSecond = queueService.process(
                new Req("GET", "queue", "weather", null)
        );

        assertThat(resultFirst.text(), is("java"));
        assertThat(resultSecond.text(), is("temperature=18"));
    }

    @Test
    void whenPostTwoElementsInOneQueue() {
        QueueService queueService = new QueueService();
        String paramForFirstPostMethod = "temperature=20";
        String paramForSecondPostMethod = "temperature=18";

        queueService.process(
                new Req("POST", "queue", "weather", paramForFirstPostMethod)
        );
        queueService.process(
                new Req("POST", "queue", "weather", paramForSecondPostMethod)
        );

        Resp resultFirst = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(resultFirst.text(), is("temperature=20"));

        Resp resultSecond = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(resultSecond.text(), is("temperature=18"));
    }
}