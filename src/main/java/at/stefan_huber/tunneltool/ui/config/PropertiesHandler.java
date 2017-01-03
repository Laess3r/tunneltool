package at.stefan_huber.tunneltool.ui.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Stefan Huber
 */
public class PropertiesHandler {

    private static final String KEY_DATABASES = "databases";
    private static final String KEY_SQL_DEV_PATH = "sqldeveloperpath";

    private static final String KEY_SCP_SCRIPTS = "scpscripts";

    private static final String FILE_NAME = "tunneltool.properties";

    private static PropertiesHandler INSTANCE;

    // contains: dbName and cmd Script
    private Map<String, String> databases;
    private String sqlDeveloperPath;

    private Map<String, String> scpScripts;

    private PropertiesHandler() {
        refreshProperties();
    }

    public void refreshProperties() {
        databases = new HashMap<>();
        scpScripts = new HashMap<>();
        readPropertyFile();
    }

    private void readPropertyFile() {
        Properties prop = new Properties();
        InputStream input = null;
        try {

            try {
                input = new FileInputStream(FILE_NAME);
                prop.load(input);
            }
            catch (IOException e) {
                // do nothing
            }

            databases = MapHelper.parseMap(prop.getProperty(KEY_DATABASES));
            scpScripts = MapHelper.parseMap(prop.getProperty(KEY_SCP_SCRIPTS));
            sqlDeveloperPath = prop.getProperty(KEY_SQL_DEV_PATH);
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void writePropertyFile() throws IOException {
        Properties prop = new Properties();
        OutputStream output = null;
        try {

            output = new FileOutputStream(FILE_NAME);

            prop.setProperty(KEY_DATABASES, MapHelper.formatMap(databases));
            prop.setProperty(KEY_SCP_SCRIPTS, MapHelper.formatMap(scpScripts));

            prop.setProperty(KEY_SQL_DEV_PATH, sqlDeveloperPath);

            // save properties to project root folder
            prop.store(output, null);

        }
        finally {
            if (output != null) {
                try {
                    output.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            readPropertyFile();
        }
    }

    public static PropertiesHandler getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new PropertiesHandler();
        }
        return INSTANCE;
    }

    public List<String> getDatabases() {
        return new ArrayList<>(databases.keySet());
    }

    public Map<String, String> getDataBasesMap() {
        return databases;
    }

    public String getDatabaseScript(String databaseName) {
        return databases.get(databaseName);
    }

    public Map<String, String> getScpScriptsMap() {
        return scpScripts;
    }

    public String getScpScript(String scpScriptName) {
        return scpScripts.get(scpScriptName);
    }

    public String getSqlDeveloperPath() {
        return sqlDeveloperPath;
    }

    public void setSqlDeveloperPath(String sqlDeveloperPath) {
        this.sqlDeveloperPath = sqlDeveloperPath;
    }

    public List<String> getScpScripts() {
        return new ArrayList<>(scpScripts.keySet());
    }
}
