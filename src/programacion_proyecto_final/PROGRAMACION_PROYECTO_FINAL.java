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