package Domain.History;

import Domain.General.Components.Component;
import Domain.General.Entity;
import Domain.General.EntityManager;

import java.util.UUID;

public class AccessoryCommand extends Command{
    private final EntityManager entityManager;
    private final Class<? extends Component> m_currentComponentType;
    private final Class<? extends Component> m_selectedAccessoryClass;
    private final Object[] m_point;
    private Entity createdEntity = null;

    public AccessoryCommand(Object value, Object oldValue, EntityManager e, Class<? extends Component> currentComponentType, Class<? extends Component> selectedAccessoryClass, Object[] p) {
        super(value, oldValue);
        entityManager = e;
        m_currentComponentType=currentComponentType;
        m_selectedAccessoryClass = selectedAccessoryClass;
        m_point = p;

    }

    @Override
    public void Execute() {
        createdEntity = entityManager.instantiate((UUID) m_Value, m_currentComponentType, m_selectedAccessoryClass, m_point);
    }

    @Override
    public void undo() {
        entityManager.destroy(createdEntity);
    }
}
