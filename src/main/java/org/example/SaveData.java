package org.example;
import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SaveData {
    public static void SaveDataToYMLFile(Frame f){
        ArrayList<CustomTable> tables = f.getTableList();
        if(tables.isEmpty()){
            JOptionPane.showMessageDialog(f, "Brak tabel do zapisania","Błąd",JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz plik");

        int selection = fileChooser.showSaveDialog(null);
        if(selection != JFileChooser.APPROVE_OPTION) return;

        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if(!filePath.endsWith(".txt")) filePath+=".txt";

        StringBuilder allSQL = new StringBuilder();
        ArrayList<TableConnection> connections = ConnectionPanel.getInstance().getConnections();

        for(CustomTable t : tables){
            StringBuilder createTableSQL = new StringBuilder();
            createTableSQL.append("CREATE TABLE ").append(t.gettableName()).append(" (\n");

            List<TableRow> tableRowList = t.getTableRowList();
            for(TableRow attributeName : tableRowList){
                if(attributeName.getAttributes().equalsIgnoreCase("Foreign key")){
                    createTableSQL.append("\t")
                            .append(attributeName.getName()).append(" INT")
                            .append(",\n");
                }else{
                    createTableSQL.append("\t")
                            .append(attributeName.getName())
                            .append(" ")
                            .append(attributeName.getAttributes())
                            .append(",\n");
                }
            }

            for(TableConnection connection : connections){
                if(connection.getTable1().equals(t)){
                    String childTable = connection.getTable1().gettableName();
                    String parentTable = connection.getTable2().gettableName();
                    String foreignKeyColumn = connection.getTable2().getPrimaryKey();

                    createTableSQL.append("\tFOREIGN KEY (")
                            .append(foreignKeyColumn)
                            .append(") REFERENCES ")
                            .append(parentTable)
                            .append("(")
                            .append(foreignKeyColumn)
                            .append("),\n");
                }
            }

            int lastComma = createTableSQL.lastIndexOf(",");
            if(lastComma != -1) createTableSQL.deleteCharAt(lastComma);

            createTableSQL.append("\n);\n\n");
            allSQL.append(createTableSQL);

            StringBuilder insertTableSQL = new StringBuilder("INSERT INTO ")
                    .append(t.gettableName()).append(" VALUES\n");

            Map<String, List<String>> generatedData = t.getGeneratedData();
            int recordCount = generatedData.values().stream().findFirst().map(List::size).orElse(0);

            if(recordCount>0){
                for(int i=0; i<recordCount; i++) {
                    insertTableSQL.append("\t(");
                    for(String key : generatedData.keySet()){
                        String value = generatedData.get(key).get(i);
                        if(!value.matches("-?\\d+(\\.\\d+)?")){
                            value = "'" + value + "'";
                        }
                        insertTableSQL.append(value).append(", ");
                    }
                    insertTableSQL.setLength(insertTableSQL.length() - 2);
                    insertTableSQL.append(")");

                    if(i<recordCount-1) insertTableSQL.append(",\n");
                    else insertTableSQL.append(";\n\n");
                }
                allSQL.append(insertTableSQL);
            }
        }

        try(FileWriter fw = new FileWriter(filePath)) {
            fw.write(allSQL.toString());
            JOptionPane.showMessageDialog(null, "Zapisano do pliku: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd zapisu do pliku!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
