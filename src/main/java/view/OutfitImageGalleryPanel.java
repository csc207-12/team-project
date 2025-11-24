package view;

import entity.User;
import interface_adapter.outfit_image_generation.OutfitImageGenerationController;
import interface_adapter.outfit_image_generation.OutfitImageGenerationPresenter;
import interface_adapter.outfit_image_generation.OutfitImageGenerationView;
import use_case.outfit_image_generation.OutfitImageGenerationInputBoundary;
import use_case.outfit_image_generation.OutfitImageGenerationInteractor;
import data_access.outfit_image_generation.OutfitImageGenerationDataAccessObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;

public class OutfitImageGalleryPanel extends JFrame implements OutfitImageGenerationView {

    private final OutfitImageGenerationController controller;
    private final List<String> outfits;

    private final JPanel imageGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
    private final JButton generateButton = new JButton("Generate Outfit Images");

    private final JLabel statusLabel = new JLabel(" ");

    public OutfitImageGalleryPanel(User currentUser, List<String> outfits) {
        this.outfits = outfits;

        OutfitImageGenerationPresenter presenter = new OutfitImageGenerationPresenter(this);
        OutfitImageGenerationDataAccessObject dataAccess =
                new OutfitImageGenerationDataAccessObject(currentUser);
        OutfitImageGenerationInputBoundary interactor =
                new OutfitImageGenerationInteractor(dataAccess, presenter);
        this.controller = new OutfitImageGenerationController(interactor);

        setTitle("Outfit Image Gallery");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        setLayout(new BorderLayout(10, 10));

        generateButton.addActionListener(e -> requestImages());
        add(generateButton, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(imageGrid);
        add(scrollPane, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        setSize(420, 500);
        setLocationRelativeTo(null);
    }

    private void requestImages() {
        generateButton.setEnabled(false);
        generateButton.setText("Loading...");

        statusLabel.setText("Generating images...");
        statusLabel.revalidate();
        statusLabel.repaint();

        imageGrid.revalidate();
        imageGrid.repaint();


        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.generateImages(outfits);
                return null;
            }
        }.execute();
    }

    @Override
    public void onImageGenerationSuccess(List<String> base64Images) {
        SwingUtilities.invokeLater(() -> {
            generateButton.setEnabled(true);
            generateButton.setText("Generate Outfit Images");
            statusLabel.setText("Images loaded successfully!");

            imageGrid.removeAll();

            for (int i = 0; i < base64Images.size(); i++) {
                String base64 = base64Images.get(i);

                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                card.setPreferredSize(new Dimension(380, 380));

                JLabel title = new JLabel("Outfit " + (i + 1), JLabel.CENTER);
                title.setFont(new Font("Arial", Font.BOLD, 14));
                title.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(title);

                JLabel imgLabel = new JLabel("Loading image...", JLabel.CENTER);
                imgLabel.setPreferredSize(new Dimension(360, 320));
                imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(imgLabel);

                loadBase64ImageAsync(base64, imgLabel);

                imageGrid.add(card);
            }

            imageGrid.revalidate();
            imageGrid.repaint();
        });
    }

    private void loadBase64ImageAsync(String base64, JLabel targetLabel) {
        new SwingWorker<ImageIcon, Void>() {
            protected ImageIcon doInBackground() {
                try {
                    byte[] decoded = Base64.getDecoder().decode(base64);
                    ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
                    Image img = ImageIO.read(bais);
                    if (img == null) return null;

                    return new ImageIcon(img.getScaledInstance(360, -1, Image.SCALE_SMOOTH));
                } catch (Exception e) {
                    return null;
                }
            }

            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        targetLabel.setText("");
                        targetLabel.setIcon(icon);
                    } else {
                        targetLabel.setText("Failed to load image");
                        targetLabel.setForeground(Color.RED);
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    @Override
    public void onImageGenerationFailure(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            generateButton.setEnabled(true);
            generateButton.setText("Generate Outfit Images");
            statusLabel.setText("Failed to load images.");
            JOptionPane.showMessageDialog(this, errorMessage,
                    "Image Error", JOptionPane.ERROR_MESSAGE);
        });
    }
}
