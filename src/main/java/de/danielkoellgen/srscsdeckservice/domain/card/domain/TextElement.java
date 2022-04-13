package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class TextElement implements ContentElement {

    @NotNull
    @Field("content_type")
    private final ContentType contentType = ContentType.TEXT;

    @NotNull
    @Field("text")
    private final String text;

    @PersistenceConstructor
    public TextElement(@NotNull String text) {
        this.text = text;
    }
}
