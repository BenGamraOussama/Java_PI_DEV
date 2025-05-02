module com.exemple {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.exemple to javafx.fxml;
    opens com.exemple.controllers to javafx.fxml;
    opens com.exemple.entities to javafx.fxml;
    opens com.exemple.services to javafx.fxml;
    opens com.exemple.utils to javafx.fxml;
    
    exports com.exemple;
    exports com.exemple.controllers;
    exports com.exemple.entities;
    exports com.exemple.services;
    exports com.exemple.utils;
} 