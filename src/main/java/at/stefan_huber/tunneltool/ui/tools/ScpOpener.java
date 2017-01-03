package at.stefan_huber.tunneltool.ui.tools;

import at.stefan_huber.tunneltool.ui.config.PropertiesHandler;

import java.util.function.Consumer;

/**
 * @author Stefan Huber
 */
public class ScpOpener {

    public static void doOpenScpScript(Object scpName, Consumer<String> logging) throws Exception {
        if (scpName == null) {
            throw new UnsupportedOperationException("No SCP Connection was selected");
        }

        PropertiesHandler props = PropertiesHandler.getInstance();
        String scriptToRun = props.getScpScript(scpName.toString());

        CmdExecutor.executeCmd(logging, scriptToRun);
    }

}
