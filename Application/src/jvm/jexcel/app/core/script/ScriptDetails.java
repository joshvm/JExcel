package jvm.jexcel.app.core.script;

import jvm.jexcel.api.Script;

public class ScriptDetails {

    protected final String name;
    protected final String desc;
    protected final String author;
    protected final double version;

    public ScriptDetails(final String name, final String desc, final String author, final double version){
        this.name = name;
        this.desc = desc;
        this.author = author;
        this.version = version;
    }

    public ScriptDetails(final Script.Manifest manifest){
        this(manifest.name(), manifest.desc(), manifest.author(), manifest.version());
    }

    public ScriptDetails(final ScriptDetails details){
        this(details.name, details.desc, details.author, details.version);
    }

    public String getName(){
        return name;
    }

    public String getDesc(){
        return desc;
    }

    public String getAuthor(){
        return author;
    }

    public double getVersion(){
        return version;
    }

    public boolean equals(final Object o){
        if(o == null)
            return false;
        if(o == this)
            return true;
        if(!(o instanceof ScriptDetails))
            return false;
        final ScriptDetails sd = (ScriptDetails) o;
        return name.equals(sd.name)
                && desc.equals(sd.desc)
                && author.equals(sd.author)
                && version == sd.version;
    }

    public String toString(){
        return String.format("%s's %s V%s", author, name, version);
    }
}
