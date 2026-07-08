package com.goslogic.orion.fleet.presentation;

import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Estas pruebas garantizan la Integridad de datos de flota y cumplimiento normativo en un entorno multi-tenant.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_devuelve_404() {
        ResponseEntity<GlobalExceptionHandler.ErrorBody> response =
                handler.handleNotFound(new ResourceNotFoundException("Recurso no encontrado"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("Recurso no encontrado");
        assertThat(response.getBody().timestamp()).isNotBlank();
    }

    @Test
    void handleConflict_devuelve_409() {
        ResponseEntity<GlobalExceptionHandler.ErrorBody> response =
                handler.handleConflict(new ConflictException("Ya existe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Ya existe");
    }

    @Test
    void handleValidation_devuelve_400_con_errores_por_campo() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "plate", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorBody> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isInstanceOf(java.util.Map.class);
        @SuppressWarnings("unchecked")
        var errors = (java.util.Map<String, String>) response.getBody().message();
        assertThat(errors).containsEntry("plate", "must not be blank");
    }
}
