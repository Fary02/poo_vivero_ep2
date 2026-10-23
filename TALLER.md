# Taller del caso Vivero · Plan de sesiones y gestión del tiempo (EP2 versión 3.1)

Este documento es del docente y vive en la rama `solucion`. La rama `main` contiene solo el proyecto base y el enunciado, en condiciones de la prueba.

## 1. Formato del taller

- **Caso tipo:** Vivero Hojas del Sur. Tiene la misma estructura que las 9 Formas de la evaluación (ver sección 4).
- **Resolución genérica:** 120 minutos si se arma de corrido, en 7 bloques.
- **Revisión con el curso:** 2 sesiones de 80 minutos. La sesión 1 construye la base (configuración, vista, DAO, Repository, tabla); la sesión 2 construye el comportamiento (registro, validación, eliminación, almacenamiento) y termina transfiriendo lo aprendido a otra Forma.
- Se trabaja en el orden que conviene hacer en la prueba: cada bloque termina con un proyecto que **compila y se ejecuta**.

## 2. Resolución genérica en 120 minutos

| Bloque | Min | Qué se construye | Indicadores (peso) |
|---|---|---|---|
| 1. Configuración | 10 | Gson en el `pom.xml`, ejecutar `Launcher`, ver el aviso de vista inexistente | 2.1.1 (6%) |
| 2. Vista | 20 | `main-view.fxml` en Scene Builder con formulario, tabla, Eliminar y `Label` de mensajes; `fx:controller` y `fx:id` | 2.2.1 y 2.2.2 (14%) |
| 3. Persistencia | 30 | `PlantaDao`, `GsonPlantaDao`, `PersistenciaException`, `Repository` y `PlantaRepository` | 2.7.1 (9%), 2.4.1 (8%) |
| 4. Controlador base | 15 | `initialize()`, columnas, tabla enlazada a la lista del repositorio, campos por tipo, carga inicial | 2.3.1 (8%), 2.5.1 a 2.5.3 (12%) |
| 5. Registrar | 25 | Validación, mensajes, creación de la planta, formulario vacío al terminar | 2.6.1 y 2.6.2 (14%), 2.3.2 (6%), 2.4.2 (7%) |
| 6. Eliminar y archivos | 10 | Eliminar con y sin selección, guardado automático, errores de lectura y de escritura | 2.7.2 (8%) |
| 7. Verificación | 10 | Datos de prueba, cerrar y abrir, archivo dañado | 2.8.1 (8%) |

## 3. Dos sesiones de 80 minutos

### Sesión 1 · Base del proyecto

| Min | Actividad | Propósito |
|---|---|---|
| 5 | Mapa del caso: enunciado, rúbrica y proyecto base | Saber qué se evalúa antes de escribir código. |
| 10 | Configuración: Gson, `Launcher`, `App`, aviso de vista inexistente | Entender el arranque sin `module-info`. |
| 20 | Vista en Scene Builder y vinculación con el controlador | Aprender a nombrar los `fx:id` desde el principio. |
| 15 | DAO con Gson | Archivo ausente, vacío y dañado. |
| 15 | Repository con lista observable y guardado sobre copia | La tabla nunca muestra algo que no quedó en el archivo. |
| 10 | Controlador base: `initialize()`, columnas, tabla, carga inicial | Primer resultado visible: la aplicación abre y muestra los datos del archivo. |
| 5 | Punto de control: compila, abre y carga | Cierre y tarea (practicar con una Forma). |

### Sesión 2 · Comportamiento y transferencia

| Min | Actividad | Propósito |
|---|---|---|
| 5 | Repaso: estado del proyecto al terminar la sesión 1 | Todos parten del mismo punto. |
| 25 | Registrar: validación, mensajes en el `Label`, formulario vacío | El bloque de mayor peso en la prueba. |
| 10 | Eliminar con y sin selección | Qué se elimina: solo la fila seleccionada. |
| 10 | Guardado automático y errores de archivo | Error de escritura: no se informa éxito. |
| 10 | Verificación con los datos de prueba y reinicio | Cómo comprobar que cumple el enunciado. |
| 20 | Transferencia: se entrega una Forma distinta y se lista qué cambia | Reconocer que cambian los nombres y las reglas, no la estructura. |

## 4. Equivalencia entre las 9 Formas y el Vivero

En todas hay una clase base abstracta con 3 atributos comunes (el primero, un texto), un subtipo con 3 atributos propios (uno de ellos de una interfaz, sin setter) y un subtipo con 1 atributo propio. El nombre de la operación de cálculo cambia.

| Forma | Base (común) | Subtipo de 3 atributos (interfaz) | Subtipo de 1 atributo | Cálculo |
|---|---|---|---|---|
| Vivero (taller) | `Planta` (nombre, alturaCm, cantidad) | `PlantaExterior` (especie, resistenteHeladas, listaParaDespacho; `Despachable`) | `PlantaInterior` (volumenMacetaLitros) | `calcularPrecio` |
| A · PataVerde | `Mascota` (nombre, edad, peso) | `Perro` (raza, vacunado, disponibleAdopcion; `Adoptable`) | `Gato` (exterior) | `calcularCostoConsulta` |
| B · Capacitación | `Curso` (codigo, duracionHoras, cupoMaximo) | `CursoCertificado` (entidadCertificadora, evaluacionAlDia, diplomaEmitido; `ConDiploma`) | `CursoLibre` (inscritos) | `calcularCosto` |
| C · LibroVivo | `Material` (titulo, anioPublicacion, copiasDisponibles) | `Libro` (autor, buenEstado, reservado; `Reservable`) | `AudioLibro` (duracionMinutos) | `calcularCostoPrestamo` |
| D · Taller de bicicletas | `Bicicleta` (codigo, anioFabricacion, peso) | `BicicletaElectrica` (autonomiaKm, bateriaCertificada, garantiaExtendida; `ConGarantiaExtendida`) | `BicicletaMontanya` (suspensiones) | `calcularCostoMantencion` |
| E · AutoFix | `Vehiculo` (marca, anioFabricacion, kilometraje) | `Auto` (modelo, garantiaVigente, garantiaActiva; `Garantizable`) | `Furgon` (capacidadToneladas) | `calcularCostoServicio` |
| F · Viviendas | `Vivienda` (codigo, superficie, habitaciones) | `Departamento` (piso, gastoComunAlDia, estacionamientoAsignado; `ConEstacionamiento`) | `Casa` (tienePatio) | `calcularCostoArriendo` |
| G · SonrisaSana | `Tratamiento` (descripcion, sesiones, duracionMinutos) | `Ortodoncia` (tipoBrackets, planDentalPrevio, financiamientoActivo; `Financiable`) | `Implante` (coronaPorcelana) | `calcularCosto` |
| H · Arriendo de maquinaria | `Maquina` (codigo, horasUso, potencia) | `Excavadora` (pesoToneladas, mantencionAlDia, certificacionSeguridad; `Certificable`) | `CargadorFrontal` (capacidadBalde) | `calcularCostoServicio` |
| I · FitPower | `Clase` (nombre, cupoMaximo, duracionMinutos) | `ClasePersonal` (instructor, evaluacionPrevia, cancelacionActiva; `Cancelable`) | `ClaseSemipersonal` (participantes) | `calcularCosto` |

Lo que cambia entre Formas: nombres de clases y campos, rangos de validación, el tipo del campo del subtipo de 1 atributo (booleano, entero o decimal), los nombres de los archivos JSON y la cantidad de reglas propias de cada subtipo (1 en A, F y G; 2 en el resto, según los enunciados).

## 5. Cómo explicar el caso

Una idea por bloque, para decir en voz alta antes de escribir código:

1. **Configuración.** El proyecto base ya sabe abrir la ventana; solo falta lo que el enunciado pide agregar: Gson. Si no compila, ningún indicador se puede evidenciar.
2. **Vista.** La vista solo declara componentes. Cada componente que el controlador usa necesita un `fx:id`, y ese nombre se repite exactamente en el controlador.
3. **Persistencia.** El DAO sabe leer y escribir un archivo; no sabe nada de ventanas. El Repository mantiene la lista en memoria y le pide al DAO que guarde.
4. **Controlador base.** La tabla se enlaza una vez a la lista del repositorio; después se actualiza sola. El precio lo calcula el modelo; el controlador solo lo muestra.
5. **Registrar.** Primero validar, después crear, después guardar, y solo si guardó, informar y limpiar. Un error nunca borra lo que el usuario escribió.
6. **Eliminar y archivos.** Se elimina la fila seleccionada, no un nombre. Si no se pudo escribir, no se informa éxito.
7. **Verificación.** Probar como lo haría el docente: datos de prueba, cerrar y abrir, un archivo dañado.

## 6. Gestión del tiempo en la evaluación real

La evaluación dura **3 bloques**. El plan se expresa en **porcentaje del tiempo total**, de modo que sirve igual para quienes tienen más tiempo por adecuación. Las horas de las tablas suponen 3 bloques de 90 minutos (270 minutos); si la duración real es otra, se recalcula multiplicando el porcentaje acumulado por el tiempo total.

### 6.1 Hitos

Cada hito termina con una aplicación que **compila**; un proyecto que no compila vale 0% en todos los indicadores.

| Hito | % del tiempo | Resultado |
|---|---|---|
| 0. Lectura | 5 | Leer el enunciado completo, abrir el proyecto, ejecutar `Launcher` y ver el aviso de vista inexistente. |
| 1. Configuración | 5 | Gson en el `pom.xml`, Maven recargado, sin errores. |
| 2. Vista | 15 | `main-view.fxml` con todos los componentes y `fx:id`; la ventana abre. |
| 3. Persistencia | 15 | DAO y Repository compilando; `cargar()` y `guardar()` funcionan con un archivo. |
| 4. Controlador base | 12 | La tabla muestra los datos del archivo; los grupos de campos se habilitan según el tipo. |
| 5. Registrar | 20 | Validación, mensajes, registro, formulario vacío y archivo actualizado. |
| 6. Eliminar y archivos | 10 | Eliminar con y sin selección; guardado automático; archivo ausente, vacío y dañado. |
| 7. Verificación | 10 | Los 4 datos de prueba, cerrar y abrir, eliminar uno y volver a abrir. |
| 8. Reserva y entrega | 8 | Revisar nombres, comprimir el proyecto y subir el ZIP a AVA con margen. |

### 6.2 Hora límite de cada hito (3 bloques de 90 minutos)

| Hito | Sin adecuación (270 min) | +25% (338 min) | +50% (405 min) |
|---|---|---|---|
| 0. Lectura | 0:14 | 0:17 | 0:20 |
| 1. Configuración | 0:27 | 0:34 | 0:40 |
| 2. Vista | 1:08 | 1:24 | 1:41 |
| 3. Persistencia | 1:48 | 2:15 | 2:42 |
| 4. Controlador base | 2:20 | 2:56 | 3:31 |
| 5. Registrar | 3:14 | 4:03 | 4:52 |
| 6. Eliminar y archivos | 3:41 | 4:37 | 5:32 |
| 7. Verificación | 4:08 | 5:10 | 6:13 |
| 8. Entrega | 4:30 | 5:38 | 6:45 |

Las horas son el tiempo transcurrido desde el inicio, redondeado al minuto. Cada estudiante con adecuación recibe su propia columna impresa antes de la prueba.

### 6.3 Reglas para organizarse

1. **Compilar primero, completar después.** Ejecutar la aplicación al final de cada hito. Si algo deja de compilar, se arregla antes de seguir.
2. **Una hora límite por hito, no por detalle.** Si se pasa de la hora límite, se deja lo que falta con lo mínimo que funcione y se avanza; se vuelve al final si sobra tiempo.
3. **Lo mínimo que funciona en cada hito.**
   - Vista: componentes y `fx:id`; el estilo visual no se evalúa.
   - Persistencia: primero guardar y cargar con el caso feliz; después archivo ausente, vacío y dañado.
   - Registrar: primero que registre con datos válidos; después las reglas una por una, empezando por las comunes.
4. **Usar los datos de prueba como lista de verificación.** El hito 7 se hace con los datos del enunciado, no con otros.
5. **Dejar 8% para la entrega.** Nombre del ZIP con el nombre completo, proyecto completo, subida a AVA antes de que termine la sesión.
6. **Pausas.** Si se toman, planificarlas dentro del tiempo total y en el cambio de hito (por ejemplo, entre los hitos 3 y 4, y entre 5 y 6), para retomar con la aplicación compilando.

### 6.4 Si se va atrasado

| Situación | Decisión |
|---|---|
| Pasada la hora límite del hito 2 y la vista no abre | Dejar solo el formulario, la tabla, el botón y el `Label`; los grupos por tipo se agregan después. |
| Pasada la hora límite del hito 3 | Un DAO genérico y un Repository con `cargar`, `agregar`, `eliminar`; sin copia de la lista. |
| Pasada la hora límite del hito 5 | Validar nombre, números y rangos comunes; los campos del subtipo al final. |
| Faltan 15% del tiempo y no se ha verificado | Saltar a la verificación con los datos de prueba: detecta antes los errores que más puntaje cuestan. |
| El proyecto no compila | Es la única prioridad: revisar el último cambio y deshacerlo si no se encuentra el error. |

### 6.5 Orientaciones para el docente

- Entregar la hoja de tiempos (6.1 y 6.2) con la hora real de inicio escrita, antes de comenzar. Cada estudiante con adecuación recibe la columna que le corresponde.
- Con adecuación de tiempo solo cambian las horas límite: la Forma, los hitos y los porcentajes son los mismos.
- Practicar el plan en el taller: en la sesión 2, los 20 minutos de transferencia se pueden cronometrar con estos hitos.
