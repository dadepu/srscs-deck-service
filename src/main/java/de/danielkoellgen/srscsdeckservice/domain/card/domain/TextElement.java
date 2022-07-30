package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class TextElement implements ContentElement {

    @NotNull
    @Field("content_type")
    private ContentType contentType = ContentType.TEXT;

    @NotNull
    @Field("text")
    private final String text;

    public TextElement(@NotNull String text) {
        this.text = text;
    }

    @PersistenceConstructor
    public TextElement(@NotNull String text, @NotNull ContentType contentType) {
        this.text = text;
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "contentType=" + contentType +
                ", text='" + text + '\'' +
                '}';
    }
}
