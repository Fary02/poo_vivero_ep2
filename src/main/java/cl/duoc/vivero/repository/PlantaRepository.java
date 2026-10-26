package cl.duoc.vivero.repository;

import cl.duoc.vivero.dao.PersistenciaException;
import cl.duoc.vivero.dao.PlantaDao;
import cl.duoc.vivero.model.Planta;
import cl.duoc.vivero.model.PlantaExterior;
import cl.duoc.vivero.model.PlantaInterior;

import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene en memoria la lista de plantas y la persiste mediante los DAO
 * despues de cada cambio, sin que el usuario deba guardar.
 *
 * Hay un DAO (y un archivo) por tipo de planta. Cada cambio reescribe solo
 * el archivo del tipo afectado y, ademas, se aplica primero a una COPIA de
 * la lista: solo si el guardado resulta se modifica la lista.
 * Asi la tabla nunca muestra un cambio que no quedo en el archivo.
 */
public class PlantaRepository implements Repository<Planta> {

    private final PlantaDao<PlantaInterior> daoInteriores;
    private final PlantaDao<PlantaExterior> daoExteriores;
    private final List<Planta> plantas = new ArrayList<>();

    public PlantaRepository(PlantaDao<PlantaInterior> daoInteriores, PlantaDao<PlantaExterior> daoExteriores) {
        this.daoInteriores = daoInteriores;
        this.daoExteriores = daoExteriores;
    }

    @Override
    public void cargar() throws PersistenciaException {
        List<Planta> todas = new ArrayList<>(daoInteriores.cargar());
        todas.addAll(daoExteriores.cargar());
        plantas.clear();
        plantas.addAll(todas);
    }

    @Override
    public List<Planta> listar() {
        return new ArrayList<>(plantas);
    }

    @Override
    public void agregar(Planta planta) throws PersistenciaException {
        List<Planta> copia = new ArrayList<>(plantas);
        copia.add(planta);
        guardarArchivoDe(planta, copia);
        plantas.add(planta);
    }

    @Override
    public void eliminar(Planta planta) throws PersistenciaException {
        List<Planta> copia = new ArrayList<>(plantas);
        copia.remove(planta);
        guardarArchivoDe(planta, copia);
        plantas.remove(planta);
    }

    /** Reescribe solo el archivo que corresponde al tipo de la planta modificada. */
    private void guardarArchivoDe(Planta modificada, List<Planta> copia) throws PersistenciaException {
        if (modificada instanceof PlantaInterior) {
            daoInteriores.guardar(filtrar(copia, PlantaInterior.class));
        } else if (modificada instanceof PlantaExterior) {
            daoExteriores.guardar(filtrar(copia, PlantaExterior.class));
        }
    }

    private static <T extends Planta> List<T> filtrar(List<Planta> lista, Class<T> clase) {
        return lista.stream().filter(clase::isInstance).map(clase::cast).toList();
    }
}
