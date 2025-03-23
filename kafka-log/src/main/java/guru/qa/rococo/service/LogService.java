package guru.qa.rococo.service;

import guru.qa.rococo.data.LogEntity;
import guru.qa.rococo.data.repository.LogRepository;
import guru.qa.rococo.model.LogJson;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {

    private static final Logger LOG = LoggerFactory.getLogger(LogService.class);

    private final LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional
    @KafkaListener(topics = {"userdata", "artist", "museum", "painting"}, groupId = "kafka-log")
    public void logListener(@Payload LogJson log, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOG.info("### Kafka received message from topic [{}]: {}", topic, log);
        saveLog(log);
    }

    private void saveLog(@Nonnull LogJson log) {
        LogEntity logDataEntity = new LogEntity();
        logDataEntity.setEventType(log.eventType());
        logDataEntity.setEntityId(log.entityId());
        logDataEntity.setDescription(log.description());
        logDataEntity.setEventDate(log.eventDate());
        LogEntity logEntity = logRepository.save(logDataEntity);
        LOG.info(String.format(
                "### Event '%s' successfully saved to database with id: %s",
                logEntity.getEventType(),
                logEntity.getId()
        ));
    }
}
