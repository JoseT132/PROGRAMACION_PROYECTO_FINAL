package programacion_proyecto_final;

import java.util.LinkedList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;

// Definición de la clase HashTableGUI que extiende JFrame para la interfaz gráfica de la tabla hash
public class HashTableGUI extends JFrame {

    // Declaración de componentes de la interfaz
    private JTextField keyField; // Campo de texto para la clave
    private JTextField valueField; // Campo de texto para el valor
    private JTable table; // Tabla para mostrar la tabla hash
    private DefaultTableModel tableModel; // Modelo de tabla
    private MapeoConHash mapeoConHash; // Instancia de la clase MapeoConHash para manejar la tabla hash

    // Constructor de la clase HashTableGUI
    public HashTableGUI() {
        // Inicialización de la tabla hash y otros componentes de la interfaz
        mapeoConHash = new MapeoConHash();

        setTitle("Tabla Hash");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear panel para la entrada de datos y botón de agregar
        JPanel inputPanel = new JPanel();
        keyField = new JTextField(5);
        valueField = new JTextField(5);
        JButton addButton = new JButton("Agregar");

        inputPanel.add(new JLabel("Clave:"));
        inputPanel.add(keyField);
        inputPanel.add(new JLabel("Valor:"));
        inputPanel.add(valueField);
        inputPanel.add(addButton);

        // Crear tabla con letras en vez de índices numéricos
        tableModel = new DefaultTableModel();
        for (int i = 0; i < 10; i++) {
            tableModel.addColumn(String.valueOf((char) ('A' + i)));
        }
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Acción del botón de agregar
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDataToTable();
            }
        });
    }

    // Método para agregar datos a la tabla hash
    private void addDataToTable() {
        try {
            int key;
            String keyText = keyField.getText().toUpperCase(); // Convertir a mayúsculas
            if (keyText.matches("[A-J]")) {
                key = keyText.charAt(0) - 'A' + 1; // Convertir letras a números según su posición en el alfabeto
            } else {
                key = Integer.parseInt(keyText);
            }
            char value = valueField.getText().charAt(0);

            mapeoConHash.put(key, value);
            updateTable();

            keyField.setText("");
            valueField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una clave válida.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (StringIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un valor válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para actualizar la tabla con los datos de la tabla hash
    private void updateTable() {
        tableModel.setRowCount(0); // Limpiar la tabla

        LinkedList<MapeoConHash.Entry>[] hashTable = mapeoConHash.getTable();
        for (int i = 0; i < hashTable.length; i++) {
            if (!hashTable[i].isEmpty()) {
                StringBuilder cellContent = new StringBuilder();
                for (MapeoConHash.Entry entry : hashTable[i]) {
                    // Si la clave es un número, mostrarla como está, de lo contrario, convertirla a letra
                    String keyString = (entry.key >= 1 && entry.key <= 10) ? String.valueOf((char) ('A' + entry.key - 1)) : String.valueOf(entry.key);
                    cellContent.append("(").append(keyString).append(",").append(entry.value).append(") ");
                }
                tableModel.addRow(new Object[10]);
                tableModel.setValueAt(cellContent.toString(), tableModel.getRowCount() - 1, i);
            }
        }
    }

    // Método principal para iniciar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HashTableGUI().setVisible(true);
            }
        });
    }
}

// Clase para implementar la tabla hash
class MapeoConHash {

    private int N = 10; // Tamaño de la tabla hash
    private LinkedList<Entry>[] table; // Array de listas enlazadas para cada posición de la tabla

    // Constructor para inicializar la tabla
    public MapeoConHash() {
        table = new LinkedList[N];
        for (int i = 0; i < N; i++) {
            table[i] = new LinkedList<>();
        }
    }

    // Método para insertar datos en la tabla hash
    public void put(int key, char value) {
        int hashIndex = hashFunction(key); // Calcular el índice hash
        table[hashIndex].add(new Entry(key, value)); // Agregar la entrada a la lista enlazada correspondiente
    }

    // Método para obtener la tabla hash
    public LinkedList<Entry>[] getTable() {
        return table;
    }

    // Función de hash para calcular el índice hash de una clave
    private int hashFunction(int key) {
        if (key >= 1 && key <= 10) {
            return key - 1;
        } else {
            // Si la clave está fuera del rango válido (A-J), colocarla en la última celda de la tabla
            return N - 1;
        }
    }

    // Clase interna para representar una entrada en la tabla hash
    static class Entry {

        int key; // Clave
        char value; // Valor

        // Constructor de la entrada
        Entry(int key, char value) {
            this.key = key;
            this.value = value;
        }

        // Método para representar la entrada como una cadena de texto
        @Override
        public String toString() {
            return "(" + key + "," + value + ")";
        }
    }
}
