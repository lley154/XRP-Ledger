# JavaFX Application Template

This is a JavaFX application template using Maven, JUnit, and SceneBuilder.

## Prerequisites

- Java 23
- Maven
- SceneBuilder (for UI design)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── MainApp.java
│   │           └── MainController.java
│   └── resources/
│       └── fxml/
│           └── MainView.fxml
└── test/
    └── java/
        └── com/
            └── example/
                └── MainControllerTest.java
```

## Building and Running

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn javafx:run
```

## Using SceneBuilder

1. Open SceneBuilder
2. Open the file `src/main/resources/fxml/MainView.fxml`
3. Make your UI changes
4. Save the file

## Running Tests

```bash
mvn test
```

## Notes

- The project uses JavaFX 21.0.2
- JUnit 5.10.1 is used for testing
- The main application class is `com.example.MainApp`
- The main FXML file is located at `src/main/resources/fxml/MainView.fxml` 