# language: es
# Estas pruebas garantizan la Integridad de datos de flota y cumplimiento normativo en un entorno multi-tenant.

Característica: US13 - Registro de Activos

  Escenario: Registrar estado técnico
    DADO una unidad seleccionada
    CUANDO el gestor registra estado de frenos y neumáticos
    ENTONCES el sistema debe guardar estado, fecha y responsable

  Escenario: Consultar historial técnico
    DADO que existen inspecciones previas
    CUANDO el gestor consulta la unidad
    ENTONCES debe visualizar el historial cronológico de evaluaciones

  Escenario: Alertar condición crítica
    DADO que una inspección reporta estado crítico
    CUANDO se guarda el registro
    ENTONCES el sistema debe emitir una alerta preventiva
