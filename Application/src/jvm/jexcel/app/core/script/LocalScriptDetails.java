package jvm.jexcel.app.core.script;

import jvm.jexcel.api.Script;
import jvm.jexcel.app.util.Settings;
import jvm.jexcel.app.util.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;

public class LocalScriptDetails extends ScriptDetails{

    private final File file;
    private final Class<? extends Script> clazz;

    public LocalScriptDetails(final ScriptDetails details, final File file, final Class<? extends Script> clazz){
        super(details);
        this.file = file;
        this.clazz = clazz;
    }

    public File getFile(){
        return file;
    }

    public int upload(){
        try{
            final CloseableHttpClient client = HttpClients.createDefault();
            final HttpPost post = new HttpPost(Settings.getBrowseUrl() + "upload.php");
            final HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("script_jar", new FileBody(file))
                    .addPart("name", new StringBody(name, ContentType.TEXT_PLAIN))
                    .addPart("desc", new StringBody(desc, ContentType.TEXT_PLAIN))
                    .addPart("version", new StringBody(Double.toString(version), ContentType.TEXT_PLAIN))
                    .addPart("author", new StringBody(author, ContentType.TEXT_PLAIN))
                    .build();
            post.setEntity(entity);
            final String response = EntityUtils.toString(client.execute(post).getEntity());
            return Integer.parseInt(response);
        }catch(Exception ex){
            Utils.err(ex, false);
            return -1;
        }
    }

    public Script createScript(){
        try{
            return clazz.newInstance();
        }catch(Exception ex){
            Utils.err(ex, false);
            return null;
        }
    }
}
