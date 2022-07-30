package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
public class View {

    @NotNull
    @Field("content_elements")
    private final List<ContentElement> contentElements;

    @PersistenceConstructor
    public View(@NotNull List<ContentElement> contentElements) {
        this.contentElements = contentElements;
    }

    @Override
    public String toString() {
        return "View{" +
                "contentElements=" + contentElements +
                '}';
    }
}
