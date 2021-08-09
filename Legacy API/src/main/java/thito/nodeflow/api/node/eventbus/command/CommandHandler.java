package thito.nodeflow.api.node.eventbus.command;

import thito.reflectedbytecode.*;

import java.util.*;

public interface CommandHandler {
    CommandVariable getSender(CommandNodeProvider provider);
    Reference hasPermission(Reference sender, String permission);
    void sendMessageToSender(Reference sender, Object message);
    List<Class<?>> getSenderTypes();
}
