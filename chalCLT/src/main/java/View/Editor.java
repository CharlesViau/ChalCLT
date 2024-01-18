package View;

import Domain.General.Components.Component;
import Domain.Interface.IPropertyChangeManager;
import Domain.MainController;

import javax.swing.*;
import java.util.UUID;

public abstract class Editor {

    protected boolean drawBaseInspector = false;


    public Editor() {

    }

    public abstract JPanel DrawEditor(IPropertyChangeManager manager, UUID selectedObject, Class<? extends Component> currentComponent);

    public boolean isDrawBaseInspector() {
        return drawBaseInspector;
    }

}
