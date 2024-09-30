package ar.unrn.tp.jpa.servicio;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import ar.unrn.tp.api.ClienteService;
import ar.unrn.tp.modelo.Cliente;
import ar.unrn.tp.modelo.TarjetaCredito;

@Service
public class ClienteServiceJPA implements ClienteService {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void crearCliente(String nombre, String apellido, String dni, String email) {
        // Validar que el DNI no se repita
        if (em.createQuery("SELECT c FROM Cliente c WHERE c.dni = :dni")
              .setParameter("dni", dni)
              .getResultList()
              .isEmpty()) {
            Cliente cliente = new Cliente(nombre, apellido, dni, email);
            em.persist(cliente);
            em.flush();
        } else {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI");
        }
    }

    @Override
    @Transactional
    public void modificarCliente(Long idCliente, String nombre, String apellido, String dni, String email) {
        Cliente cliente = em.find(Cliente.class, idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con el ID proporcionado");
        }

        // Validar que el nuevo DNI no se repita (si se está cambiando)
        if (!cliente.getDni().equals(dni)) {
            if (!em.createQuery("SELECT c FROM Cliente c WHERE c.dni = :dni AND c.id != :id")
                    .setParameter("dni", dni)
                    .setParameter("id", idCliente)
                    .getResultList()
                    .isEmpty()) {
                throw new IllegalArgumentException("Ya existe otro cliente con ese DNI");
            }
        }

        // Actualizar los datos del cliente
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDni(dni);
        cliente.setEmail(email);

        em.merge(cliente);
    }

    @Override
    @Transactional
    public void agregarTarjeta(Long idCliente, String nro, String marca) {
        Cliente cliente = em.getReference(Cliente.class, idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con el ID proporcionado");
        }

        // Validar que el número de tarjeta no se repita
        if (!em.createQuery("SELECT t FROM TarjetaCredito t WHERE t.numero = :nro")
                .setParameter("nro", nro)
                .getResultList()
                .isEmpty()) {
            throw new IllegalArgumentException("Ya existe una tarjeta con ese número");
        }

        TarjetaCredito tarjeta = new TarjetaCredito(nro, marca, LocalDate.now());
        cliente.agregarTarjeta(tarjeta);
        System.out.println("Se agrego la tarjeta con exito" + cliente.getTarjetas().toString());

        //actualiza el cliente
        //em.merge(cliente);
    }

    @Override
    @Transactional
    public List<TarjetaCredito> listarTarjetas(Long idCliente) {
        Cliente cliente = em.find(Cliente.class, idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("No se encontró un cliente con el ID proporcionado");
        }

        return em.createQuery("SELECT t FROM TarjetaCredito t WHERE t.cliente.id = :idCliente", TarjetaCredito.class)
                 .setParameter("idCliente", idCliente)
                 .getResultList();
    }

}
