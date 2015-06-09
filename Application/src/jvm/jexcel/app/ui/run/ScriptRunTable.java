package jvm.jexcel.app.ui.run;

import jvm.jexcel.app.actions.Actions;
import jvm.jexcel.app.core.run.ScriptRun;
import res.Res;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScriptRunTable extends JPanel {

    private final ScriptRunTableModel model;
    private final JTable table;

    public ScriptRunTable(){
        super(new BorderLayout());

        model = new ScriptRunTableModel();

        final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean s, final boolean f, final int r, final int c){
                final Component comp = super.getTableCellRendererComponent(table, o, s, f, r, c);
                if(o == null || r < 0 || r > model.size()-1)
                    return comp;
                final ScriptRun run = model.get(r);
                final ScriptRunTableModel.Column col = ScriptRunTableModel.Column.VALUES[c];
                final JLabel label = (JLabel) comp;
                label.setIcon(col.getIcon());
                label.setHorizontalAlignment(JLabel.CENTER);
                switch(col){
                    case SCRIPT:
                        label.setToolTipText(run.getConfig().getDetails().toString());
                        break;
                    case IN:
                        label.setToolTipText(run.getConfig().getInFile().getPath());
                        break;
                    case OUT:
                        if(run.getConfig().getOutFile() != null)
                            label.setToolTipText(run.getConfig().getOutFile().getPath());
                        break;
                }
                return label;
            }
        };

        table = new JTable(model);
        table.getColumnModel().getColumn(ScriptRunTableModel.Column.STATE.ordinal()).setMaxWidth(110);
        table.getColumnModel().getColumn(ScriptRunTableModel.Column.STATE.ordinal()).setMinWidth(110);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(String.class, renderer);
        table.addMouseListener(
                new MouseAdapter(){
                    public void mousePressed(final MouseEvent e){
                        final int r = table.rowAtPoint(e.getPoint());
                        if(r < 0)
                            return;
                        final ScriptRun run = model.get(r);
                        SwingUtilities.invokeLater(
                                new Runnable(){
                                    public void run(){
                                        final JPopupMenu menu = new JPopupMenu();
                                        final JMenuItem title = new JMenuItem(String.format("%s @ %s", run.getConfig().getDetails(), run.getConfig().getInFile().getName()), Res.SCRIPT_16);
                                        menu.add(title);
                                        menu.addSeparator();
                                        final JMenuItem stopItem = new JMenuItem("Stop", Res.STOP_16);
                                        stopItem.addActionListener(
                                                new ActionListener(){
                                                    public void actionPerformed(final ActionEvent e){
                                                        Actions.stopScript(run);
                                                    }
                                                }
                                        );
                                        menu.add(stopItem);
                                        menu.show(table, e.getX(), e.getY());
                                    }
                                }
                        );
                    }
                }
        );

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public ScriptRun getSelected(){
        final int row = table.getSelectedRow();
        return row < 0 ? null : model.get(row);
    }

    public void add(final ScriptRun run){
        model.add(run);
        refresh();
    }

    public void remove(final ScriptRun run){
        model.remove(run);
        refresh();
    }

    public void clear(){
        model.clear();
        refresh();
    }

    public void updated(final ScriptRun run, final ScriptRunTableModel.Column col){
        model.updated(run, col);
        refresh();
    }

    private void refresh(){
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run(){
                        table.repaint();
                    }
                }
        );
    }
}
