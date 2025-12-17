# Learning-Management-System
# Learning Management System (LMS)

## Overview

This project is a **JavaFX-based Learning Management System (LMS)** developed as a course project. The application provides a graphical user interface for managing LMS-related functionality and uses an **H2 database** for data storage. The database schema is created programmatically at runtime, so no external database setup is required.

---

## Technologies Used

* **Java**
* **JavaFX** (GUI)
* **H2 Database** (file-based)
* **JDBC**
* **Apache Ant** (build tool)
* **Eclipse** (development environment)

---

## Project Structure

```
├── src/            # Java source code and resources
├── doc/            # Project documentation / Javadocs
├── build.xml       # Ant build configuration
├── javadoc.xml     # Javadoc generation configuration
├── README.md       # Project overview and instructions
```

---

## Database Details

The application uses an **H2 file-based database** accessed via JDBC.

```java
jdbc:h2:~/FoundationDatabase
```

* The database file is created automatically on first run
* Tables are created programmatically in Java
* No database files are included in the repository
* Sample or persistent data is generated at runtime

This design ensures the project runs cleanly on any machine without requiring manual database setup.

---

## How to Build and Run

### Requirements

* **Java JDK** (Java 11 or higher recommended)
* **Apache Ant**
* JavaFX properly configured (VM options may be required depending on setup)

### Build

```bash
ant build
```

### Run

```bash
ant run
```

If running from an IDE, ensure JavaFX is properly configured in the run configuration.

---

## Notes

* Compiled files and IDE-specific configuration files are intentionally excluded from version control
* The database schema is recreated automatically if it does not exist
* This project is intended for academic use

---

## Author

Brian Cibrian
