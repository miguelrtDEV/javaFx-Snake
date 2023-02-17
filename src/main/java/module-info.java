module es.ces.dint.snake {
    requires javafx.controls;
    requires javafx.fxml;


    opens es.ces.dint.snake to javafx.fxml;
    exports es.ces.dint.snake;
}