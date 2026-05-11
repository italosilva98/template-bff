package itau.template.bff.shared.observability

// Ponto de extensão para log estruturado em JSON (produção).
//
// O padrão de log com correlationId já está configurado em application.yml via:
//   logging.pattern.console
//
// Para ativar logs estruturados em JSON:
//   1. Adicionar dependência:
//        net.logstash.logback:logstash-logback-encoder
//   2. Criar src/main/resources/logback-spring.xml com appender JsonEncoder.
//   3. Configurar perfil de produção para usar o novo appender.
//
// Não há beans a registrar aqui — sem @Configuration.
class LoggingConfig
