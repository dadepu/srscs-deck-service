package de.danielkoellgen.srscsdeckservice.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@WritingConverter
public class DurationToStringConverter implements Converter<Duration, String> {

    @Override
    public String convert(Duration source) {
        return source.getSeconds()+","+source.getNano();
    }
}
