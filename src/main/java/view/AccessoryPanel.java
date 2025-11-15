package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoryPanel extends JPanel {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    public AccessoryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Accessories"));

        list.setVisibleRowCount(8);
        JScrollPane sp = new JScrollPane(list);
        add(sp, BorderLayout.CENTER);
    }

    public void setAccessories(List<String> accessories) {
        model.clear();
        if (accessories == null || accessories.isEmpty()) return;
        for (String a : accessories) model.addElement(a);
    }

    public void clear() {
        model.clear();
    }
}
