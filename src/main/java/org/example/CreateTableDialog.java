package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CreateTableDialog{
    public static void showDialog(Frame app){
        JTextField tableName = new JTextField(10);
        tableName.requestFocus();

        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Nazwa tabeli:"));
        panel.add(tableName);

        int result = JOptionPane.showConfirmDialog(null, panel, "Dodaj tabele", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if(result == JOptionPane.OK_OPTION){
            if(tableName.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(app, "Musisz podać nazwę tabeli","Błąd",JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<CustomTable> customTableList = app.getTableList();
            boolean exsists = customTableList.stream().anyMatch(t->t.gettableName().equalsIgnoreCase(tableName.getText().trim()));

            if(exsists) JOptionPane.showMessageDialog(app, "Tabela "+tableName.getText().trim()+" już istnieje","Błąd",JOptionPane.ERROR_MESSAGE);
            else addTable(app, tableName.getText().trim());
        }
    }

    private static void addTable(Frame app, String tName){
        CustomTable customTable = new CustomTable(tName,app);
        app.add(customTable);
        app.revalidate();
        app.repaint();
        app.addTable(customTable);
        app.updateInstructionLabel();
    }
}

