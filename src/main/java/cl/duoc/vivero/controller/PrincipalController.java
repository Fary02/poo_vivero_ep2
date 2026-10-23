package cl.duoc.vivero.controller;

import cl.duoc.vivero.dao.GsonPlantaDao;
import cl.duoc.vivero.dao.PersistenciaException;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;
import cl.duoc.vivero.repository.PlantaRepository;
import cl.duoc.vivero.repository.Repository;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la unica ventana: formulario de registro, tabla de plantas,
 * boton Eliminar y area de mensajes.
 *
 * Ningun mensaje se muestra por consola ni en un Alert: cada resultado
 * (exito, error de validacion, aviso) reemplaza al anterior en lblMensaje.
 * El controlador valida el formulario y coordina la vista; los calculos
 * pertenecen al modelo y el acceso a los archivos, al Repository y al DAO.
 */
public class PrincipalController {

    private static final String INTERIOR = "Interior";
    private static final String EXTERIOR = "Exterior";
    private static final String CLASE_ERROR = "campo-error";

    /** Archivos JSON en la carpeta raiz del proyecto (un archivo por tipo de planta). */
    private static final Path ARCHIVO_INTERIORES = Path.of("interiores.json");
    private static final Path ARCHIVO_EXTERIORES = Path.of("exteriores.json");

    // --- Formulario ---
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtAltura;
    @FXML private TextField txtCantidad;
    @FXML private VBox boxInterior;
    @FXML private TextField txtVolumen;
    @FXML private VBox boxExterior;
    @FXML private TextField txtEspecie;
    @FXML private CheckBox chkHeladas;
    @FXML private CheckBox chkDespacho;

    // --- Tabla ---
    @FXML private TableView<Planta> tblPlantas;
    @FXML private TableColumn<Planta, String> colTipo;
    @FXML private TableColumn<Planta, String> colNombre;
    @FXML private TableColumn<Planta, Integer> colAltura;
    @FXML private TableColumn<Planta, Integer> colCantidad;
    @FXML private TableColumn<Planta, Integer> colPrecio;

    // --- Mensajes ---
    @FXML private Label lblMensaje;

    private Repository<Planta> repositorio;

    /** Lo llama FXMLLoader al terminar de inyectar los @FXML. */
    @FXML
    private void initialize() {
        // Selector de tipo y campos propios de cada subtipo: solo se habilitan los del tipo elegido.
        cmbTipo.getItems().setAll(INTERIOR, EXTERIOR);
        boxInterior.disableProperty().bind(cmbTipo.valueProperty().isNotEqualTo(INTERIOR));
        boxExterior.disableProperty().bind(cmbTipo.valueProperty().isNotEqualTo(EXTERIOR));

        configurarColumnas();

        // Capas: el controlador solo conoce la interfaz Repository.
        repositorio = new PlantaRepository(
                new GsonPlantaDao<>(ARCHIVO_INTERIORES, PlantaInterior.class),
                new GsonPlantaDao<>(ARCHIVO_EXTERIORES, PlantaExterior.class));
        // La tabla usa la lista observable del repositorio: refleja cada cambio sin recargar.
        tblPlantas.setItems(repositorio.listar());

        // Carga inicial: si los archivos no existen la tabla queda vacia sin error;
        // si estan danados se avisa en la ventana y la aplicacion sigue funcionando.
        try {
            repositorio.cargar();
            lblMensaje.setText("");
        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage() + " La aplicación inicia sin plantas.", false);
        }
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAltura.setCellValueFactory(new PropertyValueFactory<>("alturaCm"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        // Valores que no son atributos del modelo: se obtienen con un lambda.
        colTipo.setCellValueFactory(celda -> new ReadOnlyStringWrapper(celda.getValue().getTipo()));
        // El precio lo calcula el modelo. Se guarda como numero (para ordenar bien) y solo se formatea al dibujar.
        colPrecio.setCellValueFactory(celda -> new ReadOnlyObjectWrapper<>(celda.getValue().calcularPrecio()));
        colPrecio.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Integer precio, boolean vacia) {
                super.updateItem(precio, vacia);
                setText(vacia || precio == null ? null : Planta.pesos(precio));
            }
        });
    }

    @FXML
    private void onRegistrar() {
        limpiarMarcas();
        List<String> errores = new ArrayList<>();

        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            marcar(cmbTipo, "Tipo: selecciona Interior o Exterior.", errores);
        }
        String nombre = leerTexto(txtNombre, "Nombre", errores);
        Integer altura = leerEntero(txtAltura, "Altura", Planta.ALTURA_MINIMA, Planta.ALTURA_MAXIMA, errores);
        Integer cantidad = leerEntero(txtCantidad, "Cantidad", Planta.CANTIDAD_MINIMA, Planta.CANTIDAD_MAXIMA, errores);
        // Solo se validan los campos del tipo elegido; los del otro tipo estan deshabilitados.
        Double volumen = INTERIOR.equals(tipo) ? leerDecimal(txtVolumen, "Volumen de la maceta", errores) : null;
        String especie = EXTERIOR.equals(tipo) ? leerTexto(txtEspecie, "Especie", errores) : null;

        if (!errores.isEmpty()) {
            // No se registra ni se borra ningun campo: el usuario corrige y reintenta.
            mostrarMensaje(String.join("\n", errores), false);
            return;
        }

        try {
            Planta planta;
            if (INTERIOR.equals(tipo)) {
                planta = new PlantaInterior(nombre, altura, cantidad, volumen);
            } else {
                PlantaExterior exterior = new PlantaExterior(nombre, altura, cantidad, especie, chkHeladas.isSelected());
                if (chkDespacho.isSelected()) {
                    exterior.marcarListaParaDespacho();   // el estado de la interfaz se cambia con su metodo, no con un setter
                }
                planta = exterior;
            }
            repositorio.agregar(planta);
            mostrarMensaje(planta.getNombre() + " (" + planta.getTipo() + ") registrada correctamente.", true);
            limpiarFormulario();   // el formulario queda listo para un nuevo registro
        } catch (IllegalArgumentException e) {
            // Red de seguridad: el modelo tiene la ultima palabra sobre las reglas comunes.
            mostrarMensaje(e.getMessage(), false);
        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage(), false);   // no se guardo: la tabla no cambio ni se borra el formulario
        }
    }

    @FXML
    private void onEliminar() {
        Planta seleccionada = tblPlantas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Selecciona una planta de la tabla para eliminarla.", false);
            return;
        }
        try {
            repositorio.eliminar(seleccionada);
            mostrarMensaje(seleccionada.getNombre() + " (" + seleccionada.getTipo() + ") eliminada.", true);
        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage(), false);
        }
    }

    /**
     * Muestra un mensaje en la ventana; reemplaza al anterior.
     *
     * @param correcto true para el estilo de exito, false para el de error o aviso
     */
    private void mostrarMensaje(String texto, boolean correcto) {
        lblMensaje.setText(texto);
        lblMensaje.getStyleClass().removeAll("resultado-ok", "resultado-error");
        lblMensaje.getStyleClass().add(correcto ? "resultado-ok" : "resultado-error");
    }

    // ------------------------------------------------------------------
    // Lectura y validacion de los campos
    // ------------------------------------------------------------------

    private String leerTexto(TextField campo, String nombreCampo, List<String> errores) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        if (texto.isEmpty()) {
            marcar(campo, nombreCampo + ": no puede estar vacío.", errores);
            return null;
        }
        return texto;
    }

    private Integer leerEntero(TextField campo, String nombreCampo, int minimo, int maximo, List<String> errores) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        String regla = nombreCampo + ": debe ser un número entero entre " + minimo + " y " + maximo + ".";
        if (texto.isEmpty()) {
            marcar(campo, nombreCampo + ": no puede estar vacío.", errores);
            return null;
        }
        try {
            int valor = Integer.parseInt(texto);
            if (valor < minimo || valor > maximo) {
                marcar(campo, regla, errores);
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            marcar(campo, regla, errores);
            return null;
        }
    }

    /** Acepta punto o coma como separador decimal (6.5 o 6,5). */
    private Double leerDecimal(TextField campo, String nombreCampo, List<String> errores) {
        String texto = campo.getText() == null ? "" : campo.getText().trim().replace(',', '.');
        String regla = nombreCampo + ": debe ser un número mayor que cero (acepta decimales).";
        if (texto.isEmpty()) {
            marcar(campo, nombreCampo + ": no puede estar vacío.", errores);
            return null;
        }
        try {
            double valor = Double.parseDouble(texto);
            if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
                marcar(campo, regla, errores);
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            marcar(campo, regla, errores);
            return null;
        }
    }

    private void marcar(Control campo, String mensaje, List<String> errores) {
        errores.add(mensaje);
        campo.getStyleClass().add(CLASE_ERROR);
    }

    private void limpiarMarcas() {
        List.of(cmbTipo, txtNombre, txtAltura, txtCantidad, txtVolumen, txtEspecie)
                .forEach(control -> control.getStyleClass().remove(CLASE_ERROR));
    }

    /** Deja el formulario vacio: sin tipo elegido, campos en blanco y casillas sin marcar. */
    private void limpiarFormulario() {
        cmbTipo.setValue(null);
        txtNombre.clear();
        txtAltura.clear();
        txtCantidad.clear();
        txtVolumen.clear();
        txtEspecie.clear();
        chkHeladas.setSelected(false);
        chkDespacho.setSelected(false);
    }
}
