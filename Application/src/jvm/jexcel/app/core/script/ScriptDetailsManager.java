package jvm.jexcel.app.core.script;

import jvm.jexcel.api.Script;
import jvm.jexcel.app.util.Environment;
import jvm.jexcel.app.util.Settings;
import jvm.jexcel.app.util.Utils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ScriptDetailsManager {

    private static final String GET_PHP = "scripts.php";
    private static final int TIMEOUT = 60000;
    private static final String USER_AGENT = "jexcel";

    private static final Set<LocalScriptDetails> LOCAL = new HashSet<>();
    private static final Set<RemoteScriptDetails> REMOTE = new HashSet<>();

    private ScriptDetailsManager(){}

    public static Set<String> getAuthorNames(){
        final Set<String> result = new TreeSet<>();
        for(final LocalScriptDetails sd : LOCAL)
            result.add(sd.getAuthor());
        return result;
    }

    public static Set<String> getScriptNames(final String author){
        final Set<String> result = new TreeSet<>();
        for(final LocalScriptDetails sd : LOCAL)
            if(sd.getAuthor().equalsIgnoreCase(author))
                result.add(sd.getName());
        return result;
    }

    public static Set<Double> getVersions(final String author, final String name){
        final Set<Double> result = new TreeSet<>();
        for(final LocalScriptDetails sd : LOCAL)
            if(sd.getAuthor().equalsIgnoreCase(author) && sd.getName().equalsIgnoreCase(name))
                result.add(sd.getVersion());
        return result;
    }

    public static LocalScriptDetails getScript(final String author, final String script, final double version){
        for(final LocalScriptDetails sd : LOCAL)
            if(sd.getAuthor().equalsIgnoreCase(author) && sd.getName().equalsIgnoreCase(script) && sd.getVersion() == version)
                return sd;
        return null;
    }

    public static LocalScriptDetails parse(final File file){
        try{
            final ClassLoader cl = new URLClassLoader(new URL[]{file.toURI().toURL()});
            try(final JarFile jar = new JarFile(file)){
                final Enumeration<JarEntry> entries = jar.entries();
                while(entries.hasMoreElements()){
                    final JarEntry entry = entries.nextElement();
                    if(entry.isDirectory() || !entry.getName().endsWith(".class"))
                        continue;
                    final String name = entry.getName().replace(".class", "").replace('/', '.');
                    if(!name.contains("jexcel"))
                        continue;
                    try{
                        final Class<?> clazz = cl.loadClass(name);
                        if(!Script.class.isAssignableFrom(clazz))
                            continue;
                        final Script.Manifest manifest = clazz.getAnnotation(Script.Manifest.class);
                        if(manifest == null)
                            continue;
                        final ScriptDetails details = new ScriptDetails(manifest);
                        return new LocalScriptDetails(details, file, (Class<? extends Script>)clazz);
                    }catch(Exception ex){
                        Utils.err(ex, false);
                    }
                }
            }
        }catch(Exception ex){
            Utils.err(ex, false);
        }
        return null;
    }

    public static boolean loadLocal(){
        try{
            for(final File file : Environment.getScriptJarFiles()){
                final LocalScriptDetails sd = parse(file);
                if(sd != null)
                    LOCAL.add(sd);
            }
            return true;
        }catch(Exception ex){
            Utils.err(ex, true);
            return false;
        }
    }

    public static boolean reloadLocal(){
        LOCAL.clear();
        return loadLocal();
    }

    public static Set<LocalScriptDetails> getLocal(){
        return LOCAL;
    }

    public static boolean loadRemote(){
        final String host = Settings.getBrowseUrl();
        try{
            final URL url = new URL(host + GET_PHP);
            final URLConnection con = url.openConnection();
            con.setConnectTimeout(TIMEOUT);
            con.setReadTimeout(TIMEOUT);
            con.setRequestProperty("User-Agent", USER_AGENT);
            try(final JsonReader reader = Json.createReader(con.getInputStream())){
                final JsonArray array = reader.readArray();
                try{
                    for(final JsonValue value : array){
                        final JsonObject obj = (JsonObject) value;
                        final String name = obj.getString("name");
                        final String desc = obj.getString("desc");
                        final String author = obj.getString("author");
                        final double version = obj.getJsonNumber("version").doubleValue();
                        final String hash = obj.getString("hash");
                        final ScriptDetails sd = new ScriptDetails(name, desc, author, version);
                        final RemoteScriptDetails rsd = new RemoteScriptDetails(sd, hash);
                        REMOTE.add(rsd);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            return true;
        }catch(Exception ex){
            Utils.err(ex, true);
            return false;
        }
    }

    public static boolean reloadRemote(){
        REMOTE.clear();
        return loadRemote();
    }

    public static Set<RemoteScriptDetails> getRemote(){
        return REMOTE;
    }
}
