package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TableRow extends JLabel{
    private final Color defaultAttributeBgColor = Color.decode("#3d3d3d");
    private final Color defaultForegroundColor = Color.decode("#f7f1e3");
    private final Font defaultFont = new Font("DefaVu",Font.BOLD, 20);
    private CustomTable customTable;
    private String name;
    private String attributes;

    public TableRow(CustomTable customTable, String name, String attributes){
        super(name+" - "+attributes);
        this.name = name;
        this.customTable = customTable;
        this.attributes = attributes;

        setOpaque(false);
        setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.BLACK));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height+11));
        setBackground(defaultAttributeBgColor);
        setForeground(defaultForegroundColor);
        setFont(defaultFont);

        customTable.getTablePanel().add(this);
        customTable.getTablePanel().revalidate();
        customTable.getTablePanel().repaint();

        addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e){}
            @Override
            public void mousePressed(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem removeAttributeItem = new JMenuItem("Usun atrybut");
                    removeAttributeItem.addActionListener(a -> removeTableRow());

                    menu.add(removeAttributeItem);
                    menu.show(TableRow.this,e.getX(),e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){}

            @Override
            public void mouseEntered(MouseEvent e){
                setBorder(BorderFactory.createMatteBorder(0,0,2,0, Color.CYAN));
            }

            @Override
            public void mouseExited(MouseEvent e){
                setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.BLACK));
            }
        });
    }

    @Override
    public String getName(){
        return name;
    }
    @Override
    public void setName(String name){
        this.name = name;
    }

    public String getAttributes(){
        return attributes;
    }

    public void setAttributes(String attributes){
        this.attributes = attributes;
    }
    public void removeTableRow(){
        customTable.getTablePanel().remove(this);
        customTable.getTablePanel().revalidate();
        customTable.getTablePanel().repaint();
        customTable.removeTableRow(this);
        customTable.getParent().repaint();
        customTable.getParent().revalidate();
        if(customTable.getPrimaryKey()!=null && customTable.getPrimaryKey().equals(this.getName())) customTable.setPrimaryKey(null);
    }
}

