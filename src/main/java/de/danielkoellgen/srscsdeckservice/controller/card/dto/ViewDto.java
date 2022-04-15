package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ContentElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.ImageElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.TextElement;
import de.danielkoellgen.srscsdeckservice.domain.card.domain.View;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ViewDto(

    @NotNull List<ContentElementDto> content

) {
    public ViewDto(View view) {
        this(view.getContentElements().stream().map(element -> {
            return switch(element.getContentType()) {
                case TEXT -> ContentElementDto.makeAsText((TextElement) element);
                case IMAGE -> ContentElementDto.makeAsImage((ImageElement) element);
            };
        }).toList());
    }

    @JsonIgnore
    public @NotNull List<ContentElement> getMappedContent() {
        return content.stream().map(element -> {
            return switch(element.getMappedContentType()) {
                case TEXT -> element.getAsTextElement();
                case IMAGE -> element.getAsImageElement();
            };
        }).toList();
    }

    @JsonIgnore
    public @NotNull View mapToView() {
        return new View(getMappedContent());
    }
}
