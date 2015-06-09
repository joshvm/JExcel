package jvm.jexcel.app.ui.console;

import res.Res;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Message {

    public enum Type{
        APPLICATION(Res.EXCEL_16, -1),
        SCRIPT(Res.SCRIPT_16, -1),
        INFO(Res.INFO_16, JOptionPane.INFORMATION_MESSAGE),
        ERROR(Res.ERROR_16, JOptionPane.ERROR_MESSAGE);

        private final ImageIcon icon;
        private final int dialogId;

        private Type(final ImageIcon icon, final int dialogId){
            this.icon = icon;
            this.dialogId = dialogId;
        }

        public ImageIcon getIcon(){
            return icon;
        }

        public int getDialogId(){
            return dialogId;
        }
    }

    private final Type type;
    private final String text;

    private Message(final Type type, final String text){
        this.type = type;
        this.text = text;
    }

    public Type getType(){
        return type;
    }

    public String getText(){
        return text;
    }

    public static Message info(final String fmt, final Object... args){
        return new Message(Type.INFO, String.format(fmt, args));
    }

    public static Message app(final String fmt, final Object... args){
        return new Message(Type.APPLICATION, String.format(fmt, args));
    }

    public static Message script(final String fmt, final Object... args){
        return new Message(Type.SCRIPT, String.format(fmt, args));
    }

    public static Message error(final Exception ex){
        return new Message(Type.ERROR, ex.toString());
    }
}
