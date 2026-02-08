# Aether-Flow

Aether-Flow is a modular LLM orchestration platform built in Java.

This repository contains the initial multi-module Maven setup and
base infrastructure for building an internal AI control plane.

---

## Modules

- orchestrator-api   : Public contracts and DTOs
- orchestrator-core  : Core orchestration logic
- orchestrator-app   : Spring Boot runtime
- adapters           : LLM provider integrations

---

## Requirements

- Java 21+
- Maven 3.9+

---

## Build

From project root:

```bash
mvn clean install

