package view;

import javax.swing.*;
import java.awt.*;

/**
 * AdvicePanel: renders the advice text on the right side.
 */
public class AdvicePanel extends JPanel {

    private final JTextArea textArea = new JTextArea(12, 24);
    private final JButton outfitButton = new JButton("Get Outfit Suggestion");

    public AdvicePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Advice"));

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(textArea);
        add(sp, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(outfitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setOutfitButtonAction(Runnable action) {
        outfitButton.addActionListener(e -> action.run());
    }

    public void setAdviceText(String text) {
        textArea.setText(text != null ? text : "");
        textArea.setCaretPosition(0);
    }

    public void clear() {
        setAdviceText("");
    }
}
