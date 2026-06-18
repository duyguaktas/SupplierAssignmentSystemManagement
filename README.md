# Supplier Assignment System

A distributed client-server application designed to manage supplier data and automate monthly scheduling assignments based on strict business logic constraints

## Architecture Overview
* **Android Client (UI):** A mobile application built in Java that manages a local SQLite database (`suppliers.db`) utilizing Room. It includes interfaces to add, edit, and remove suppliers, track custom unavailability ranges, and view finalized schedules.
* **Console Server (Backend):** A stateless Java application that handles high-performance scheduling computation. It dynamically maps "Contract" and "Stock" suppliers to calendar days based on rule sets like consecutive-day restrictions.
* **Network Protocol:** Low-level TCP Sockets utilizing structured JSON serialization for reliable client-server handshakes.
