package de.danielkoellgen.srscsdeckservice.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DurationConversionIntegrationTest {

    @Autowired
    private StringToDurationConverter stringToDurationConverter;

    @Autowired
    private DurationToStringConverter durationToStringConverter;

    @Test
    public void shouldAllowToConvertDurations() {
        Duration duration = Duration.ofSeconds(1000);
        String converted = durationToStringConverter.convert(duration);
        Duration deConverted = stringToDurationConverter.convert(converted);

        assertThat(duration)
                .isEqualTo(deConverted);
    }
}
