# Online Chat

This project is an **online chat application written in Java**.

---

## Requirements

- Java 21
- Maven 3.9+

---

## Build and Run

### 1. Build the project

To build the project into a JAR, run:

`mvn clean package`

The JAR will be generated in the target/ folder, for example:  
`java -jar target/online-chat-1.0-SNAPSHOT.jar`

---

### 2. Set environment variables

Before running the application, set the required environment variables:

| Variable      | Description |
|---------------|-------------|
| JDBC_URL      | URL to your PostgreSQL database |
| DB_USER       | Database username |
| DB_PASSWORD   | Database password |
| CONTEXT       | Type of run: `dev` for development, `prod` for production/fat-jar |
| PORT          | The port on which the application will be run |