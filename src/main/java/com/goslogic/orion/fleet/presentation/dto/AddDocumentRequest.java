package com.goslogic.orion.fleet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddDocumentRequest(
        @NotBlank @JsonProperty("document_type") String documentType,
        @NotBlank @JsonProperty("document_number") String documentNumber,
        @NotNull @JsonProperty("expiry_date") String expiryDate
) {}
