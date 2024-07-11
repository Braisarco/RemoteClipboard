package es.uvigo.tfg.remoteClipboard.services.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import es.uvigo.tfg.remoteClipboard.services.AppManager;

public class UIApplication extends JFrame implements ActionListener {
  private String[] clipboardContent = { "Hola", "Soy", "Pepe", "El", "Pato" };
  private List<String> users;
  private int currentUserIndex;
  private HeaderComponent header;
  private AppManager manager;
  private JButton leftArrow;
  private JButton rightArrow;

  public UIApplication(AppManager manager) {
    this.manager = manager;
    currentUserIndex = 0;

    // PREPARES WINDOW
    header = new HeaderComponent(this, users.get(currentUserIndex));
    DefaultListModel<String> listModel = new DefaultListModel<>();
    for (String item : users) {
      listModel.addElement(item);
    }
    JList<String> list = new JList<>(listModel);
    JScrollPane scrollPane = new JScrollPane(list);

    this.setSize(400, 400);
    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    this.setResizable(false);
    this.add(header.getPanel(), BorderLayout.NORTH);
    this.add(scrollPane, BorderLayout.CENTER);
    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == header.getLeftArrow()) {
      if (currentUserIndex == 0) {
        currentUserIndex = users.size() - 1;
      } else {
        currentUserIndex--;
      }
    } else if (e.getSource() == header.getRightArrow()) {
      if (currentUserIndex == users.size() - 1) {
        currentUserIndex = 0;
      } else {
        currentUserIndex++;
      }
    }
  }
}
