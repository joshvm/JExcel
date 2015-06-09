package jvm.jexcel.app.core.script;

import jvm.jexcel.app.util.Environment;
import jvm.jexcel.app.util.Settings;
import jvm.jexcel.app.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class RemoteScriptDetails extends ScriptDetails{

    private static final int TIMEOUT = 60000;
    private static final String USER_AGENT = "jexcelscripts";

    private final String hash;

    public RemoteScriptDetails(final ScriptDetails details, final String hash){
        super(details);
        this.hash = hash;
    }

    public String getHash(){
        return hash;
    }

    public boolean download(){
        try{
            final File outFile = new File(Environment.getScriptsDir(), hash + ".jar");
            final URLConnection con = new URL(Settings.getBrowseUrl() + "scripts/" + hash + ".jar").openConnection();
            con.setReadTimeout(TIMEOUT);
            con.setConnectTimeout(TIMEOUT);
            con.setRequestProperty("User-Agent", USER_AGENT);
            try(final InputStream in = con.getInputStream()){
                final byte[] buffer = new byte[1024];
                try(final OutputStream out = new FileOutputStream(outFile)){
                    int index;
                    while((index = in.read(buffer)) > 0)
                        out.write(buffer, 0, index);
                }
            }
            return outFile.exists();
        }catch(Exception ex){
            Utils.err(ex, false);
            return false;
        }
    }
}
