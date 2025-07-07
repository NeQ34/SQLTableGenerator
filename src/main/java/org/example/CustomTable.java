package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.List;

public class CustomTable extends JPanel{
    private int x,y;
    private int primaryKeySize;
    private String primaryKey;
    private final JPanel tablePanel;
    private final Frame parent;
    private final String tableName;
    private final List<CustomTable> foreignTablesKeysList;
    private List<TableRow> tableRowList;
    private final Map<String, List<String>> generatedData;
    private final String dataTypes[] = {"AUTO_INCREMENT","CHAR","Foreign Key","VARCHAR","TEXT","INT","FLOAT","DATE"};
    private final Font defaultFont = new Font("DefaVu",Font.BOLD, 20);
    private final Color defaultTableBgColor = Color.decode("#3d3d3d");
    private final Color defaultTableNameBgColor = Color.decode("#3d3d3d");
    private final Color defaultForegroundColor = Color.decode("#f7f1e3");

    public CustomTable(String tableName, Frame parent) {
        this.tableName = tableName;
        this.parent = parent;
        foreignTablesKeysList = new ArrayList<>();
        generatedData = new LinkedHashMap<>();
        tableRowList = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBounds(100,100,350,300);

        JLabel titleLabel = new JLabel(tableName,SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.pink);
        titleLabel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));
        titleLabel.setBackground(defaultTableNameBgColor);
        titleLabel.setForeground(defaultForegroundColor);
        titleLabel.setFont(defaultFont);
        System.out.println(titleLabel.getPreferredSize().height);
        add(titleLabel, BorderLayout.NORTH);

        tablePanel = new JPanel();
        tablePanel.setBackground(Color.gray);
        tablePanel.setLayout(new BoxLayout(tablePanel,BoxLayout.Y_AXIS));
        tablePanel.setBackground(defaultTableBgColor);
        tablePanel.setForeground(defaultForegroundColor);
        tablePanel.setFont(defaultFont);
        add(tablePanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    showMenu(e);
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                e.getComponent().setLocation(e.getX()+e.getComponent().getX()-x,(e.getY()+e.getComponent().getY()-y));
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                parent.repaintConnections();
            }
        });
    }

    private void showMenu(MouseEvent e){
        JPopupMenu menu = new JPopupMenu();
        JMenuItem addAttributeItem = new JMenuItem("Dodaj atrybut");
        JMenuItem item = new JMenuItem("Dodaj relację");
        JMenuItem remove = new JMenuItem("Usuń relację");
        JMenuItem generateDataItem = new JMenuItem("Generuj dane");
        JMenuItem showGeneratedDataItem = new JMenuItem("Zobacz dane");
        JMenuItem removeTable = new JMenuItem("Usuń tabelę");

        item.addActionListener(select -> ConnectionPanel.selectTableForConnection(this));
        remove.addActionListener(re -> ConnectionPanel.removeConnection());
        addAttributeItem.addActionListener(attr -> addTableAttribute());
        generateDataItem.addActionListener(generate -> GenerateRandomData.showDialog(this));
        showGeneratedDataItem.addActionListener(showData -> GenerateRandomData.showGeneratedData(this));
        removeTable.addActionListener(removeTab -> removeTable());

        menu.add(addAttributeItem);
        menu.add(item);
        menu.add(remove);
        menu.add(generateDataItem);
        menu.add(showGeneratedDataItem);
        menu.add(removeTable);
        menu.show(this,e.getX(),e.getY());
    }
    public void addTableAttribute(){
        JTextField nameField = new JTextField(10);
        JComboBox<String> attributesTypes = new JComboBox<>(dataTypes);
        JCheckBox primaryKeyCheckBox = new JCheckBox();

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel("Nazwa: "));
        panel.add(nameField);
        panel.add(new JLabel("Wybierz atrybut: "));
        panel.add(attributesTypes);
        panel.add(new JLabel("Klucz główny:"));
        panel.add(primaryKeyCheckBox);
        if(getPrimaryKey()!=null) primaryKeyCheckBox.setEnabled(false);

        int result = JOptionPane.showConfirmDialog(null,panel, "Dodaj nowy element", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if(result == JOptionPane.OK_OPTION){
            if(!nameField.getText().trim().isEmpty()){
                if(primaryKeyCheckBox.isSelected()){
                    setPrimaryKey(nameField.getText());
                    tableRowList.add(new TableRow(this,nameField.getText(),"INT PRIMARY KEY "+attributesTypes.getSelectedItem().toString()));
                }else tableRowList.add(new TableRow(this,nameField.getText(),attributesTypes.getSelectedItem().toString()));
            }else JOptionPane.showMessageDialog(null, "Podaj nazwę atrybutu", "Informacja", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public void removeTableRow(TableRow tableRow){
        tableRowList.remove(tableRow);
        System.out.println("Usunięto: "+tableRow.getName());
    }
    public void removeTableRow(String rowName){
        TableRow toRemove = null;
        System.out.println("Tname: "+getName()+" tableRowName(parametr): "+rowName);
        for(TableRow r : getTableRowList()){
            if(r.getName().equalsIgnoreCase(rowName)){
                toRemove = r;
            }
        }
        toRemove.removeTableRow();
    }
    public void removeTable(){
        this.remove(this);
        parent.removeTable(this);
    }

    public ArrayList<String> getAttributeNames(){
        ArrayList<String> names = new ArrayList<>();
        for(TableRow row : tableRowList){
            names.add(row.getName());
        }
        return names;
    }
    public Map<String, Point> getAttributePositions() {
        Map<String, Point> positions = new HashMap<>();

        for(TableRow t : tableRowList){
            int y = t.getHeight()/2;
            int x = getX()<getParent().getWidth()/2 ? t.getWidth() : 0;

            Point p = SwingUtilities.convertPoint(t,new Point(x,y),ConnectionPanel.getInstance());
            positions.put(t.getName(),p);
        }
        return positions;
    }
    public void addTableForeignKey(CustomTable table){
        foreignTablesKeysList.add(table);
    }

    public void addTableRow(String name, String attributes){
        tableRowList.add(new TableRow(this, name, attributes));
    }

    public List<CustomTable> getForeignKeys(){
        return foreignTablesKeysList;
    }

    public void addGeneratedData(String attribute, List<String> data){
        generatedData.put(attribute,data);
    }

    public Map<String, List<String>> getGeneratedData(){
        return generatedData;
    }

    public void displayGeneratedData(){
        for(Map.Entry<String, List<String>> data : generatedData.entrySet()){
            System.out.println(data.getKey()+": "+data.getValue());
        }
    }
    public boolean hasGeneretedData(){
        return generatedData.isEmpty();
    }

    public List<TableRow> getTableRowList() {
        return tableRowList;
    }

    public void setPrimaryKey(String primaryKey){
        this.primaryKey = primaryKey;
    }

    public String getPrimaryKey(){
        return primaryKey;
    }

    public String gettableName() {
        return tableName;
    }

    public void setPrimaryKeySize(int primaryKeySize){
        this.primaryKeySize = primaryKeySize;
    }

    public int getPrimaryKeySize(){
        return primaryKeySize;
    }

    public JPanel getTablePanel(){
        return tablePanel;
    }
}

