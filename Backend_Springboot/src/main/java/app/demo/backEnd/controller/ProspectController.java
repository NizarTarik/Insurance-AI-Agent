package app.demo.backEnd.controller;

import app.demo.backEnd.model.dto.ProspectDTO.CreationRequest;
import app.demo.backEnd.model.entity.Prospect;
import app.demo.backEnd.repository.ProspectRepository;
import app.demo.backEnd.service.ProspectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prospect")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ProspectController {

    private final ProspectService prospectService;
    private final ProspectRepository prospectRepository;

    @Value("${internal.api.key}")
    private String apiKey;

    @PostMapping
    public ResponseEntity<Void> createProspect(
            @RequestBody CreationRequest request) {

        prospectService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Returns prospects not yet imported
     * SECURED WITH API KEY
     */
    @GetMapping("/not-imported")
    public ResponseEntity<?> getNotImportedProspects(
            @RequestHeader(value = "X-API-KEY", required = false) String requestApiKey) {

        // SECURITY CHECK
        if (requestApiKey == null ||
                !requestApiKey.equals(apiKey)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        List<Prospect> prospects = prospectRepository.findByIsSMImportedFalse();

        return ResponseEntity.ok(prospects);
    }

    /**
     * Mark imported prospects
     * SECURED WITH API KEY
     */
    @PostMapping("/mark-imported")
    public ResponseEntity<?> markImported(
            @RequestHeader(value = "X-API-KEY", required = false) String requestApiKey,
            @RequestBody List<Long> ids) {

        // SECURITY CHECK
        if (requestApiKey == null ||
                !requestApiKey.equals(apiKey)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        prospectRepository.markAsImported(ids);

        return ResponseEntity.ok().build();
    }
}