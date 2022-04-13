package de.danielkoellgen.srscsdeckservice.controller.card.dto;

import de.danielkoellgen.srscsdeckservice.domain.card.domain.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record HintDto(

    @NotNull
    List<ContentElementDto> content

) {
    public HintDto(Hint view) {
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

    public @NotNull Hint mapToHint() {
        return new Hint(getContent());
    }
}
