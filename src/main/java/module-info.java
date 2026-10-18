/**
 * Modulo de la aplicacion Vivero Hojas del Sur.
 *
 * requires: modulos externos que usa el proyecto.
 * opens:    paquetes que otras bibliotecas leen por reflexion
 *           (FXML inyecta los @FXML de los controladores; Gson lee y escribe
 *           los atributos del modelo; la tabla lee sus getters con
 *           PropertyValueFactory).
 * exports:  paquete de la clase Application, que JavaFX debe poder instanciar.
 */
module cl.duoc.vivero {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens cl.duoc.vivero.controller to javafx.fxml;
    opens cl.duoc.vivero.model to com.google.gson, javafx.base;

    exports cl.duoc.vivero;
}
