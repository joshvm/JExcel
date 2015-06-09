package jvm.jexcel.app.core.run;

import jvm.jexcel.api.Script;
import jvm.jexcel.app.core.script.LocalScriptDetails;

import java.io.File;

public class ScriptRunConfig {

    private final LocalScriptDetails details;
    private final File inFile;
    private final boolean saveChanges;
    private final File outFile;
    private final Script.Args args;

    public ScriptRunConfig(final LocalScriptDetails details, final File inFile, final boolean saveChanges, final File outFile){
        this.details = details;
        this.inFile = inFile;
        this.saveChanges = saveChanges;
        this.outFile = outFile;
        args = null;
    }

    public LocalScriptDetails getDetails(){
        return details;
    }

    public File getInFile(){
        return inFile;
    }

    public boolean isSaveChanges(){
        return saveChanges;
    }

    public File getOutFile(){
        return outFile;
    }

    public Script.Args getArgs(){
        return args;
    }
}
