package Domain.History;

import Domain.General.EntityManager;
import Domain.General.Components.Component;

import java.util.UUID;

public class PropertyCommand extends Command {
    private final EntityManager _entityManager;
    private final UUID _uuid;
    private final Class<? extends Component> _componentType;
    private final String _fieldName;

    public PropertyCommand(Object value, Object oldValue, EntityManager entityManager, UUID objectUuid, Class<? extends Component> componentType, String fieldName) {
        super(value, oldValue);
        _entityManager = entityManager;
        _uuid = objectUuid;
        _componentType = componentType;
        _fieldName = fieldName;
    }

    @Override
    public void Execute() {

        m_OldValue = _entityManager.editProperty(_uuid, _componentType, _fieldName, m_Value);
    }

    @Override
    public void undo() {
        m_OldValue = _entityManager.editProperty(_uuid, _componentType, _fieldName, m_OldValue);
    }
}
