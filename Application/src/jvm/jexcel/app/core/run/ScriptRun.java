package jvm.jexcel.app.core.run;

import jvm.jexcel.api.Script;
import jvm.jexcel.app.Application;
import jvm.jexcel.app.ui.run.ScriptRunTableModel;
import jvm.jexcel.app.util.Utils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ScriptRun extends Thread{

    public enum State{
        INIT("Initializing"),
        LOADING("Loading"),
        RUNNING("Running"),
        WRITING("Writing"),
        CLOSING("Closing"),
        FINISHED("Finished");

        private final String text;

        private State(final String text){
            this.text = text;
        }

        public String getText(){
            return text;
        }
    }

    private final ScriptRunConfig config;

    private State state;

    public ScriptRun(final ScriptRunConfig config){
        this.config = config;

        state = State.INIT;
    }

    public ScriptRunConfig getConfig(){
        return config;
    }

    public State getCurrentState(){
        return state;
    }

    public void setCurrentState(final State state){
        this.state = state;
        Application.getScriptRunTable().updated(this, ScriptRunTableModel.Column.STATE);
    }

    public void start(){
        setPriority(MAX_PRIORITY);
        super.start();
    }

    public void run(){
        try{
            final String title = String.format("[%s @ %s]", config.getDetails(), config.getInFile());
            final long start = System.currentTimeMillis();
            setCurrentState(State.LOADING);
            Utils.script("%s Loading workbook", title);
            final Workbook workbook = WorkbookFactory.create(new FileInputStream(config.getInFile()));
            Utils.script("%s Creating script", title);
            final Script script = config.getDetails().createScript();
            setCurrentState(State.RUNNING);
            Utils.script("%s Running script", title);
            script.run(workbook, config.getArgs());
            if(config.getOutFile() != null){
                setCurrentState(State.WRITING);
                Utils.script("%s Writing to %s", title, config.getOutFile());
                try(final FileOutputStream out = new FileOutputStream(config.getOutFile())){
                    workbook.write(out);
                }
            }
            setCurrentState(State.CLOSING);
            Utils.script("%s Closing", title);
            if(config.isSaveChanges()){
                try(final FileOutputStream os = new FileOutputStream(config.getInFile())){
                    workbook.write(os);
                }
                workbook.close();
            }else{
                if(workbook instanceof XSSFWorkbook)
                    ((XSSFWorkbook)workbook).getPackage().revert();
                else
                    workbook.close();
            }
            final long end = System.currentTimeMillis() - start;
            setCurrentState(State.FINISHED);
            Utils.script("%s Finished in %d ms", title, end);
            Application.getScriptRunTable().remove(this);
            if(config.isSaveChanges()){
                Utils.script("Opening %s", config.getInFile());
                Utils.open(config.getInFile());
            }else if(config.getOutFile() != null){
                Utils.script("Opening %s", config.getOutFile());
                Utils.open(config.getOutFile());
            }
        }catch(Exception ex){
            setCurrentState(State.FINISHED);
            if(ex instanceof InterruptedException){
                Utils.script("%s Stopped", String.format("[%s @ %s]", config.getDetails(), config.getInFile()));
            }else{
                Utils.err(ex, false);
                Utils.script("%s Finished with problems", String.format("[%s @ %s]", config.getDetails(), config.getInFile()));
            }
            Application.getScriptRunTable().remove(this);
        }
    }
}
