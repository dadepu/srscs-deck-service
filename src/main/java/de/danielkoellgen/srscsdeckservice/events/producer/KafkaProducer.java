package de.danielkoellgen.srscsdeckservice.events.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ProducerEvent event) {
        ProducerRecord<String, String> record = new ProducerRecord<>(event.getTopic(), event.getSerializedContent());
        record.headers().add(new RecordHeader("eventId", event.getEventId().toString().getBytes()));
        record.headers().add(new RecordHeader("transactionId", event.getTransactionId().toString().getBytes()));
        record.headers().add(new RecordHeader("version", event.getVersion().toString().getBytes()));
        record.headers().add(new RecordHeader("timestamp", event.getOccurredAt().getFormatted().getBytes()));
        record.headers().add(new RecordHeader("type", event.getEventName().getBytes()));
        kafkaTemplate.send(record);
    }
}
