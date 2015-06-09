package jvm.jexcel.app.actions;

import jvm.jexcel.app.Application;
import jvm.jexcel.app.core.run.ScriptRun;
import jvm.jexcel.app.core.run.ScriptRunConfig;
import jvm.jexcel.app.core.script.LocalScriptDetails;
import jvm.jexcel.app.core.script.RemoteScriptDetails;
import jvm.jexcel.app.core.script.ScriptDetailsManager;
import jvm.jexcel.app.ui.console.Message;
import jvm.jexcel.app.ui.run.ScriptRunConfigDialog;
import jvm.jexcel.app.util.Utils;

import javax.swing.SwingUtilities;

public final class Actions {

    private Actions(){}

    public static void runScript(final String author, final String script, final double version){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        final ScriptRunConfig config = ScriptRunConfigDialog.open(author, script, version);
                        if(config == null)
                            return;
                        final ScriptRun run = new ScriptRun(config);
                        Application.getScriptRunTable().add(run);
                        run.start();
                    }
                }
        );
    }

    public static void runScript(final LocalScriptDetails sd){
        runScript(sd.getAuthor(), sd.getName(), sd.getVersion());
    }

    public static void stopScript(final ScriptRun run){
        run.interrupt();
    }

    public static void deleteScript(final LocalScriptDetails sd){
        if(sd.getFile().exists() && !sd.getFile().delete())
            return;
        Application.getLocalScriptsTree().remove(sd);
        ScriptDetailsManager.getLocal().remove(sd);
    }

    public static void downloadScript(final RemoteScriptDetails sd){
        new Thread(
                new Runnable(){
                    public void run(){
                        Utils.info("Downloading %s", sd);
                        final long start = System.currentTimeMillis();
                        if(sd.download())
                            Utils.msg(Message.info("Successfully downloaded %s in %d ms", sd, System.currentTimeMillis() - start), true);
                    }
                }
        ).start();
    }
}
