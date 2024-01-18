package Domain.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Action {
    private final List<Consumer<Object[]>> eventHandlers = new ArrayList<>();
    private final Class<?>[] expectedArgumentTypes;

    public Action(Class<?>... expectedArgumentTypes) {
        this.expectedArgumentTypes = expectedArgumentTypes;
    }

    public void addEventHandler(Consumer<Object[]> eventHandler) {
        eventHandlers.add(eventHandler);
    }

    public void removeEventHandler(Consumer<Object[]> eventHandler) {
        eventHandlers.remove(eventHandler);
    }

    public void invoke(Object... arguments) {
        if (checkArgumentTypes(arguments)) {
            for (Consumer<Object[]> eventHandler : eventHandlers) {
                eventHandler.accept(arguments);
            }
        } else {
            throw new RuntimeException("Argument types do not match.");
        }
    }

    private boolean checkArgumentTypes(Object... arguments) {
        if (expectedArgumentTypes.length != arguments.length) {
            return false;
        }

        for (int i = 0; i < expectedArgumentTypes.length; i++) {
            if (!expectedArgumentTypes[i].isInstance(arguments[i])) {
                return false;
            }
        }

        return true;
    }
}


