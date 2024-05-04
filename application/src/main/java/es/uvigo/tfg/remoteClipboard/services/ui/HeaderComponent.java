package es.uvigo.tfg.remoteClipboard.services.ui;

import javax.swing.*;
import javax.swing.text.ComponentView;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderComponent extends JComponent {
    private JButton leftArrow;
    private JButton rightArrow;
    private JPanel panel;

    public HeaderComponent(ActionListener listener, String userName){
        JLabel user = new JLabel(userName);
        user.setFont(new Font("Consolas", Font.PLAIN,20));
        user.setHorizontalAlignment(JLabel.CENTER);

        leftArrow = new JButton();
        leftArrow.setFocusable(false);
        leftArrow.setIcon(new ImageIcon("./icons/leftArrow.png"));
        leftArrow.addActionListener(listener);
        //leftArrow.setHorizontalAlignment(JButton.LEFT);

        rightArrow = new JButton();
        rightArrow.setFocusable(false);
        rightArrow.setIcon(new ImageIcon("./icons/rightArrow.png"));
        rightArrow.addActionListener(listener);
        //rightArrow.setHorizontalAlignment(JButton.RIGHT);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(leftArrow, BorderLayout.WEST);
        panel.add(rightArrow, BorderLayout.EAST);
        panel.add(user, BorderLayout.CENTER);
    }

    public JPanel getPanel(){
        return panel;
    }

    public JButton getLeftArrow(){
        return leftArrow;
    }
    public JButton getRightArrow(){
        return rightArrow;
    }
}
