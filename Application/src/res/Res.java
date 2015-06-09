package res;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.Arrays;
import java.util.List;

public final class Res {

    public static final ImageIcon ERROR_16 = icon("error", 16);
    public static final ImageIcon INFO_16 = icon("info", 16);

    public static final ImageIcon EXCEL_48 = icon("excel", 48);
    public static final ImageIcon EXCEL_32 = icon("excel", 32);
    public static final ImageIcon EXCEL_16 = icon("excel", 16);

    public static final ImageIcon REFRESH_32 = icon("refresh", 32);
    public static final ImageIcon REFRESH_16 = icon("refresh", 16);

    public static final ImageIcon RUN_32 = icon("run", 32);
    public static final ImageIcon RUN_16 = icon("run", 16);
    public static final ImageIcon STOP_32 = icon("stop", 32);
    public static final ImageIcon STOP_16 = icon("stop", 16);

    public static final ImageIcon BROWSE_32 = icon("browse", 32);

    public static final ImageIcon SETTINGS_32 = icon("settings", 32);

    public static final ImageIcon SCRIPT_16 = icon("script", 16);
    public static final ImageIcon USER_16 = icon("user", 16);

    public static final ImageIcon EXCEL_FILE_16 = icon("excel_file", 16);
    public static final ImageIcon FILE_BROWSE_16 = icon("file_browse", 16);

    public static final ImageIcon YES_16 = icon("yes", 16);
    public static final ImageIcon NO_16 = icon("no", 16);

    public static final ImageIcon DOWNLOAD_16 = icon("download", 16);
    public static final ImageIcon DOWNLOAD_32 = icon("download", 32);

    public static final ImageIcon SAVE_16 = icon("save", 16);

    public static final ImageIcon DELETE_32 = icon("delete", 32);
    public static final ImageIcon DELETE_16 = icon("delete", 16);

    public static final ImageIcon UPLOAD_32 = icon("upload", 32);
    public static final ImageIcon UPLOAD_16 = icon("upload", 16);
    public static final ImageIcon JAR_32 = icon("jar", 32);

    private Res(){}

    public static ImageIcon icon(final String name, final int size){
        return new ImageIcon(Res.class.getResource(String.format("img/%s_%d.png", name, size)));
    }

    public static List<Image> icons(){
        return Arrays.asList(
                Res.EXCEL_48.getImage(),
                Res.EXCEL_32.getImage(),
                Res.EXCEL_16.getImage()
        );
    }
}
