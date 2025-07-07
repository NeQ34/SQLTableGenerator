package org.example;

public class TableConnection{
    private final CustomTable table1;
    private final CustomTable table2;

    public TableConnection(CustomTable table1, CustomTable table2) {
        this.table1 = table1;
        this.table2 = table2;
    }

    public CustomTable getTable1() {
        return table1;
    }

    public CustomTable getTable2() {
        return table2;
    }
}

