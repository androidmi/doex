
package com.doex.demo.database.model;

public class LabelModel {

    private String number;
    private int label;
    private int count;

    private LabelModel() {
    };

    public String getNumber() {
        return number;
    }

    public int getLabel() {
        return label;
    }

    public int getCount() {
        return count;
    }

    public static LabelModel create(String line) {
        String[] data = line.split(",");
        LabelModel model = new LabelModel();
        model.number = data[0];
        model.label = Integer.valueOf(data[1]);
        model.count = Integer.valueOf(data[2]);
        return model;

    }
}
