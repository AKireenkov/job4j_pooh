package ru.job4j.pooh;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TopicServiceTest {

    @Test
    public void whenTopic() {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenSeveralTopics() {
        TopicService topicService = new TopicService();
        String paramForPublisher1 = "temperature=18";
        String paramForPublisher2 = "java";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        /*
        Подписались как client407 и client6565 на два разных топика
         */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        topicService.process(
                new Req("GET", "topic", "jobs", paramForSubscriber2)
        );
        /*
        Отправили в каждый топик свое значение
         */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher1)
        );
        topicService.process(
                new Req("POST", "topic", "jobs", paramForPublisher2)
        );
        /*
        Получили значение
         */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        Resp result2 = topicService.process(
                new Req("GET", "topic", "jobs", paramForSubscriber2)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is("java"));
    }

    @Test
    public void whenOneSubscriberOnSeveralTopics() {
        TopicService topicService = new TopicService();
        String paramForPublisher1 = "temperature=18";
        String paramForPublisher2 = "java";
        String paramForSubscriber1 = "client407";
        /*
        Подписались как client407 на два разных топика
         */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        topicService.process(
                new Req("GET", "topic", "jobs", paramForSubscriber1)
        );
        /*
        Отправили в каждый топик свое значение
         */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher1)
        );
        topicService.process(
                new Req("POST", "topic", "jobs", paramForPublisher2)
        );
        /*
        Получили значение
         */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        Resp result2 = topicService.process(
                new Req("GET", "topic", "jobs", paramForSubscriber1)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is("java"));
    }


    @Test
    public void whenTopicDoesNotExist() {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        Resp result = topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        assertThat(result.text(), is("Топик с названием weather - отсутствует"));
        assertThat(result.status(), is("204"));
        assertThat(result1.text(), is(""));
    }


}