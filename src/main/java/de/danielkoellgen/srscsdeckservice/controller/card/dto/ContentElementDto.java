package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.ContentType;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ImageElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.TextElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ContentElementDto(

    @NotNull
    String contentType,

    @Nullable
    String text,

    @Nullable
    String url
) {
    public static ContentElementDto makeAsText(@NotNull TextElement textElement) {
        return new ContentElementDto("text", textElement.getText(), null);
    }

    public static ContentElementDto makeAsImage(@NotNull ImageElement imageElement) {
        return new ContentElementDto("image", null, imageElement.getUrl());
    }

    public @NotNull ContentType getContentType() {
        return switch (contentType) {
            case "text" -> ContentType.TEXT;
            case "image" -> ContentType.IMAGE;
            default -> throw new RuntimeException("Invalid content-type.");
        };
    }

    public @NotNull ImageElement getAsImageElement() {
        if (getContentType() != ContentType.IMAGE) {
            throw new RuntimeException("Invalid type-cast to type ImageElement.");
        }
        if (url == null) {
            throw new RuntimeException("Url not set while trying to cast to type ImageElement.");
        }
        return new ImageElement(url);
    }

    public @NotNull TextElement getAsTextElement() {
        if (getContentType() != ContentType.TEXT) {
            throw new RuntimeException("Invalid type-cast to type TextElement.");
        }
        if (text == null) {
            throw new RuntimeException("Text not set while trying to cast to type TextElement.");
        }
        return new TextElement(text);
    }
}
