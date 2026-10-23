# Guía de resolución · Simulacro EP2 Vivero Hojas del Sur

> Para comparar tu trabajo con la solución: `git diff main solucion`
>
> Versiones: JDK 21, JavaFX 21.0.6, Gson 2.13.1. Formato de la evaluación versión 3.1: una sola ventana, proyecto Maven con `Launcher` y `App`, sin `module-info.java`. El modelo no se modifica: la solución solo agrega código.

## 0. Mapa de requisitos

| Sección del enunciado | Dónde se resuelve | Indicadores (peso) |
|---|---|---|
| 2. Estructura del proyecto | `pom.xml` (Gson), `Launcher`, `App`, paquetes | 2.1.1 (6%), 2.4.1 (8%) |
| 3. Ventana de la aplicación | `main-view.fxml`, `PrincipalController` | 2.2.1 (7%), 2.2.2 (7%), 2.3.1 (8%), 2.3.2 (6%), 2.5.1 a 2.5.3 (12%) |
| 4. Validaciones del formulario | `PrincipalController.onRegistrar()` | 2.6.1 (7%), 2.6.2 (7%), 2.4.2 (7%) |
| 5. Almacenamiento | `GsonPlantaDao`, `PlantaDao`, `Repository`, `PlantaRepository` | 2.7.1 (9%), 2.7.2 (8%) |
| 6. Datos de prueba | La aplicación completa | 2.8.1 (8%) |

Flujo de una acción de principio a fin:

```
Registrar ─► PrincipalController.onRegistrar()
                │  valida los campos (si hay errores: mensaje en lblMensaje y termina)
                │  crea la Planta (el modelo vuelve a validar nombre, altura y cantidad)
                ▼
        PlantaRepository.agregar(planta)
                │  1. copia de la lista + la planta nueva
                │  2. dao.guardar(solo las del tipo afectado)  ──► interiores.json | exteriores.json
                │  3. si el guardado resultó: lista observable.add(...)  ──► la tabla se actualiza sola
                ▼
        mensaje "Menta (Interior) registrada correctamente." + formulario vacío
```

## Paso 1 — Configuración del proyecto (sección 2)

La base trae JavaFX, `Launcher` y `App`. Al ejecutar `Launcher` la aplicación se detiene con *"No se encontró la vista principal"*: es lo esperado, porque falta `main-view.fxml`.

Se agrega Gson en el lugar que indica el comentario del `pom.xml` y se recarga Maven:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.13.1</version>
</dependency>
```

- No hay `module-info.java`: el proyecto corre con *classpath*. No hace falta declarar `requires` ni `opens`.
- `Launcher` existe aparte de `App` para que JavaFX arranque desde IntelliJ sin configuración adicional. No se modifica.
- `App` carga la vista desde `/cl/duoc/vivero/view/main-view.fxml`. El nombre y la carpeta deben coincidir exactamente.

## Paso 2 — Vista en Scene Builder (sección 3)

Se crea `main-view.fxml` en `src/main/resources/cl/duoc/vivero/view/` con:

- Formulario: `cmbTipo`, `txtNombre`, `txtAltura`, `txtCantidad`, el grupo de interior (`boxInterior` con `txtVolumen`), el grupo de exterior (`boxExterior` con `txtEspecie`, `chkHeladas`, `chkDespacho`) y el botón `btnRegistrar`.
- Tabla `tblPlantas` con cinco columnas (`colTipo`, `colNombre`, `colAltura`, `colCantidad`, `colPrecio`) y botón `btnEliminar`.
- Un `Label` `lblMensaje` para los resultados.
- `fx:controller="cl.duoc.vivero.controller.PrincipalController"` en el nodo raíz y `onAction` en los dos botones.

Cada componente que usa el controlador necesita `fx:id`, y el atributo del controlador debe llevar `@FXML` con el **mismo nombre y un tipo compatible**. Los grupos de subtipo van dentro de un `VBox` para poder deshabilitarlos de una vez.

## Paso 3 — DAO con Gson (sección 5)

Una clase genérica `GsonPlantaDao<T>`, con un objeto por tipo de planta.

```java
this.tipoLista = TypeToken.getParameterized(List.class, clase).getType();
List<T> plantas = gson.fromJson(lector, tipoLista);
if (plantas == null) return new ArrayList<>();     // archivo vacío
```

- Gson crea cada objeto con el **constructor sin parámetros** del modelo y asigna los atributos directamente, sin usar los *setters*. Por eso `listaParaDespacho`, que no tiene setter, igual se guarda y se recupera.
- Dos objetos, dos archivos: `new GsonPlantaDao<>(ARCHIVO_INTERIORES, PlantaInterior.class)` y el equivalente para exteriores. Como cada archivo contiene una sola clase, Gson no necesita información de subtipos.
- Archivo que no existe: lista vacía, sin error. Archivo vacío: `fromJson` retorna `null`, también lista vacía.
- `JsonParseException` es una `RuntimeException`: hay que capturarla para traducirla a `PersistenciaException` y que el controlador pueda mostrar el mensaje.

## Paso 4 — Repository (sección 5)

`PlantaRepository` recibe los dos DAO y mantiene una `ObservableList<Planta>` con todas las plantas.

- `cargar()` junta los resultados de ambos DAO y los carga con `setAll`.
- `agregar` y `eliminar` trabajan **primero sobre una copia**, guardan y solo después modifican la lista observable. Si el guardado falla, la tabla queda como estaba y no se informa éxito.
- Se reescribe **solo el archivo del tipo afectado** (`instanceof`).
- El controlador usa la interfaz `Repository<Planta>`; no conoce Gson ni los archivos.

## Paso 5 — Controlador: inicialización y tabla (secciones 3 y 5)

`initialize()` se ejecuta una vez, cuando FXML ya inyectó los atributos:

```java
cmbTipo.getItems().setAll(INTERIOR, EXTERIOR);
boxInterior.disableProperty().bind(cmbTipo.valueProperty().isNotEqualTo(INTERIOR));
boxExterior.disableProperty().bind(cmbTipo.valueProperty().isNotEqualTo(EXTERIOR));
...
tblPlantas.setItems(repositorio.listar());
repositorio.cargar();
```

- Los grupos se **deshabilitan** según el tipo elegido; sin tipo, ambos quedan deshabilitados.
- La tabla se enlaza **una vez** a la lista observable del repositorio. Después de cada registro o eliminación la tabla se actualiza sola: no se vuelve a asignar ni a recargar.
- `colTipo` y `colPrecio` no son atributos: se calculan con `setCellValueFactory(celda -> ...)` usando `getTipo()` y `calcularPrecio()` del modelo. El controlador **no recalcula** el precio.
- El precio se guarda como número y solo se formatea al dibujar la celda: si la columna fuera de texto (`"$10.000"`), `$8.000` quedaría después de `$10.000` al ordenar.
- Si `cargar()` falla (archivo dañado), el mensaje aparece en la ventana y la aplicación sigue funcionando con la tabla vacía.

## Paso 6 — Registrar, validar y mensajes (secciones 3 y 4)

`onRegistrar()` sigue este orden:

1. Quita las marcas de error anteriores.
2. Revisa cada campo y **acumula** los errores en una lista, con el formato *"Campo: motivo"*. Solo se revisan los campos del tipo elegido; los del otro tipo están deshabilitados y no se consideran.
3. Si hay errores: `mostrarMensaje(String.join("\n", errores), false)` y `return`. No se registra nada ni se borra ningún campo, así que los valores se mantienen.
4. Si no hay errores: crea la planta. En una planta de exterior, si *Lista para despacho* está marcada se llama a `marcarListaParaDespacho()`, porque la interfaz no tiene setter.
5. `repositorio.agregar(planta)`; si funciona, mensaje de confirmación y `limpiarFormulario()`.

El área de mensajes es un `Label`. Cada `setText` reemplaza el mensaje anterior; el estilo (`resultado-ok` o `resultado-error`) se cambia con `getStyleClass()`. No se usa `Alert` ni `System.out`.

Si el guardado falla (`PersistenciaException`), el mensaje de error reemplaza al de éxito, la tabla no cambia y el formulario conserva los valores para reintentar.

## Paso 7 — Eliminar (sección 3)

`onEliminar()` lee la fila seleccionada con `getSelectionModel().getSelectedItem()`. Si es `null`, avisa en el área de mensajes y no cambia nada. Si hay selección, `repositorio.eliminar(seleccionada)` quita **solo esa planta** (no todas las que tengan el mismo nombre) y los archivos se actualizan solos.

## Verificación con los datos de prueba

| Prueba | Resultado esperado |
|---|---|
| Iniciar sin `interiores.json` ni `exteriores.json` | Tabla vacía, sin mensaje. |
| Cambiar el tipo en el selector | Solo el grupo del tipo elegido queda habilitado. |
| **Eliminar** sin selección | *"Selecciona una planta de la tabla para eliminarla."* en la misma ventana. |
| Altura `abc` y cantidad `0` | Dos líneas de error en el área de mensajes, valores conservados, nada registrado. |
| Volumen `-1` | Error de volumen; `0,8` (con coma) se acepta. |
| Registrar las 4 plantas | Precios `$8.000`, `$10.000`, `$6.600`, `$5.500`; mensaje *"Rosal (Exterior) registrada correctamente."* y formulario vacío. |
| Eliminar Rosal, cerrar y abrir | Quedan 3; `exteriores.json` contiene solo la Menta, con `"listaParaDespacho": true`. |
| `interiores.json` con texto inválido | Mensaje en la ventana; tabla vacía; la aplicación sigue funcionando. |
| `interiores.json` vacío | Tabla vacía y sin mensaje. |
| Error al escribir (por ejemplo, un archivo de solo lectura) | Mensaje de error, **no** se informa éxito y la tabla no cambia. |

## Errores frecuentes

| Síntoma | Causa |
|---|---|
| La aplicación se detiene con *"No se encontró la vista principal"* | `main-view.fxml` no existe, está en otra carpeta o tiene otro nombre. |
| `package com.google.gson does not exist` | Falta la dependencia de Gson en el `pom.xml` o no se recargó Maven. |
| `NullPointerException` al iniciar, en `initialize()` | Un `fx:id` del FXML no coincide con el atributo del controlador (`cmbTipo` y `cmbtipo` son distintos). |
| `LoadException` / el botón no responde | Falta `fx:controller` en el FXML o `onAction="#metodo"` no coincide con un método del controlador. |
| La tabla muestra las filas pero las columnas quedan vacías | El nombre de `PropertyValueFactory` no coincide con un getter (`"alturaCm"` busca `getAlturaCm()`). |
| `NullPointerException` al iniciar con un archivo vacío | No se consideró el `null` que retorna `fromJson`. |
| La planta exterior no queda lista para despacho | Se olvidó llamar a `marcarListaParaDespacho()` cuando la casilla está marcada. |
| El volumen `6,5` se rechaza | `Double.parseDouble` no acepta la coma: hay que reemplazarla por punto antes de convertir. |
| La tabla muestra una planta que no está en el archivo | Se modificó la lista observable antes de guardar. |
| Se valida el volumen de una planta de exterior | Se leyeron todos los campos sin considerar el tipo elegido. |
| El formulario no queda vacío tras registrar | Falta limpiar los campos, el selector y las casillas después del éxito. |
| Un mensaje de éxito sigue visible tras un error | Se usó `Alert` o se concatenó texto: cada `setText` debe reemplazar el anterior. |
