# Serial Position Effect — Memory Test

An interactive demonstration of the serial position effect: 10 words are
shown one at a time, the user recalls as many as they can, and results are
scored, stored, and aggregated across attempts to reveal the classic
U-shaped recall curve.

## Tech stack

- **Frontend:** HTML5, CSS3, vanilla JavaScript, Chart.js
- **Backend:** Java 17+, Spring Boot 3, REST API, Bean Validation
- **Database:** H2 (file-based), via Spring Data JPA — swappable for MySQL
  later by changing `application.properties` and the driver dependency,
  with no changes to any Java code

## Project structure

```
serial-position-effect/
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── backend/
│   └── springboot/
│       ├── pom.xml
│       └── src/main/
│           ├── java/com/example/serialposition/
│           │   ├── SerialPositionApplication.java
│           │   ├── controller/RecallController.java
│           │   ├── service/RecallService.java
│           │   ├── service/AggregationService.java
│           │   ├── service/SemanticAssociationService.java
│           │   ├── repository/TestAttemptRepository.java
│           │   ├── repository/PositionResultRepository.java
│           │   ├── model/TestAttempt.java
│           │   ├── model/PositionResult.java
│           │   ├── dto/ (request/response shapes)
│           │   ├── config/CorsConfig.java
│           │   └── exception/GlobalExceptionHandler.java
│           └── resources/application.properties
└── README.md
```

## Prerequisites

- Java 17 or newer (`java -version`)
- Maven (`mvn -version`)
- A modern browser
- VS Code's **Live Server** extension (or any static file server) to serve
  the frontend over `http://` — opening `index.html` directly as a
  `file://` URL will be blocked by the backend's CORS policy

## Running the backend

```bash
cd backend/springboot
mvn spring-boot:run
```

Wait for `Tomcat started on port(s): 8080` in the log. Leave this terminal
running for as long as you're testing.

- REST API base: `http://localhost:8080/api`
- H2 web console: `http://localhost:8080/h2-console`
  (JDBC URL: `jdbc:h2:file:./data/serialpositiondb`, user `sa`, no password)

## Running the frontend

In VS Code, right-click `frontend/index.html` → **Open with Live Server**.
It should open at an address like `http://127.0.0.1:5500`.

## API reference

### `POST /api/recall`

Submit one attempt for scoring and storage.

```json
{
  "originalWords": ["Apple", "Table", "River", "Phone", "Cloud", "House", "Green", "Tiger", "Book", "Star"],
  "recalledWords": ["apple", "river", "tiger", "book"],
  "relatedWords": ["fruit"]
}
```

`relatedWords` is optional — it's for words the user says came to mind but
weren't part of the presented list, used by the semantic association
check below.

Returns totals, primacy/middle/recency percentages, a per-position
breakdown, any detected semantic associations, and notes about duplicate
or unrecognized recalled words.

### `GET /api/recall/aggregate`

Returns recall percentage per position, averaged across every attempt
stored so far — this is what the U-curve graph plots.

## Features by phase

1. **Frontend UI** — word presentation (1 word/sec, no pause/skip),
   recall input, results display
2. **Backend scoring** — position matching, primacy/middle/recency
   calculation
3. **Frontend ↔ backend** — real `fetch()` calls replacing local scoring
4. **Persistence** — H2 database via JPA, one row per attempt and per
   word-position
5. **Aggregation** — recall percentage per position across all attempts
6. **U-curve graph** — Chart.js line chart of the aggregated data
7. **Semantic proximity (experimental)** — flags missed words where the
   user produced something loosely associated instead (e.g. missed
   "star" but wrote "space"), using a small hardcoded word list built for
   this app's specific 10 words. This is a research/demo feature, not a
   validated test of Spreading Activation Theory.
8. **Polish** — duplicate/unrecognized recalled words are now surfaced as
   gentle notes rather than silently ignored

## Known limitations

- The word list is fixed at 10 hardcoded words (Apple, Table, River,
  Phone, Cloud, House, Green, Tiger, Book, Star) rather than randomized
  or configurable per attempt.
- The semantic association dataset only covers this app's 10 words — it
  is not a general-purpose semantic network and makes no claim to
  psychological validity.
- H2 runs in file mode for simplicity; a production deployment would use
  a real database (e.g. MySQL) and a proper migration tool instead of
  Hibernate's `ddl-auto=update`.

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| Frontend shows "Couldn't reach the backend" | Backend isn't running, or the frontend was opened as `file://` instead of via Live Server |
| `mvn spring-boot:run` fails with "No plugin found for prefix 'spring-boot'" | You're running Maven from the wrong folder — `cd` into the folder that directly contains `pom.xml` |
| `Cannot create resource output directory` | Windows permissions issue on the project's folder/drive — move the project to a simpler path like `C:\dev\springboot` |
| H2 console won't connect | Double-check the JDBC URL is exactly `jdbc:h2:file:./data/serialpositiondb` and the backend is running |
