package jvm.jexcel.app.ui.run;

import jvm.jexcel.app.core.run.ScriptRun;
import res.Res;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ScriptRunTableModel extends AbstractTableModel{
    
    public enum Column{
        STATE("Status", null),
        SCRIPT("Script", Res.SCRIPT_16),
        IN("In", Res.EXCEL_FILE_16),
        OUT("Out", Res.EXCEL_FILE_16);
        
        public static final Column[] VALUES = values();
        
        private final String name;
        private final ImageIcon icon;
        
        private Column(final String name, final ImageIcon icon){
            this.name = name;
            this.icon = icon;
        }
        
        public String getName(){
            return name;
        }

        public ImageIcon getIcon(){
            return icon;
        }
    }
    
    private final List<ScriptRun> model;
    
    public ScriptRunTableModel(){
        model = new ArrayList<>();
    }

    public ScriptRun get(final int i){
        return model.get(i);
    }

    public void add(final ScriptRun run){
        model.add(run);
        final int i = model.size()-1;
        fireTableRowsInserted(i, i);
    }

    public void remove(final int i){
        model.remove(i);
        fireTableRowsDeleted(i, i);
    }

    public void remove(final ScriptRun run){
        final int i = model.indexOf(run);
        if(i != -1)
            remove(i);
    }

    public void clear(){
        final int i = model.size()-1;
        model.clear();
        if(i != -1)
            fireTableRowsDeleted(0, i);
    }

    public int getRowCount(){
        return model.size();
    }

    public int size(){
        return model.size();
    }

    public void updated(final ScriptRun run, final Column col){
        final int r = model.indexOf(run);
        if(r == -1)
            return;
        fireTableCellUpdated(r, col.ordinal());
    }

    public Object getValueAt(final int r, final int c){
        if(r < 0 || r > size()-1)
            return null;
        final ScriptRun sr = get(r);
        switch(Column.VALUES[c]){
            case STATE:
                return sr.getCurrentState();
            case SCRIPT:
                return sr.getConfig().getDetails().toString();
            case IN:
                return sr.getConfig().getInFile().getName();
            case OUT:
                return (sr.getConfig().getOutFile() != null ? sr.getConfig().getOutFile() : sr.getConfig().getInFile()).getName();
            default:
                return null;
        }
    }

    public int getColumnCount(){
        return Column.VALUES.length;
    }

    public String getColumnName(final int c){
        return Column.VALUES[c].name;
    }

    public Class<?> getColumnClass(final int c){
        return String.class;
    }
}
