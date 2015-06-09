package jvm.jexcel.app.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public final class Settings {

    private static final Properties PROPS = new Properties();

    private static final String DEFAULT_IP = "192.168.1.249";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_SAVE_CHANGES = "false";
    private static final String DEFAULT_WORKBOOK_DIR = "";

    public static final String IP_KEY = "ip";
    public static final String PORT_KEY = "port";
    public static final String SAVE_CHANGES_KEY = "modifyIn";
    public static final String WORKBOOK_DIR_KEY = "workbookDir";

    static{
        if(Environment.getSettingsFile().exists()){
            try(final InputStream in = new FileInputStream(Environment.getSettingsFile())){
                PROPS.loadFromXML(in);
            }catch(Exception ex){
                Utils.err(ex, true);
            }
        }
    }

    private Settings(){}

    public static boolean save(){
        try(final OutputStream out = new FileOutputStream(Environment.getSettingsFile())){
            PROPS.storeToXML(out, null);
            return true;
        }catch(Exception ex){
            Utils.err(ex, false);
            return false;
        }
    }

    public static boolean set(final String key, final Object value){
        final String old = PROPS.getProperty(key);
        PROPS.setProperty(key, value.toString());
        final boolean save = save();
        if(!save)
            PROPS.setProperty(key, old);
        return save;
    }

    public static String getIp(){
        return PROPS.getProperty(IP_KEY, DEFAULT_IP);
    }

    public static int getPort(){
        return Integer.parseInt(PROPS.getProperty(PORT_KEY, DEFAULT_PORT));
    }

    public static String getBrowseUrl(){
        return String.format("http://%s:%d/jexcel/", getIp(), getPort());
    }

    public static boolean getSaveChanges(){
        return Boolean.parseBoolean(PROPS.getProperty(SAVE_CHANGES_KEY, DEFAULT_SAVE_CHANGES));
    }
    
    public static String getWorkbookDir(){
        return PROPS.getProperty(WORKBOOK_DIR_KEY, DEFAULT_WORKBOOK_DIR);
    }
}

