package Domain.History;

import Domain.General.Components.Component;
import Domain.General.Entity;
import Domain.General.EntityManager;
import Domain.Utility.Vector2;

import java.util.UUID;

public class AccessoryDeletionCommand <T> extends Command{
    private EntityManager m_entityManager;

    private Class<? extends Component> m_currentComponentType;
    private  Class<? extends Component> m_selectedAccessoryClass;
    private String X;
    private String Y;
    private UUID m_parent;
    public AccessoryDeletionCommand(Object value, Object oldValue, EntityManager e, Class<? extends Component> currentComponentType, Class<? extends Component> selectedAccessoryClass, UUID parent) {
        super(value, oldValue);
        m_entityManager = e;
        m_currentComponentType = currentComponentType;
        m_selectedAccessoryClass = selectedAccessoryClass;
        m_parent =parent;
    }

    @Override
    public void Execute() {
        Entity e=m_entityManager.getEntity((UUID)m_Value);
        X = String.valueOf(e.transform.getX());
        Y = String.valueOf(e.transform.getY());
        m_entityManager.destroy((UUID)m_Value);
    }

    @Override
    public void undo() {

        Entity e=m_entityManager.instantiate((UUID)m_parent,m_currentComponentType,m_selectedAccessoryClass,new Object[]{X,Y});
        m_Value=e.getUuid();

    }
}
