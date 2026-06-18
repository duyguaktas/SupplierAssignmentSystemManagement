# Supplier Assignment System

[cite_start]A distributed client-server application designed to manage supplier data and automate monthly scheduling assignments based on strict business logic constraints[cite: 2, 83, 84].

## Architecture Overview
* [cite_start]**Android Client (UI):** A mobile application built in Java that manages a local SQLite database (`suppliers.db`) utilizing Room[cite: 2, 15, 17]. [cite_start]It includes interfaces to add, edit, and remove suppliers, track custom unavailability ranges, and view finalized schedules[cite: 26, 35, 49, 53].
* [cite_start]**Console Server (Backend):** A stateless Java application that handles high-performance scheduling computation[cite: 2, 83]. [cite_start]It dynamically maps "Contract" and "Stock" suppliers to calendar days based on rule sets like consecutive-day restrictions[cite: 7, 90, 91, 92].
* [cite_start]**Network Protocol:** Low-level TCP Sockets utilizing structured JSON serialization for reliable client-server handshakes[cite: 5, 62].
