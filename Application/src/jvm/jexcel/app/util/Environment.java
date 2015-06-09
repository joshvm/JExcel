package jvm.jexcel.app.util;

import java.io.File;
import java.io.FileFilter;

public final class Environment {

    private static final FileFilter JAR_FILTER = new FileFilter(){
        public boolean accept(final File f){
            return f.isFile() && f.getName().endsWith(".jar");
        }
    };

    private static final File SCRIPTS_DIR = new File("Scripts");
    private static final File SETTINGS_FILE = new File("settings.xml");

    static{
        dir(SCRIPTS_DIR);
    }

    private Environment(){}

    private static void dir(final File f){
        if(!f.exists())
            f.mkdir();
    }

    public static File getScriptsDir(){
        return SCRIPTS_DIR;
    }

    public static File[] getScriptJarFiles(){
        return SCRIPTS_DIR.listFiles(JAR_FILTER);
    }

    public static File getSettingsFile(){
        return SETTINGS_FILE;
    }
}
