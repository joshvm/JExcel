package jvm.jexcel.app.ui.browse;

import jvm.jexcel.app.actions.Actions;
import jvm.jexcel.app.core.script.RemoteScriptDetails;
import jvm.jexcel.app.core.script.ScriptDetailsManager;
import jvm.jexcel.app.ui.misc.Button;
import jvm.jexcel.app.util.Utils;
import res.Res;

import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar implements ActionListener{

    private final Button refreshButton;
    private final Button downloadButton;
    private final Button uploadButton;

    public ToolBar(){
        setFloatable(false);

        refreshButton = new Button("Refresh", Res.REFRESH_32, this);

        downloadButton = new Button("Download", Res.DOWNLOAD_32, this);

        uploadButton = new Button("Upload", Res.UPLOAD_32, this);

        add(refreshButton);
        add(downloadButton);
        add(uploadButton);

        refreshButton.doClick();
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(refreshButton)){
            BrowseWindow.getRemoteScriptsTree().clear();
            new Thread(
                    new Runnable(){
                        public void run(){
                            ScriptDetailsManager.reloadRemote();
                            for(final RemoteScriptDetails sd : ScriptDetailsManager.getRemote())
                                BrowseWindow.getRemoteScriptsTree().add(sd);
                        }
                    }
            ).start();
        }else if(source.equals(downloadButton)){
            final RemoteScriptDetails sd = BrowseWindow.getRemoteScriptsTree().getMaxSelected();
            if(sd != null){
                Actions.downloadScript(sd);
            }else{
                Utils.infoMsg("Select a script to download");
            }
        }else if(source.equals(uploadButton)){
            UploadForm.open();
        }
    }
}
