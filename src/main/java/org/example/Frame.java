package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Frame extends JFrame {
    private final ArrayList<CustomTable> tables;
    private final Color defaultBgColor = Color.decode("#353b48");
    private final ConnectionPanel connectionPanel;
    private final JPanel contentPanel;
    private JLabel instructionLabel;

    public Frame(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }
        setTitle("SQL TABLE GENERATOR");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setSize(1100,600);
        setVisible(true);
        tables = new ArrayList<>();

        contentPanel = new JPanel(null);
        contentPanel.setBackground(defaultBgColor);
        connectionPanel = new ConnectionPanel(this);
        connectionPanel.setBackground(Color.GRAY);
        connectionPanel.setBounds(0,0,getWidth(),getHeight());

        instructionLabel = new JLabel("Kliknij PPM aby utworzyć tabelę", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("DejaVu", Font.BOLD, 20));
        instructionLabel.setForeground(Color.LIGHT_GRAY);
        instructionLabel.setBounds(0,0,getWidth(),getHeight());
        instructionLabel.setOpaque(false);
        instructionLabel.setVisible(true);

        contentPanel.add(instructionLabel,BorderLayout.CENTER);

        setContentPane(contentPanel);
        contentPanel.add(connectionPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                int labelWidth = instructionLabel.getPreferredSize().width;
                int labelHeight = instructionLabel.getPreferredSize().height;
                instructionLabel.setBounds((size.width-labelWidth)/2, (size.height-labelHeight)/2, labelWidth, labelHeight);
            }
        });

        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem createTable= new JMenuItem("Dodaj tabele");
                    JMenuItem saveProject = new JMenuItem("Zapisz projekt");

                    createTable.addActionListener(create -> CreateTableDialog.showDialog(Frame.this));
                    saveProject.addActionListener(save -> SaveData.SaveDataToYMLFile(Frame.this));

                    menu.add(createTable);
                    menu.add(saveProject);
                    menu.show(Frame.this,e.getX(),e.getY());
                }
            }
        });
    }
    public void addTable(CustomTable table){
        tables.add(table);
        contentPanel.add(table);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    public void removeTable(CustomTable table){
        tables.remove(table);
        contentPanel.remove(table);
        contentPanel.revalidate();
        contentPanel.repaint();
        updateInstructionLabel();
    }
    public void updateInstructionLabel(){
        instructionLabel.setVisible(tables.isEmpty());
    }
    public JPanel getContentPanel() {
        return contentPanel;
    }

    public ArrayList<CustomTable> getTableList() {
        return tables;
    }

    public void repaintConnections(){
        connectionPanel.repaintConnections();
    }
}

