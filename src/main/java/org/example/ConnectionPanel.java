package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ConnectionPanel extends JPanel {
    private final ArrayList<TableConnection> connections;
    private static Frame frame;
    private static ConnectionPanel connectionPanel;

    public ConnectionPanel(Frame frame) {
        this.connections = new ArrayList<>();
        this.frame = frame;
        connectionPanel = this;
        setOpaque(false);
    }

    public void addConnection(CustomTable table1Source, CustomTable table2){
        connections.add(new TableConnection(table1Source,table2));
        table1Source.addTableRow(table2.getPrimaryKey(),"FOREIGN KEY");
        table1Source.addTableForeignKey(table2);
        System.out.println("Dodano polaczenie miedzy "+table1Source.gettableName()+" i "+table2.gettableName());
        repaint();
    }

    public static void selectTableForConnection(CustomTable source){
        ArrayList<CustomTable> tables = frame.getTableList();
        if(tables.size()<2){
            JOptionPane.showMessageDialog(frame, "Musisz posiadać minimum 2 tabele do połaczenia relacji","Błąd",JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<CustomTable> filteredTables = tables.stream()
                .filter(t -> !t.gettableName().equalsIgnoreCase(source.gettableName()))
                .filter(t -> t.getPrimaryKey() != null && !t.getPrimaryKey().isBlank())
                .toList();

        if(filteredTables.isEmpty()){
            JOptionPane.showMessageDialog(frame, "Brak dostępnych tabel z kluczem głównym do połączenia.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] tableNames = filteredTables.stream()
                .map(CustomTable::gettableName)
                .toArray(String[]::new);

        String selectedTable = (String) JOptionPane.showInputDialog(
                frame,
                "Wybierz tabelę do relacji z " + source.gettableName(),
                "Dodaj relację",
                JOptionPane.QUESTION_MESSAGE,
                null,
                tableNames,
                tableNames[0]
        );
        if(selectedTable != null){
            for(TableConnection t : connectionPanel.connections){
                System.out.println("t1: "+t.getTable1().gettableName());
                System.out.println("t2: "+t.getTable2().gettableName());
                if(t.getTable2().gettableName().equals(selectedTable) || t.getTable1().gettableName().equals(selectedTable)){
                    JOptionPane.showMessageDialog(frame, "Połączenie już istnieje","Blad",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            for(CustomTable table : tables){
                if(table.gettableName().equals(selectedTable)){
                    connectionPanel.addConnection(source,table);
                    break;
                }
            }
        }
    }

    public void repaintConnections(){
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.CYAN);
        g2.setStroke(new BasicStroke(2));

        for(TableConnection connection : connections) {
            CustomTable table1 = connection.getTable1();
            CustomTable table2 = connection.getTable2();

            Map<String, Point> attributes1 = table1.getAttributePositions();
            Map<String, Point> attributes2 = table2.getAttributePositions();

            Rectangle bounds1 = table1.getBounds();
            Rectangle bounds2 = table2.getBounds();

            String primaryKey = table2.getPrimaryKey();

            for(TableRow row : table1.getTableRowList()){
                String attributeName = row.getName();
                if(attributeName.equalsIgnoreCase(primaryKey)){
                    Point p1 = attributes1.get(attributeName);
                    Point p2 = attributes2.get(attributeName);

                    int dx = bounds2.x - bounds1.x;

                    if(dx>0){
                        p1.x = bounds1.x + bounds1.width;
                        p2.x = bounds2.x;
                    }else{
                        p1.x = bounds1.x;
                        p2.x = bounds2.x + bounds2.width;
                    }
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }

    public static void removeConnection() {
        if (connectionPanel.connections.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Brak istniejących relacji do usunięcia", "Informacja", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> comboBox = new JComboBox<>();
        for (TableConnection conn : connectionPanel.connections) {
            String entry = conn.getTable1().gettableName() + " ==> " + conn.getTable2().gettableName();
            comboBox.addItem(entry);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Wybierz relację do usunięcia:"));
        panel.add(comboBox);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Usuń relację", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if(result == JOptionPane.OK_OPTION) {
            int selectedIndex = comboBox.getSelectedIndex();
            if(selectedIndex>=0) {
                TableConnection connection = connectionPanel.connections.get(selectedIndex);
                CustomTable table1 = connection.getTable1();
                CustomTable table2 = connection.getTable2();

                table1.removeTableRow(table2.getPrimaryKey());

                connectionPanel.connections.remove(selectedIndex);

                frame.repaint();
                connectionPanel.repaint();
            }
        }
    }
    public ArrayList<TableConnection> getConnections() {
        return connections;
    }
    public static ConnectionPanel getInstance() {
        return connectionPanel;
    }
}

