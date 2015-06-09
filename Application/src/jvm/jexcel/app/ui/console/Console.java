package jvm.jexcel.app.ui.console;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;

public class Console extends JPanel {

    private final DefaultListModel<Message> model;
    private final JList<Message> list;

    public Console(){
        super(new BorderLayout());

        model = new DefaultListModel<>();

        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(
                new DefaultListCellRenderer(){
                    public Component getListCellRendererComponent(final JList list, final Object o, final int i, final boolean s, final boolean f){
                        final Component comp = super.getListCellRendererComponent(list, o, i, s, f);
                        if(o == null)
                            return comp;
                        final Message msg = (Message) o;
                        final JLabel label = (JLabel) comp;
                        label.setIcon(msg.getType().getIcon());
                        label.setText(msg.getText());
                        label.setToolTipText(msg.getText());
                        return label;
                    }
                }
        );

        add(Message.app("Welcome to JExcel"));

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void add(final Message msg){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        model.addElement(msg);
                        list.ensureIndexIsVisible(model.size()-1);
                        list.repaint();
                    }
                }
        );
    }

    private void refresh(){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        list.repaint();
                    }
                }
        );
    }
}
