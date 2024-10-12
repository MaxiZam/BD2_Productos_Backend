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

import com.ar.unrn.tp.frontend.clases.Descuento;
import com.ar.unrn.tp.frontend.clases.DescuentoCompra;
import com.ar.unrn.tp.frontend.clases.Producto;
import com.ar.unrn.tp.frontend.clases.TarjetaCredito;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AplicacionComprasCliente extends JFrame {
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JPanel panelLogin;
    private JPanel panelCompra;
    private JList<String> listaProductos;
    private JList<String> listaTarjetas;
    private JTextArea areaDescuentos;
    private JButton botonCalcularTotal;
    private JButton botonComprar;
    private JLabel etiquetaTotal;

    private List<Producto> productos;
    private List<TarjetaCredito> tarjetas;
    private List<Descuento> descuentos;
    private String token; // Para almacenar el token de autenticación

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
            tarjetas = obtenerTarjetasDelBackend();    // Puedes agregar algo similar para las tarjetas.
            descuentos = obtenerDescuentosDelBackend(); // Lo mismo con los descuentos.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void crearPanelLogin() {
        panelLogin = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel etiquetaUsuario = new JLabel("Usuario:");
        JLabel etiquetaContrasena = new JLabel("Contraseña:");
        campoUsuario = new JTextField(20);
        campoContrasena = new JPasswordField(20);
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
                if (iniciarSesion(campoUsuario.getText(), String.valueOf(campoContrasena.getPassword()))) {
                    setContentPane(panelCompra);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(panelLogin, "Credenciales incorrectas, intenta nuevamente.");
                }
            }
        });
    }

    private boolean iniciarSesion(String nombre, String dni) {
        try {
            String apiUrl = "http://localhost:8080/api/login"; // Cambia esto si tu backend está en otra dirección.

            // Crear el objeto JSON para la solicitud
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("nombre", nombre); // Cambiar "usuario" por "nombre"
            jsonObject.addProperty("dni", dni); // Cambiar "contrasena" por "dni"

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

                // Aquí se asume que el backend devuelve el objeto Cliente en lugar de un token
                JsonObject responseJson = new Gson().fromJson(content.toString(), JsonObject.class);
                // Puedes obtener información específica del cliente si lo deseas
                // Por ejemplo, token = responseJson.get("token").getAsString(); si el backend también devuelve un token
                return true; // Inicio de sesión exitoso
            } else {
                System.out.println("Error en la conexión: " + connection.getResponseCode());
                //return false;
                return true; // Inicio de sesión simulado
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Error al iniciar sesión
        }
    }


    private void crearPanelCompra() {
        panelCompra = new JPanel(new BorderLayout());

        JPanel panelProductos = new JPanel(new BorderLayout());
        listaProductos = new JList<>(productos.stream().map(Producto::getDescripcion).toArray(String[]::new));
        listaProductos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panelProductos.add(new JLabel("Productos:"), BorderLayout.NORTH);
        panelProductos.add(new JScrollPane(listaProductos), BorderLayout.CENTER);

        JPanel panelTarjetas = new JPanel(new BorderLayout());
        listaTarjetas = new JList<>(tarjetas.stream().map(TarjetaCredito::getNumero).toArray(String[]::new));
        panelTarjetas.add(new JLabel("Tarjetas de Crédito:"), BorderLayout.NORTH);
        panelTarjetas.add(new JScrollPane(listaTarjetas), BorderLayout.CENTER);

        JPanel panelDescuentos = new JPanel(new BorderLayout());
        areaDescuentos = new JTextArea();
        areaDescuentos.setEditable(false);
        for (Descuento descuento : descuentos) {
            areaDescuentos.append(descuento.toString() + "\n");
        }
        panelDescuentos.add(new JLabel("Descuentos Activos:"), BorderLayout.NORTH);
        panelDescuentos.add(new JScrollPane(areaDescuentos), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        botonCalcularTotal = new JButton("Calcular Total");
        botonComprar = new JButton("Comprar");
        panelBotones.add(botonCalcularTotal);
        panelBotones.add(botonComprar);

        etiquetaTotal = new JLabel("Total: $0.00");

        JPanel panelPrincipal = new JPanel(new GridLayout(2, 2));
        panelPrincipal.add(panelProductos);
        panelPrincipal.add(panelTarjetas);
        panelPrincipal.add(panelDescuentos);
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
    }

    private void calcularTotal() {
        int[] indicesSeleccionados = listaProductos.getSelectedIndices();
        BigDecimal total = BigDecimal.ZERO;
        for (int indice : indicesSeleccionados) {
            total = total.add(productos.get(indice).getPrecio());
        }
        etiquetaTotal.setText("Total: $" + total.toString());
    }

    private void realizarCompra() {
        if (listaProductos.getSelectedIndices().length == 0 || listaTarjetas.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione productos y una tarjeta de crédito.");
            return;
        }

        int[] indicesSeleccionados = listaProductos.getSelectedIndices();
        List<Long> productosSeleccionados = new ArrayList<>();
        for (int indice : indicesSeleccionados) {
            productosSeleccionados.add(productos.get(indice).getId()); // Asegúrate de que Producto tenga un método getId()
        }

        Long tarjetaSeleccionada = tarjetas.get(listaTarjetas.getSelectedIndex()).getId(); // Asegúrate de que TarjetaCredito tenga un método getId()

        try {
            String apiUrl = "http://localhost:8080/api/ventas/crear"; // Cambia esto si tu backend está en otra dirección.
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token); // Agrega el token
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Crear el objeto JSON para la solicitud
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("idCliente", 1); // Cambia esto si necesitas obtener el ID del cliente de alguna manera
            jsonObject.add("productos", new Gson().toJsonTree(productosSeleccionados));
            jsonObject.addProperty("tarjetaId", tarjetaSeleccionada);
            connection.getOutputStream().write(jsonObject.toString().getBytes());

            if (connection.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(this, "Compra realizada exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al realizar la compra: " + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al realizar la compra: " + e.getMessage());
        }
    }

    private List<Producto> obtenerProductosDelBackend() throws IOException {
        String apiUrl = "http://localhost:8080/api/productos"; // Cambia esto si tu backend está en otra dirección.
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
            Producto[] productosArray = new Gson().fromJson(content.toString(), Producto[].class);
            return List.of(productosArray); // Convierte a una lista
        } else {
            System.out.println("Error en la conexión: " + connection.getResponseCode());
            return new ArrayList<>(); // Retorna una lista vacía si hay un error
        }
    }

    private List<TarjetaCredito> obtenerTarjetasDelBackend() throws IOException {
        String apiUrl = "http://localhost:8080/api/tarjetas"; // Cambia esto si tu backend está en otra dirección.
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
            TarjetaCredito[] tarjetasArray = new Gson().fromJson(content.toString(), TarjetaCredito[].class);
            return List.of(tarjetasArray); // Convierte a una lista
        } else {
            System.out.println("Error en la conexión: " + connection.getResponseCode());
            return new ArrayList<>(); // Retorna una lista vacía si hay un error
        }
    }

    private List<Descuento> obtenerDescuentosDelBackend() throws IOException {
        String apiUrl = "http://localhost:8080/api/descuentos"; // Cambia esto si tu backend está en otra dirección.
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
            Descuento[] descuentosArray = new Gson().fromJson(content.toString(), Descuento[].class);
            return List.of(descuentosArray); // Convierte a una lista
        } else {
            System.out.println("Error en la conexión: " + connection.getResponseCode());
            return new ArrayList<>(); // Retorna una lista vacía si hay un error
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AplicacionComprasCliente());
    }
}

