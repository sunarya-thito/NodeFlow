package thito.nodeflow.plugin.base;

import thito.nodeflow.engine.node.*;
import thito.nodeflow.config.*;
import thito.nodeflow.settings.SettingsParser;

import java.util.*;

public class LinkStyleParser implements SettingsParser<LinkStyle> {

    private final Map<String, LinkStyle> linkStyleMap = new HashMap<>();
    public LinkStyleParser() {
        linkStyleMap.put("LINE", LinkStyle.LINE);
        linkStyleMap.put("CABLE", LinkStyle.CABLE);
        linkStyleMap.put("PIPE", LinkStyle.PIPE);
        linkStyleMap.put("PATH", LinkStyle.PATH);
    }

    @Override
    public Optional<LinkStyle> fromConfig(Section source, String key) {
        return source.getString(new Path(key)).map(x -> linkStyleMap.getOrDefault(x, LinkStyle.CABLE));
    }

    @Override
    public void toConfig(Section source, String key, LinkStyle value) {
        if (value == LinkStyle.LINE) {
            source.set(new Path(key), "LINE");
            return;
        }
        if (value == LinkStyle.PIPE) {
            source.set(new Path(key), "PIPE");
            return;
        }
        if (value == LinkStyle.PATH) {
            source.set(new Path(key), "PATH");
            return;
        }
        source.set(new Path(key), "CABLE");
    }
}
