package jvm.jexcel.app;

import jvm.jexcel.app.actions.Actions;
import jvm.jexcel.app.core.run.ScriptRun;
import jvm.jexcel.app.core.script.LocalScriptDetails;
import jvm.jexcel.app.core.script.ScriptDetailsManager;
import jvm.jexcel.app.ui.browse.BrowseWindow;
import jvm.jexcel.app.ui.misc.Button;
import jvm.jexcel.app.ui.settings.SettingsDialog;
import jvm.jexcel.app.util.Settings;
import jvm.jexcel.app.util.Utils;
import res.Res;

import javax.swing.Box;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

public class ToolBar extends JToolBar implements ActionListener{

    private final Button browseButton;
    private final Button refreshButton;

    private final Button deleteButton;

    private final Button runButton;
    private final Button stopButton;

    private final Button apiButton;
    private final Button settingsButton;

    public ToolBar(){
        setFloatable(false);

        browseButton = new Button("Browse", Res.BROWSE_32, this);

        refreshButton = new Button("Refresh", Res.REFRESH_32, this);

        deleteButton = new Button("Delete", Res.DELETE_32, this);

        runButton = new Button("Run", Res.RUN_32, this);

        stopButton = new Button("Stop", Res.STOP_32, this);

        apiButton = new Button("API", Res.JAR_32, this);

        settingsButton = new Button("Settings", Res.SETTINGS_32, this);

        add(browseButton);
        add(refreshButton);
        //add(deleteButton);
        add(runButton);
        add(stopButton);
        add(Box.createHorizontalGlue());
        add(apiButton);
        add(settingsButton);

        refreshButton.doClick();
    }
    
    private void enable(final Button button, final boolean enabled){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        button.setEnabled(enabled);
                    }
                }
        );
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(browseButton)){
            BrowseWindow.open();
        }else if(source.equals(refreshButton)){
            enable(refreshButton, false);
            Application.getLocalScriptsTree().clear();
            new Thread(
                    new Runnable(){
                        public void run(){
                            ScriptDetailsManager.reloadLocal();
                            for(final LocalScriptDetails sd : ScriptDetailsManager.getLocal())
                                Application.getLocalScriptsTree().add(sd);
                            enable(refreshButton, true);
                        }
                    }
            ).start();
        }else if(source.equals(deleteButton)){
            final LocalScriptDetails details = Application.getLocalScriptsTree().getMaxSelected();
            if(details != null)
                Actions.deleteScript(details);
            else
                Utils.infoMsg("Select a script from the scripts table");
        }else if(source.equals(runButton)){
            final LocalScriptDetails details = Application.getLocalScriptsTree().getMaxSelected();
            if(details != null){
                Actions.runScript(details);
            }else{
                Actions.runScript(null, null, -1);
            }
        }else if(source.equals(stopButton)){
            final ScriptRun run = Application.getScriptRunTable().getSelected();
            if(run != null){
                Actions.stopScript(run);
            }else{
                Utils.infoMsg("Select a running script from the running scripts table");
            }
        }else if(source.equals(apiButton)){
            try{
                Desktop.getDesktop().browse(URI.create(Settings.getBrowseUrl() + "JExcel-API.jar"));
            }catch(Exception ex){
                Utils.err(ex, true);
            }
        }else if(source.equals(settingsButton)){
            SettingsDialog.open();
        }
    }
}
