package de.danielkoellgen.srscsdeckservice.controller.schedulerpreset;

import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetRequestDto;
import de.danielkoellgen.srscsdeckservice.controller.schedulerpreset.dto.SchedulerPresetResponseDto;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.application.SchedulerPresetService;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.domain.*;
import de.danielkoellgen.srscsdeckservice.domain.schedulerpreset.repository.SchedulerPresetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
public class SchedulerPresetController {

    private final SchedulerPresetService schedulerPresetService;
    private final SchedulerPresetRepository schedulerPresetRepository;

    private final Logger log = LoggerFactory.getLogger(SchedulerPresetController.class);

    @Autowired
    public SchedulerPresetController(SchedulerPresetService schedulerPresetService,
            SchedulerPresetRepository schedulerPresetRepository) {
        this.schedulerPresetService = schedulerPresetService;
        this.schedulerPresetRepository = schedulerPresetRepository;
    }

    @PostMapping(value = "/scheduler-presets", consumes= {"application/json"},
            produces = {"application/json"})
    @SpanName("controller-create-preset")
    public ResponseEntity<SchedulerPresetResponseDto> createPreset(
            @RequestBody SchedulerPresetRequestDto requestDto) {
        log.info("POST /scheduler-presets: Create new Preset. {}", requestDto);

        PresetName presetName;
        LearningSteps learningSteps;
        LapseSteps lapseSteps;
        MinimumInterval minimumInterval;
        EaseFactor easeFactor;
        EasyFactorModifier easyFactorModifier;
        NormalFactorModifier normalFactorModifier;
        HardFactorModifier hardFactorModifier;
        LapseFactorModifier lapseFactorModifier;
        EasyIntervalModifier easyIntervalModifier;
        LapseIntervalModifier lapseIntervalModifier;
        try {
            presetName = requestDto.getPresetName();
            learningSteps = requestDto.getLearningStepsOrDefault();
            lapseSteps = requestDto.getLapseStepsOrDefault();
            minimumInterval = requestDto.getMinimumIntervalOrDefault();
            easeFactor = requestDto.getEaseFactorOrDefault();
            easyFactorModifier = requestDto.getEasyFactorModifierOrDefault();
            normalFactorModifier = requestDto.getNormalFactorModifierOrDefault();
            hardFactorModifier = requestDto.getHardFactorModifierOrDefault();
            lapseFactorModifier = requestDto.getLapseFactorModifierOrDefault();
            easyIntervalModifier = requestDto.getEasyIntervalModifierOrDefault();
            lapseIntervalModifier = requestDto.getLapseIntervalModifierOrDefault();

        } catch (Exception e) {
            log.trace("Request failed with 400. Invalid mapping. {}", (Object) e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mapping error.", e);
        }

        SchedulerPreset schedulerPreset;
        try {
            schedulerPreset = schedulerPresetService.createPreset(presetName, requestDto.userId(),
                    learningSteps, lapseSteps, minimumInterval, easeFactor, easyFactorModifier,
                    normalFactorModifier, hardFactorModifier, lapseFactorModifier,
                    easyIntervalModifier, lapseIntervalModifier
            );
            SchedulerPresetResponseDto responseDto = new SchedulerPresetResponseDto(schedulerPreset);
            log.trace("Responding 201.");
            log.debug("{}", responseDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            log.trace("Responding with 404. User not found. {}", (Object) e.getStackTrace());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
    }

    @DeleteMapping(value = "/scheduler-presets/{scheduler-preset-id}")
    @NewSpan("controller-disable-preset")
    public ResponseEntity<?> disablePreset(@PathVariable("scheduler-preset-id") UUID presetId) {
        log.info("DELETE /scheduler-presets/{}: Disable Preset.", presetId);

        try {
            schedulerPresetService.disablePreset(presetId);
            log.trace("Responding 200.");
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (NoSuchElementException e) {
            log.trace("Request failed with 404. Preset not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Preset not found.", e);
        }
    }

    @GetMapping(value = "/scheduler-presets/{scheduler-preset-id}", produces = {"application/json"})
    @NewSpan("controller-get-preset")
    public ResponseEntity<SchedulerPresetResponseDto> getPreset(
            @PathVariable("scheduler-preset-id") UUID presetId) {
        log.info("GET /scheduler-presets/{}: Fetch Preset by id.", presetId);

        SchedulerPreset schedulerPreset;
        try {
            schedulerPreset = schedulerPresetRepository.findById(presetId).orElseThrow();
            SchedulerPresetResponseDto responseDto = new SchedulerPresetResponseDto(schedulerPreset);
            log.trace("Responding 200.");
            log.debug("{}", responseDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        } catch (NoSuchElementException e) {
            log.trace("Responding 404. User not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.", e);
        }
    }

    @GetMapping(value = "/scheduler-presets", produces = {"application/json"})
    @NewSpan("controller-get-preset-by-userid")
    public List<SchedulerPresetResponseDto> getPresetsByUserId(@RequestParam("user-id") UUID userId) {
        log.info("GET /scheduler-presets?user-id={}: Fetch Presets by user-id.", userId);

        List<SchedulerPresetResponseDto> responseDtos = schedulerPresetRepository
                .findSchedulerPresetsByEmbeddedUser_UserId(userId)
                .stream()
                .map(SchedulerPresetResponseDto::new).toList();
        log.trace("Responding 200.");
        log.debug("{} Presets fetched. {}", responseDtos.size(), responseDtos);
        return responseDtos;
    }
}
