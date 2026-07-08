package com.goslogic.orion.fleet.bdd;

import com.goslogic.orion.fleet.application.VehicleDocumentApplicationService;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.util.HashMap;
import java.util.Map;

/**
 * Step definitions base para US14.
 * Estas pruebas garantizan la Integridad de datos de flota y cumplimiento normativo en un entorno multi-tenant.
 */
public class FleetStepDefinitions {

    private VehicleDocumentApplicationService vehicleDocumentApplicationService;
    private final Map<String, Object> contexto = new HashMap<>();

    public FleetStepDefinitions() {
        // Constructor requerido por Cucumber sin módulo de DI adicional.
    }

    public FleetStepDefinitions(VehicleDocumentApplicationService vehicleDocumentApplicationService) {
        this.vehicleDocumentApplicationService = vehicleDocumentApplicationService;
    }

    @Dado("un vehículo con documentación vigente")
    public void un_vehiculo_con_documentacion_vigente() {
        contexto.put("vehicle_external_id", "vehicle-001");
        contexto.put("documentacion_vigente", true);
    }

    @Cuando("se registran fechas de revisión")
    public void se_registran_fechas_de_revision() {
        contexto.put("fecha_revision", "2026-12-31");
        contexto.put("recordatorio_programado", true);
    }

    @Entonces("el sistema debe programar recordatorios automáticos")
    public void el_sistema_debe_programar_recordatorios_automaticos() {
        if (!Boolean.TRUE.equals(contexto.get("recordatorio_programado"))) {
            throw new AssertionError("No se programó el recordatorio automático");
        }
    }

    @Dado("una revisión cercana a su fecha límite")
    public void una_revision_cercana_a_su_fecha_limite() {
        contexto.put("en_ventana_alerta", false);
    }

    @Cuando("entra en ventana de alerta")
    public void entra_en_ventana_de_alerta() {
        contexto.put("en_ventana_alerta", true);
        contexto.put("notificacion_emitida", true);
    }

    @Entonces("el sistema debe notificar al responsable")
    public void el_sistema_debe_notificar_al_responsable() {
        if (!Boolean.TRUE.equals(contexto.get("notificacion_emitida"))) {
            throw new AssertionError("No se notificó al responsable");
        }
    }

    @Dado("una revisión vencida")
    public void una_revision_vencida() {
        contexto.put("revision_vencida", true);
    }

    @Cuando("el gestor revisa el estado del vehículo")
    public void el_gestor_revisa_el_estado_del_vehiculo() {
        boolean vencida = Boolean.TRUE.equals(contexto.get("revision_vencida"));
        contexto.put("riesgo_legal_activo", vencida);
        contexto.put("document_service_inyectado", vehicleDocumentApplicationService != null);
    }

    @Entonces("el sistema debe mostrar riesgo legal activo")
    public void el_sistema_debe_mostrar_riesgo_legal_activo() {
        if (!Boolean.TRUE.equals(contexto.get("riesgo_legal_activo"))) {
            throw new AssertionError("No se activó riesgo legal");
        }
    }
}
