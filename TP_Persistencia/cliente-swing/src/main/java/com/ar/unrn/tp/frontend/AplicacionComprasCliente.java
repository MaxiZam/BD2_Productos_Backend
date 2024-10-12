package com.ar.unrn.tp.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ar.unrn.tp.frontend.dto.*;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class AplicacionComprasCliente extends JFrame {
    private JTextField campoEmail;
    private JPanel panelLogin;
    private JPanel panelCompra;
    private JList<String> listaProductos;
    private JList<String> listaTarjetas;
    private JTextArea areaDescuentos;
    private JButton botonCalcularTotal;
    private JButton botonComprar;
    private JButton botonConcretarVenta;
    private JLabel etiquetaTotal;
    private JTextField campoUsuario;
    private JTextField campoContrasena;

    private List<ProductoDTO> productos;
    private List<TarjetaCreditoDTO> tarjetas;
    private List<DescuentoDTO> descuentos;
    private String token; // Para almacenar el token de autenticación

    private Long idUsuarioActivo; // Variable para almacenar el ID del usuario activo

    public AplicacionComprasCliente() {
        setTitle("Aplicación de Compras para Cliente");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializarDatos();
        crearPanelLogin();
        crearPanelCompra();

        setContentPane(panelLogin);
        setVisible(true);
    }

    private void inicializarDatos() {
        try {
            productos = obtenerProductosDelBackend();  // Llama al backend en lugar de tener datos hardcodeados.
            // Inicializa las tarjetas y descuentos como listas vacías por ahora
            tarjetas = new ArrayList<>();
            descuentos = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void crearPanelLogin() {
        panelLogin = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel etiquetaUsuario = new JLabel("Email:");
        JLabel etiquetaContrasena = new JLabel("DNI:");
        campoUsuario = new JTextField(20);
        campoContrasena = new JPasswordField(20); // Este campo ahora representará el DNI
        JButton botonLogin = new JButton("Iniciar Sesión");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelLogin.add(etiquetaUsuario, gbc);

        gbc.gridx = 1;
        panelLogin.add(campoUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelLogin.add(etiquetaContrasena, gbc);

        gbc.gridx = 1;
        panelLogin.add(campoContrasena, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panelLogin.add(botonLogin, gbc);

        botonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = campoUsuario.getText();
                String dni = campoContrasena.getText(); // Captura el DNI del campo de contraseña
                if (true/*iniciarSesion(email, dni)*/) { // Modifica el método para aceptar el DNI
                    setContentPane(panelCompra);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(panelLogin, "Credenciales incorrectas, intenta nuevamente.");
                }
            }
        });
    }

    private boolean iniciarSesion(String email, String dni) {
        try {
            String apiUrl = "http://localhost:8080/api/clientes/login"; // Cambia esto si tu backend está en otra dirección.

            // Crear el objeto JSON para la solicitud
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", email); // Usar email para la autenticación
            //jsonObject.addProperty("dni", dni); // Usar DNI para la autenticación

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonObject.toString().getBytes());

            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                connection.disconnect();

                // Asumiendo que el backend devuelve un objeto JSON con el id del usuario
                JsonObject responseJson = new Gson().fromJson(content.toString(), JsonObject.class);
                this.idUsuarioActivo = responseJson.get("id").getAsLong(); // Obtener el ID del usuario
                this.token = responseJson.get("token").getAsString(); // Obtener el token de autenticación (si se devuelve)

                return true; // Inicio de sesión exitoso
            } else {
                System.out.println("Error en la conexión: " + connection.getResponseCode());
                return false; // Error en el inicio de sesión
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Error al iniciar sesión
        }
    }


    // En el método 'crearPanelCompra', añade un botón para modificar productos
    private void crearPanelCompra() {
        panelCompra = new JPanel(new BorderLayout());

        JPanel panelProductos = new JPanel(new BorderLayout());
        listaProductos = new JList<>(productos.stream().map(ProductoDTO::getDescripcion).toArray(String[]::new));
        listaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelProductos.add(new JLabel("Productos:"), BorderLayout.NORTH);
        panelProductos.add(new JScrollPane(listaProductos), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        botonCalcularTotal = new JButton("Calcular Total");
        botonComprar = new JButton("Comprar");
        JButton botonModificar = new JButton("Modificar Producto"); // Nuevo botón para modificar producto
        panelBotones.add(botonCalcularTotal);
        panelBotones.add(botonComprar);
        panelBotones.add(botonModificar); // Agrega el botón al panel

        etiquetaTotal = new JLabel("Total: $0.00");

        JPanel panelPrincipal = new JPanel(new GridLayout(2, 2));
        panelPrincipal.add(panelProductos);
        panelPrincipal.add(new JPanel()); // Espacio vacío para mantener la estructura
        panelPrincipal.add(new JPanel()); // Espacio vacío para mantener la estructura
        panelPrincipal.add(etiquetaTotal);

        panelCompra.add(panelPrincipal, BorderLayout.CENTER);
        panelCompra.add(panelBotones, BorderLayout.SOUTH);

        botonCalcularTotal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcularTotal();
            }
        });

        botonComprar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarCompra();
            }
        });

        botonModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarProducto();
            }
        });
    }

    private void calcularTotal() {
        int[] indicesSeleccionados = listaProductos.getSelectedIndices();
        BigDecimal total = BigDecimal.ZERO;
        for (int indice : indicesSeleccionados) {
            total = total.add(BigDecimal.valueOf(productos.get(indice).getPrecio()));
        }
        etiquetaTotal.setText("Total: $" + total.toString());
    }

    private void realizarCompra() {
        if (listaProductos.getSelectedIndices().length == 0 || listaTarjetas.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione productos y una tarjeta de crédito.");
            return;
        }

        // Aquí deberías rellenar con el código necesario para agregar la lógica de compra
        // Por ejemplo, guardar los datos de la compra y el ticket.

        // Muestra un mensaje al usuario
        JOptionPane.showMessageDialog(this, "Compra realizada. Seleccione 'Concretar Venta' para finalizar.");
    }

    private void concretarVenta() {
        // Implementa la lógica para concretar la venta aquí
        if (listaProductos.getSelectedIndices().length == 0 || listaTarjetas.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione productos y una tarjeta de crédito.");
            return;
        }

        int[] indicesSeleccionados = listaProductos.getSelectedIndices();
        List<Long> productosSeleccionados = new ArrayList<>();
        for (int indice : indicesSeleccionados) {
            productosSeleccionados.add(productos.get(indice).getId());
        }

        Long tarjetaSeleccionada = tarjetas.get(listaTarjetas.getSelectedIndex()).getId();

        try {
            String apiUrl = "http://localhost:8080/api/ventas/crear";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token); // Agrega el token
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Crear el objeto JSON para la solicitud
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("clienteId", 1); // Cambia esto si necesitas obtener el ID del cliente de alguna manera
            jsonObject.add("productos", new Gson().toJsonTree(productosSeleccionados));
            jsonObject.addProperty("tarjetaId", tarjetaSeleccionada);
            connection.getOutputStream().write(jsonObject.toString().getBytes());

            if (connection.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(this, "Venta concretada exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al concretar la venta: " + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al concretar la venta: " + e.getMessage());
        }
    }

    private List<ProductoDTO> obtenerProductosDelBackend() throws IOException {
        String apiUrl = "http://localhost:8080/api/productos/listar";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            // Suponiendo que el backend devuelve una lista de productos en formato JSON
            ProductoDTO[] productosArray = new Gson().fromJson(content.toString(), ProductoDTO[].class);
            return List.of(productosArray); // Convierte a una lista
        } else {
            System.out.println("Error en la conexión: " + connection.getResponseCode());
            return new ArrayList<>(); // Retorna una lista vacía si hay un error
        }
    }

    private List<TarjetaCreditoDTO> obtenerTarjetasDelBackend() throws IOException {
        String apiUrl = "http://localhost:8080/api/clientes/listar-tarjetas"; // Cambia esto si tu backend está en otra dirección.
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            // Suponiendo que el backend devuelve una lista de tarjetas en formato JSON
            TarjetaCreditoDTO[] tarjetasArray = new Gson().fromJson(content.toString(), TarjetaCreditoDTO[].class);
            return List.of(tarjetasArray); // Convierte a una lista
        } else {
            System.out.println("Error en la conexión: " + connection.getResponseCode());
            return new ArrayList<>(); // Retorna una lista vacía si hay un error
        }
    }

    private List<DescuentoDTO> obtenerDescuentosDelBackend() throws IOException {
        String apiUrl = "http://localhost:8080/api/descuentos/listar"; // Cambia esto si tu backend está en otra dirección.
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            // Suponiendo que el backend devuelve una lista de descuentos en formato JSON
            DescuentoDTO[] descuentosArray = new Gson().fromJson(content.toString(), DescuentoDTO[].class);
            return List.of(descuentosArray); // Convierte a una lista
        } else {
            System.out.println("Error en la conexión: " + connection.getResponseCode());
            return new ArrayList<>(); // Retorna una lista vacía si hay un error
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AplicacionComprasCliente::new);
    }

    private JPanel panelModificarProducto;
    private JTextField campoNombreProducto;
    private JTextField campoPrecioProducto;
    private JTextField campoMarcaProducto;
    private JComboBox<String> comboCategoriaProducto;
    private Long idProductoSeleccionado; // Para almacenar el ID del producto seleccionado

    // Método para modificar el producto seleccionado
    private void modificarProducto() {
        int indiceSeleccionado = listaProductos.getSelectedIndex();
        if (indiceSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto.");
            return;
        }

        ProductoDTO productoSeleccionado = productos.get(indiceSeleccionado);
        idProductoSeleccionado = productoSeleccionado.getId(); // Guarda el ID del producto

        // Crear un nuevo panel para modificar el producto
        panelModificarProducto = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Agregar componentes para mostrar la información del producto
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelModificarProducto.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        panelModificarProducto.add(new JLabel(String.valueOf(productoSeleccionado.getId())), gbc); // ID del producto

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelModificarProducto.add(new JLabel("Nombre:"), gbc);

        campoNombreProducto = new JTextField(productoSeleccionado.getDescripcion(), 20);
        gbc.gridx = 1;
        panelModificarProducto.add(campoNombreProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelModificarProducto.add(new JLabel("Precio:"), gbc);

        campoPrecioProducto = new JTextField(String.valueOf(productoSeleccionado.getPrecio()), 20);
        gbc.gridx = 1;
        panelModificarProducto.add(campoPrecioProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelModificarProducto.add(new JLabel("Marca:"), gbc);

        campoMarcaProducto = new JTextField(productoSeleccionado.getMarca(), 20);
        gbc.gridx = 1;
        panelModificarProducto.add(campoMarcaProducto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panelModificarProducto.add(new JLabel("Categoría:"), gbc);

        // Aquí puedes cargar las categorías desde el backend
        comboCategoriaProducto = new JComboBox<>(new String[]{"Categoria1", "Categoria2", "Categoria3"}); // Ejemplo de categorías
        gbc.gridx = 1;
        panelModificarProducto.add(comboCategoriaProducto, gbc);

        // Botón para guardar los cambios
        JButton botonGuardar = new JButton("Guardar Cambios");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panelModificarProducto.add(botonGuardar, gbc);

        botonGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarCambiosProducto();
            }
        });

        // Mostrar el panel de modificación en un nuevo diálogo
        JOptionPane.showMessageDialog(this, panelModificarProducto, "Modificar Producto", JOptionPane.PLAIN_MESSAGE);
    }

    // Método para guardar los cambios del producto
    private void guardarCambiosProducto() {
        String nombre = campoNombreProducto.getText();
        BigDecimal precio;
        try {
            precio = new BigDecimal(campoPrecioProducto.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio no válido.");
            return;
        }
        String marca = campoMarcaProducto.getText();
        String categoria = (String) comboCategoriaProducto.getSelectedItem();

        // Aquí debes implementar la lógica para enviar estos cambios al backend
        // Suponiendo que tienes un método en el backend para actualizar el producto

        // Ejemplo de cómo podrías hacerlo
        // (Recuerda que necesitarás implementar el endpoint en tu backend)
        try {
            String apiUrl = "http://localhost:8080/api/productos/modificar/" + idProductoSeleccionado; // Endpoint para modificar producto
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Crear el objeto JSON para la solicitud
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("nombre", nombre);
            jsonObject.addProperty("precio", precio);
            jsonObject.addProperty("marca", marca);
            jsonObject.addProperty("categoria", categoria);

            connection.getOutputStream().write(jsonObject.toString().getBytes());

            if (connection.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(this, "Producto modificado exitosamente.");
                // Actualiza la lista de productos
                productos = obtenerProductosDelBackend(); // Vuelve a cargar los productos desde el backend
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar el producto: " + connection.getResponseCode());
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al modificar el producto: " + e.getMessage());
        }
    }
}


