package at.stefan_huber.tunneltool.ui.tools;

/**
 * @author Stefan Huber
 */
public class FileNameReplacer {

    public static final String PLACEHOLDER = "%FILENAME%";
    public static final String PUTTY_PLACEHOLDER = "%PUTTYFILE%";

    public static String placeFileName(String scriptToReplace, String fileName) {
        if(scriptToReplace == null){
            return null;
        }

        return scriptToReplace.replace(PLACEHOLDER, fileName);
    }

    public static String placePuttyFile(String scriptToReplace, String fileName) {
        if(scriptToReplace == null){
            return null;
        }

        return scriptToReplace.replace(PUTTY_PLACEHOLDER, fileName);
    }

}
