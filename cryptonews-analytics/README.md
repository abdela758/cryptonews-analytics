# CryptoBros Analytics

## Objetivo

CryptoBros Analytics es un sistema de arquitectura Lambda que combina precios de criptomonedas en tiempo real (CoinGecko API) con noticias tecnológicas (NewsAPI) para ofrecer al usuario un dashboard analítico a través de una API REST.

El sistema permite detectar correlaciones entre eventos noticiosos y variaciones en los precios de criptomonedas, proporcionando valor añadido al usuario final mediante consultas en tiempo real e históricas.

## Arquitectura del Sistema

El proyecto sigue una arquitectura **Lambda** compuesta por cuatro módulos independientes:

```
┌──────────────┐     ┌───────────┐     ┌─────────────────────┐     ┌─────────────┐
│ crypto-feeder│────▶│           │     │ Event Store Builder │────▶│ Event Store │
│ (CoinGecko)  │     │  ActiveMQ │     │   (subscriber)      │     │  (.events)  │
└──────────────┘     │  (broker) │     └─────────────────────┘     └─────────────┘
                     │           │
┌──────────────┐     │  Topics:  │     ┌─────────────────────┐     ┌─────────────┐
│ news-feeder  │────▶│  - Crypto │     │   Business Unit     │────▶│  Datamart   │
│  (NewsAPI)   │     │  - News   │────▶│  (subscriber + API) │     │  (SQLite)   │
└──────────────┘     └───────────┘     └─────────────────────┘     └─────────────┘
```

### Módulos

- **crypto-feeder**: Captura precios de las 10 principales criptomonedas desde CoinGecko cada 30 minutos. Publica eventos en el topic `CryptoPrice` de ActiveMQ y persiste en SQLite.

- **news-feeder**: Captura titulares de noticias tecnológicas desde NewsAPI cada 30 minutos. Publica eventos en el topic `TechNews` de ActiveMQ y persiste en SQLite.

- **event-store-builder**: Se suscribe de forma durable a los topics de ActiveMQ y almacena los eventos en archivos `.events` organizados por topic, fuente y fecha (`eventstore/{topic}/{ss}/{YYYYMMDD}.events`).

- **business-unit**: Consume eventos en tiempo real desde ActiveMQ, puede cargar eventos históricos desde el event store, construye un datamart en SQLite y expone una API REST con Javalin.

## Justificación de las APIs

- **CoinGecko API**: Elegida por ser gratuita, sin autenticación requerida, con datos actualizados de precios, capitalización y volumen de criptomonedas. Ideal para capturar datos dinámicos que varían constantemente.

- **NewsAPI**: Proporciona titulares de fuentes globales filtradas por categoría tecnológica. Su formato JSON estructurado facilita la deserialización y el almacenamiento. Requiere API key gratuita.

La combinación de ambas fuentes permite analizar si las noticias tecnológicas y financieras tienen impacto directo en los movimientos de precio de las criptomonedas.

## Estructura del Datamart

El datamart en SQLite está diseñado para responder consultas rápidas del usuario final. Contiene tres tablas:

- **crypto_latest**: Precio actual de cada criptomoneda. Usa `UPSERT` (INSERT ON CONFLICT UPDATE) para mantener siempre el último precio sin duplicados. Clave primaria: `coin_id`.

- **crypto_history**: Historial completo de precios con timestamp. Permite análisis temporal y detección de tendencias. Sin límite de registros para preservar el histórico.

- **news**: Noticias capturadas con título, fuente, URL y timestamps. Ordenadas por `captured_at` para mostrar las más recientes primero.

Esta estructura separa los datos actuales (consulta rápida) del histórico (análisis temporal), optimizando el rendimiento de las consultas REST.

## Principios y Patrones de Diseño

- **Separación de responsabilidades**: Cada clase tiene una única función (Feeder trae datos, Serializer traduce JSON, Store persiste, Controller coordina).

- **Programación orientada a interfaces**: Se usan interfaces (`Feeder`, `Serializer`, `DataStore`, `Publisher`, `Subscriber`) con implementaciones concretas, facilitando el desacoplamiento y la testabilidad.

- **Patrón Publisher/Subscriber**: Los feeders publican eventos en ActiveMQ y los consumidores (Event Store Builder, Business Unit) los procesan de forma independiente y asíncrona.

- **Suscripción durable**: Garantiza que los mensajes no se pierdan si un consumidor se desconecta temporalmente.

- **Modelo de dominio separado del JSON externo**: Las clases `CryptoRecord` y `NewsRecord` representan el dominio interno, independientes de la estructura JSON de las APIs externas.

- **Persistencia incremental**: Nunca se borran datos. Cada captura se inserta manteniendo el histórico con timestamps (`captured_at`).

## Tecnologías Utilizadas

- Java 21
- Maven (proyecto multimódulo)
- ActiveMQ 5.19.6 (broker de mensajería)
- SQLite (persistencia)
- OkHttp (cliente HTTP)
- Gson (serialización JSON)
- Javalin (API REST)
- Jackson (serialización para Javalin)

## Cómo Ejecutar

### Requisitos Previos

1. Java 21 instalado
2. Maven instalado
3. ActiveMQ 5.x descargado y descomprimido

### Pasos

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/abdela758/cryptonews-analytics.git
   cd cryptonews-analytics
   ```

2. **Compilar el proyecto:**
   ```bash
   mvn clean install -DskipTests
   ```

3. **Arrancar ActiveMQ:**
   ```bash
   # Windows
   ruta\a\apache-activemq-5.19.6\bin\activemq.bat start

   # Mac/Linux
   ./apache-activemq-5.19.6/bin/activemq start
   ```

4. **Ejecutar los módulos en este orden:**
   - Event Store Builder: ejecutar `event-store-builder/src/.../Main.java`
   - Business Unit: ejecutar `business-unit/src/.../Main.java`
   - Crypto Feeder: ejecutar `crypto-feeder/src/.../Main.java`
   - News Feeder: ejecutar `news-feeder/src/.../Main.java`

### Probar la API REST

Una vez ejecutado el business-unit, la API está disponible en `http://localhost:7000` con los siguientes endpoints:

| Endpoint | Descripción |
|---|---|
| `GET /api/cryptos` | Precios actuales de las 10 principales criptomonedas |
| `GET /api/cryptos/{id}/history` | Historial de precios de una criptomoneda (ej: bitcoin) |
| `GET /api/news` | Últimas 20 noticias tecnológicas capturadas |
| `GET /api/analysis/{coinId}` | Correlación entre noticias y precio de una criptomoneda |

### Ejemplo de Respuesta — `/api/cryptos`

```json
[
  {
    "coin_id": "bitcoin",
    "symbol": "btc",
    "name": "Bitcoin",
    "price_usd": "80918.0",
    "market_cap_usd": "1621111861929.0",
    "volume_24h": "37191812165.0",
    "last_updated": "2026-05-07T11:29:25Z"
  }
]
```

### Ejemplo de Respuesta — `/api/news`

```json
[
  {
    "title": "Chrome's AI features may be hogging 4GB of your computer storage",
    "description": "Chrome users are discovering that Google is installing a 4GB file...",
    "source": "The Verge",
    "url": "https://www.theverge.com/tech/...",
    "published_at": "2026-05-06T10:13:09Z",
    "captured_at": "2026-05-07T12:08:32Z"
  }
]
```

## Estructura del Event Store

Los eventos se almacenan en el sistema de archivos con la siguiente estructura:

```
eventstore/
├── CryptoPrice/
│   └── crypto-feeder/
│       ├── 20260428.events
│       ├── 20260507.events
│       └── ...
└── TechNews/
    └── news-feeder/
        ├── 20260428.events
        ├── 20260507.events
        └── ...
```

Cada archivo `.events` contiene un evento JSON por línea (formato JSON Lines/NDJSON).

## Equipo

- **Team name:** CryptoBros Analytics
- **Abdelaziz:** crypto-feeder, event-store-builder
- **Rayan:** news-feeder, business-unit

## Propuesta de Valor

Combinar el precio en tiempo real de criptomonedas con noticias financieras y tecnológicas para detectar correlaciones entre eventos noticiosos y variaciones de precio, aportando valor analítico al usuario final a través de una API REST consultable.
