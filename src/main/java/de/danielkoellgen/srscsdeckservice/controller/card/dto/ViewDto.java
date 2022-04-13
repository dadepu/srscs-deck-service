package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.ContentElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ImageElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.TextElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.View;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ViewDto(

    @NotNull
    List<ContentElementDto> content

) {
    public ViewDto(View view) {
        this(view.getContentElements().stream().map(element -> {
            return switch(element.getContentType()) {
                case TEXT -> ContentElementDto.makeAsText((TextElement) element);
                case IMAGE -> ContentElementDto.makeAsImage((ImageElement) element);
            };
        }).toList());
    }

    public @NotNull List<ContentElement> getContent() {
        return content.stream().map(element -> {
            return switch(element.getContentType()) {
                case TEXT -> element.getAsTextElement();
                case IMAGE -> element.getAsImageElement();
            };
        }).toList();
    }

    public @NotNull View mapToView() {
        return new View(getContent());
    }
}
