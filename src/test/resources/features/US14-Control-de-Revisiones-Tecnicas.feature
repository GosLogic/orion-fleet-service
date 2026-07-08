# language: es
# Estas pruebas garantizan la Integridad de datos de flota y cumplimiento normativo en un entorno multi-tenant.

Característica: US14 - Control de Revisiones Técnicas

  Escenario: Programar vencimientos
    DADO un vehículo con documentación vigente
    CUANDO se registran fechas de revisión
    ENTONCES el sistema debe programar recordatorios automáticos

  Escenario: Alertar vencimiento próximo
    DADO una revisión cercana a su fecha límite
    CUANDO entra en ventana de alerta
    ENTONCES el sistema debe notificar al responsable

  Escenario: Marcar riesgo legal
    DADO una revisión vencida
    CUANDO el gestor revisa el estado del vehículo
    ENTONCES el sistema debe mostrar riesgo legal activo
