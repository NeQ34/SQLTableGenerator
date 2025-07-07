package org.example;

import net.datafaker.Faker;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GenerateRandomData {
    public static void showDialog(CustomTable table){
        ArrayList<String> attributes = table.getAttributeNames();
        if(attributes.isEmpty()){
            JOptionPane.showMessageDialog(null,"Brak atrybutów","Blad",JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Generowanie danych");
        dialog.setSize(400,300);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(attributes.size(), 2));
        JComboBox<String> comboBoxes[] = new JComboBox[attributes.size()];
        ArrayList<String> ids = new ArrayList<>(Arrays.asList("ID","Id","id","iD"));

        TreeSet<String> categories2 = new TreeSet();
        categories2.add("AUTO_INCREMENT");categories2.add("Foreign Key");categories2.add("Name");categories2.add("Last Name");
        categories2.add("Address");categories2.add("Email");categories2.add("Number");categories2.add("Phone Number");
        categories2.add("Company");categories2.add("Date");categories2.add("Lorem Ipsum");categories2.add("University");
        categories2.add("Currency");categories2.add("App");categories2.add("Artist");categories2.add("Book");
        categories2.add("Credit Card Number");categories2.add("Color");categories2.add("Programming language");categories2.add("Music");
        categories2.add("Job");categories2.add("Animal");categories2.add("Beer");categories2.add("Cat");
        categories2.add("Food");categories2.add("Hobby");categories2.add("Dessert");

        for(int i=0; i<attributes.size(); i++){
            String attrName = attributes.get(i);
            JLabel label = new JLabel(attrName);
            JComboBox<String> comboBox = new JComboBox<>(categories2.toArray(new String[0]));
            comboBoxes[i] = comboBox;

            boolean isForeignKey = false;
            for(CustomTable t : table.getForeignKeys()){
                String foreignPrimaryKey = t.getPrimaryKey();
                System.out.println(attrName+" "+foreignPrimaryKey);
                if(attrName.equals(foreignPrimaryKey)){
                    isForeignKey = true;
                    break;
                }
            }
            if(isForeignKey) comboBox.setSelectedIndex(16);
            inputPanel.add(label);
            inputPanel.add(comboBox);
        }

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JLabel countLabel = new JLabel("Liczba rekordów:");
        JTextField countField = new JTextField("10",5);
        JButton generateDataButton = new JButton("Generuj");

        bottomPanel.add(countLabel);
        bottomPanel.add(countField);
        bottomPanel.add(generateDataButton);

        generateDataButton.addActionListener(e->{
            int recordCount;
            try{
                recordCount = Integer.parseInt(countField.getText());
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog, "Podaj poprawną liczbę","Błąd",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Faker f = new Faker();

            boolean hasAutoIncrement = false;
            for(JComboBox<String> box : comboBoxes){
                if(box.getSelectedItem().equals("AUTO_INCREMENT")){
                    hasAutoIncrement = true;
                    break;
                }
            }
            if(hasAutoIncrement){
                List<String> idColumn = new ArrayList<>();
                for(int i=0; i<recordCount; i++){
                    idColumn.add(String.valueOf(i+1));
                }
                table.addGeneratedData("ID",idColumn);
                table.setPrimaryKeySize(idColumn.size());
            }

            for(int i=0; i<attributes.size(); i++){
                String attribute = attributes.get(i).strip();
                String selectedCategory = (String) comboBoxes[i].getSelectedItem();
                if(selectedCategory.equalsIgnoreCase("AUTO_INCREMENT")) continue;

                if(selectedCategory.equalsIgnoreCase("Foreign key")){
                    List<CustomTable> foreignTableKeyList = table.getForeignKeys();
                    for(CustomTable t : foreignTableKeyList){
                        List<String> values = new ArrayList<>();
                        int maxValue = t.getPrimaryKeySize();
                        System.out.println("Max value: "+maxValue);
                        for(int j=0; j<recordCount; j++){
                            values.add(generateFakeData(f,selectedCategory,maxValue));
                        }
                        table.addGeneratedData(t.getPrimaryKey(),values);
                    }
                }else{
                    List<String> values = new ArrayList<>();
                    for(int j=0; j<recordCount; j++){
                        values.add(generateFakeData(f,selectedCategory,0));
                    }
                    table.addGeneratedData(attribute, values);
                }
            }
            dialog.dispose();
            JOptionPane.showMessageDialog(null, "Dane zostały wygenerowane","Sukces", JOptionPane.INFORMATION_MESSAGE);
        });

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showGeneratedData(CustomTable table){
        System.out.println(table.getTableRowList());
        for(TableRow r : table.getTableRowList()){
            System.out.println(r.getName());
        }
        table.displayGeneratedData();
        System.out.println("Tabela "+table.gettableName());
        System.out.println("Klucz główny: "+table.getPrimaryKey());
        System.out.println("Klucze obce: "+table.getForeignKeys());

        if(table.hasGeneretedData()){
            JOptionPane.showMessageDialog(null, "Brak wygenerowanych danych dla tabeli "+table.gettableName(),"Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, List<String>> attributeData = table.getGeneratedData();
        String columnNames[] = attributeData.keySet().toArray(new String[0]);
        int rowCount = attributeData.values().iterator().next().size();

        String tableData[][] = new String[rowCount][columnNames.length];

        for(int i=0; i<rowCount; i++){
            int col=0;
            for(String attribute : columnNames){
                tableData[i][col] = attributeData.get(attribute).get(i);
                col++;
            }
        }

        JTable dataTable = new JTable(tableData, columnNames);
        JScrollPane scrollPane = new JScrollPane(dataTable);

        JDialog dialog = new JDialog();
        dialog.setTitle("Wygenerowane dane");
        dialog.setSize(600,400);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane,BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private static String generateFakeData(Faker f, String category, int max){
        return switch(category){
            case "Name" -> f.name().firstName();
            case "Last Name" -> f.name().lastName();
            case "Address"-> f.address().fullAddress();
            case "Email" -> f.internet().emailAddress();
            case "Phone Number" -> f.phoneNumber().cellPhone();
            case "Beer" -> f.beer().name();
            case "Food" -> f.food().dish();
            case "Hobby" -> f.hobby().activity();
            case "Cat" -> f.cat().name();
            case "CryptoCoin" -> f.cryptoCoin().coin();
            case "Dessert" -> f.dessert().flavor();
            case "Number" -> String.valueOf(f.number().numberBetween(0,100));
            case "Company" -> f.company().name();
            case "Date" -> f.timeAndDate().birthday(1,60,"YYY-MM-dd").toString();
            case "Lorem ipsum" -> f.lorem().sentence();
            case "Animal" -> f.animal().name();
            case "Job" -> f.job().title();
            case "Music" -> f.music().genre();
            case "Programming language" -> f.programmingLanguage().name();
            case "Color" -> f.color().name();
            case "Credit Card Number" -> f.business().creditCardNumber();
            case "Book" -> f.book().title();
            case "Artist" -> f.artist().name();
            case "App" -> f.app().name();
            case "Currency" -> f.money().currency();
            case "University" -> f.university().name();
            case "Foreign Key" -> String.valueOf(f.number().numberBetween(1,max+1));
            default -> throw new IllegalArgumentException("Brak kategorii: "+category);
        };
    }
}

