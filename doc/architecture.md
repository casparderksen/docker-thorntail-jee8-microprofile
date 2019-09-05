# Code organization

The application is organized conform
[Onion Architecture](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/) principles.
This means that the code is organized into independent functional slices. Each slice is organized into layers.
Dependencies between layers point are from more concrete to abstract. Abstractions may not depend on
implementations. Communication with external systems and infrastructure is encapsulated in adapters.
Adapters must be independent of each other.

Layers and package structure:
- `org.my.app.<slice>`: slice of functionality (not a layer)
  - `domain`: core logic, depends on business domain, framework-free
    - `model`: domain entities (behaviour and encapsulated data)
    - `service`: domain services
  - `application`: use cases (application specific workflow)
  - `adapter`: adapters for external systems or infrastructure
    - `rest`: REST API (thin, no logic other than DTO mapping and building responses)
- `org.my.util.<slice>`: application independent generic logic (could be moved to library)

# Dependency rules

- Slices must be independent
- There must not be cyclic dependencies
- The `domain` package may not depend on other packages
- The `application` package may depend on `domain`, but not on any `adapter`
- An `adapter` package may use `application` and `domain`
- An `adapter` package may not depend on any other `adapter` package
- A `util` package may not depend on `app` packages

Consequences:
- Use application services to send data from an input adapter to an output adapter (workflow).
- Use Dependency Inversion or domain events for invoking adapters from domain or application services.

# Integrity of models

We want domain and JPA entities to be consistent and valid at all times.
Therefore, REST adapters use DTOs as separate input and output models.
These models are mapped from/to domain models. This is largely automated with MapStruct.