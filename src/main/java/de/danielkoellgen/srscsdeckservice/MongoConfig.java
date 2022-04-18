package de.danielkoellgen.srscsdeckservice;

import de.danielkoellgen.srscsdeckservice.converter.DurationToStringConverter;
import de.danielkoellgen.srscsdeckservice.converter.StringToDurationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List list = new ArrayList<>();
        list.add(new DurationToStringConverter());
        list.add(new StringToDurationConverter());
        return new MongoCustomConversions(list);
    }
}
