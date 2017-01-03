package at.stefan_huber.tunneltool.ui.tools;

import at.stefan_huber.tunneltool.ui.config.PropertiesHandler;

import java.util.function.Consumer;

/**
 * @author Stefan Huber
 */
public class DatabaseOpener {

    public static void doOpenDatabase(Object dbName, Consumer<String> logging) throws Exception {
        if (dbName == null) {
            throw new UnsupportedOperationException("No Database was selected");
        }

        PropertiesHandler props = PropertiesHandler.getInstance();
        String scriptToRun = props.getDatabaseScript(dbName.toString());
        String sqlDevPath = props.getSqlDeveloperPath();

        if (sqlDevPath != null && !sqlDevPath.trim().isEmpty()) {
            scriptToRun = scriptToRun + "\nstart " + sqlDevPath;
        }

        CmdExecutor.executeCmd(logging, scriptToRun);
    }

}
