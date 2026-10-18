package cl.duoc.vivero;

import cl.duoc.vivero.controller.PrincipalController;
import cl.duoc.vivero.dao.GsonPlantaDao;
import cl.duoc.vivero.dao.PersistenciaException;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;
import cl.duoc.vivero.repository.PlantaRepository;
import cl.duoc.vivero.repository.Repository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Punto de entrada de la aplicacion grafica. Ejecuta con: mvn javafx:run
 *
 * Es el unico lugar, junto a los DAO, que conoce la ubicacion de los archivos
 * (interiores.json y exteriores.json, en la carpeta raiz del proyecto).
 */
public class AppFX extends Application {

    private static final Path ARCHIVO_INTERIORES = Path.of("interiores.json");
    private static final Path ARCHIVO_EXTERIORES = Path.of("exteriores.json");

    @Override
    public void start(Stage stage) throws IOException {
        // Se arman las capas: el controlador recibira solo la interfaz Repository.
        Repository<Planta> repositorio = new PlantaRepository(
                new GsonPlantaDao<>(ARCHIVO_INTERIORES, PlantaInterior.class),
                new GsonPlantaDao<>(ARCHIVO_EXTERIORES, PlantaExterior.class));

        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/cl/duoc/vivero/view/principal-view.fxml"));
        Parent raiz = cargador.load();
        PrincipalController principal = cargador.getController();
        principal.inicializar(repositorio);

        stage.setTitle("Vivero Hojas del Sur");
        stage.setScene(new Scene(raiz, 900, 560));
        stage.setMinWidth(720);
        stage.setMinHeight(420);
        stage.show();

        // Si los archivos no existen la lista queda vacia sin error; si estan danados se avisa en la ventana.
        try {
            repositorio.cargar();
        } catch (PersistenciaException e) {
            principal.mostrarMensaje(e.getMessage() + " La aplicación inicia sin plantas.", false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
