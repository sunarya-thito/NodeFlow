package thito.nodeflow.internal.settings.application;

import javafx.beans.property.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.internal.settings.canvas.*;

import java.io.*;

@Category(value = "${settings.general.name}", context = SettingsContext.GLOBAL)
public class General extends SettingsCanvas {

    @Item("${settings.general.items.workspace-directory}")
    public final ObjectProperty<File> workspaceDirectory = new SimpleObjectProperty<>(new File(NodeFlow.ROOT, "Workspace"));

    @Item("${settings.general.items.action-buffer}") @NumberItem(min = 0, max = 150)
    public final ObjectProperty<Integer> actionBuffer = new SimpleObjectProperty<>(100);

}