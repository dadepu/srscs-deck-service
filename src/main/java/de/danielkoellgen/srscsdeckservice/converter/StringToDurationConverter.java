package de.danielkoellgen.srscsdeckservice.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ReadingConverter
public class StringToDurationConverter implements Converter<String, Duration> {

    @Override
    public Duration convert(String source) {
        String[] separated = source.split(",");
        return Duration.ofSeconds(Long.parseLong(separated[0]), Integer.parseInt(separated[1]));
    }
}
