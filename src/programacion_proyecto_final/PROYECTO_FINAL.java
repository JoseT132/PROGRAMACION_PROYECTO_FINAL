package programacion_proyecto_final;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class PROYECTO_FINAL extends JFrame {

    // Declaración de variables
    private JTable[] tables; // Arreglo de tablas para los libros de la hoja de cálculo
    private JTabbedPane tabbedPane; // Panel de pestañas para mostrar los libros
    private JTextField formulaField; // Campo de texto para ingresar fórmulas
    private JTextField cellField; // Campo de texto para ingresar la celda de destino de una fórmula
    private int bookCount = 1; // Contador de libros

    // Constructor de la clase
    public PROYECTO_FINAL() {
        //Formato de hoja de cálculo
        setTitle("Hoja de cálculo");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creación del menú
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");

        // Opción para crear un nuevo libro
        JMenuItem newBookItem = new JMenuItem("Nuevo Libro");
        newBookItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNewBook();
            }
        });
        fileMenu.add(newBookItem);

        // Opción para mostrar la interfaz de tabla hash 
        JMenuItem hashTableItem = new JMenuItem("Tabla Hash");
        hashTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHashTableInterface();
            }
        });
        fileMenu.add(hashTableItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Configuración del panel de pestañas y panel inferior con campos de texto y botón
        tabbedPane = new JTabbedPane();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        cellField = new JTextField(5);
        formulaField = new JTextField();
        JButton applyButton = new JButton("Aplicar fórmula");

        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFormula();
            }
        });

        bottomPanel.add(new JLabel("Celda:"), BorderLayout.WEST);
        bottomPanel.add(cellField, BorderLayout.CENTER);
        bottomPanel.add(formulaField, BorderLayout.CENTER);
        bottomPanel.add(applyButton, BorderLayout.EAST);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        addNewBook(); // Agregar el primer libro al iniciar la aplicación
    }

    // Método para agregar un nuevo libro a la hoja de cálculo
    private void addNewBook() {
        // Definición de nombres de columnas y datos iniciales
        String[] columnNames = {"A", "B", "C", "D", "E"};
        Object[][] data = new Object[10][5];

        // Inicializar datos con celdas vacías
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                data[i][j] = "";
            }
        }

        // Crear modelo de tabla
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // Permitir la edición de todas las celdas
            }
        };

        // Crear tabla con el modelo
        JTable table = new JTable(model);

        // Agregar un listener para detectar cambios en las celdas
        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    String cellValue = (String) table.getValueAt(row, column);
                    // Si la celda comienza con '=', evaluar como fórmula
                    if (cellValue.startsWith("=")) {
                        evaluateFormula(table, row, column, cellValue);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        tabbedPane.addTab("Libro " + bookCount, scrollPane); // Agregar pestaña con la tabla
        bookCount++; // Incrementar contador de libros

        // Agregar la nueva tabla al arreglo de tablas
        if (tables == null) {
            tables = new JTable[]{table};
        } else {
            JTable[] newTables = new JTable[tables.length + 1];
            System.arraycopy(tables, 0, newTables, 0, tables.length);
            newTables[newTables.length - 1] = table;
            tables = newTables;
        }
    }

    // Método para mostrar la interfaz de tabla hash 
    private void showHashTableInterface() {
        HashTableGUI hashTableGUI = new HashTableGUI();
        hashTableGUI.setVisible(true);
    }

    // Método para aplicar una fórmula a una celda específica
    private void applyFormula() {
        //Verificar la fórmula o la celda no pueden estar vacías
        try {
            String formula = formulaField.getText().trim();
            String cell = cellField.getText().trim();
            if (formula.isEmpty() || cell.isEmpty()) {
                throw new Exception("La fórmula o la celda no pueden estar vacías.");
            }

            // Obtener la tabla seleccionada
            int selectedTabIndex = tabbedPane.getSelectedIndex();
            JTable selectedTable = tables[selectedTabIndex];

            // Obtener fila y columna de la celda
            int row = Integer.parseInt(cell.substring(1)) - 1;
            int column = cell.charAt(0) - 'A';

            // Evaluar la fórmula y establecer el resultado en la celda
            double result = evaluateExpression(formula.substring(1)); // Eliminar el '='
            selectedTable.setValueAt(result, row, column);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al aplicar la fórmula: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para evaluar una fórmula en una celda específica de una tabla
    private void evaluateFormula(JTable table, int row, int column, String formula) {
        try {
            double result = evaluateExpression(formula.substring(1)); // Eliminar el '='
            table.setValueAt(result, row, column);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al evaluar la fórmula: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para evaluar una expresión matemática dada
    private double evaluateExpression(String expression) throws Exception {
        String[] tokens = expression.split("(?=[-+*/])|(?<=[-+*/])");

        double result = 0.0;
        boolean firstToken = true;
        String operator = "+";
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) {
                continue; // Ignorar tokens vacíos
            }

            // Si el token es un operador, actualizar el operador actual
            if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                operator = token;
            } else {
                // Si el token es un número o una referencia a una celda, realizar la operación correspondiente
                double value;
                if (token.startsWith("Libro")) {
                    // Si la referencia es a otra tabla (libro), obtener el valor de la celda en esa tabla
                    String[] parts = token.split("!");
                    int bookIndex = Integer.parseInt(parts[0].substring(5)) - 1;
                    String cellReference = parts[1];
                    value = getCellValue(bookIndex, cellReference);
                } else {
                    // Si no hay prefijo de libro, asumir que es del libro actual y obtener el valor de la celda
                    value = getCellValue(tabbedPane.getSelectedIndex(), token);
                }

                // Realizar la operación según el operador actual
                switch (operator) {
                    case "+":
                        result = firstToken ? value : result + value;
                        break;
                    case "-":
                        result = firstToken ? value : result - value;
                        break;
                    case "*":
                        result = firstToken ? value : result * value;
                        break;
                    case "/":
                        result = firstToken ? value : result / value;
                        break;
                }
                firstToken = false; // Cambiar el indicador para los siguientes tokens
            }
        }
        return result; // Devolver el resultado final de la expresión
    }

    // Método para obtener el valor de una celda en una tabla específica
    private double getCellValue(int bookIndex, String cellReference) throws Exception {
        // Obtener fila y columna de la celda
        int row = Integer.parseInt(cellReference.substring(1)) - 1;
        int column = cellReference.charAt(0) - 'A';
        // Obtener el valor de la celda en la tabla especificada
        Object value = tables[bookIndex].getValueAt(row, column);
        // Si el valor es nulo o vacío, lanzar una excepción
        if (value == null || value.toString().isEmpty()) {
            throw new Exception(String.format("La celda %s en Libro %d está vacía.", cellReference, bookIndex + 1));
        }
        // Convertir y devolver el valor como un número
        return Double.parseDouble(value.toString());
    }

    // Método principal para iniciar la aplicación
    public static void main(String[] args) {
        // Ejecutar la aplicación en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Crear una instancia de la clase PROYECTO_FINAL y hacerla visible
                PROYECTO_FINAL app = new PROYECTO_FINAL();
                app.setVisible(true);
            }
        });
    }
}
