package de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain;

import de.danielkoellgen.srscsdeckservice.domain.core.AbstractStringValidation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = false)
public class PresetName extends AbstractStringValidation {

    @Getter
    @Field("name")
    private final @NotNull String name;

    public PresetName(@NotNull String name) throws Exception {
        validateNameOrThrow(name);
        this.name = name;
    }

    private void validateNameOrThrow(@NotNull String name) throws Exception {
        validateMinLengthOrThrow(name, 4, this::mapToException);
        validateMaxLengthOrThrow(name, 16, this::mapToException);
        validateRegexOrThrow(name, "^([A-Za-z0-9]){4,16}$", this::mapToException);
    }

    private Exception mapToException(String message) {
        return new PresetNameException(message);
    }
}
