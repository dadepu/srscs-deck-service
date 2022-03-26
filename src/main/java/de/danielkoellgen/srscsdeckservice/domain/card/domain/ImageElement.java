package de.danielkoellgen.srscsdeckservice.domain.card.domain;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class ImageElement implements ContentElement {

    @NotNull
    @Field("content_type")
    private final ContentType contentType = ContentType.IMAGE;

    @NotNull
    @Field("url")
    private final String url;

    @PersistenceConstructor
    public ImageElement(@NotNull String url) {
        this.url = url;
    }
}
