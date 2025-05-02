module org.example.pi__dev_ {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires com.google.protobuf;


    opens org.example.pi__dev_ to javafx.fxml;
    exports org.example.pi__dev_;
}