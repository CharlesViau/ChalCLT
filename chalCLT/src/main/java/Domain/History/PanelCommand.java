package Domain.History;

import Domain.General.Entity;
import Domain.Interface.IElementsShack;
import Domain.Interface.IInspectable;
import Domain.Shack.Shack;

public class PanelCommand extends Command{
    private Shack m_shack;
    private  ListenersAccessor m_accessor;
    public PanelCommand(Object value, Object oldvalue, Shack shack, ListenersAccessor accessor) {
        super(value, oldvalue);
        m_shack = shack;
        m_accessor = accessor;
    }

    @Override
    public void Execute() {
        m_shack.setActiveEntity((Entity) m_Value);

        m_accessor.NotifyListenersSelection((IInspectable)m_Value);
    }

    @Override
    public void undo() {
        //on swap les deux valeurs

        //on rappelle les méthodes d'avant
        if(m_OldValue==null){
            m_shack.setActiveEntity((Entity)m_OldValue);
            m_accessor.NotifyListenersUnselection();
        }
        else{
            //dans le cas où il y avait un panneau avant
            m_shack.setActiveEntity((Entity) m_OldValue);

            m_accessor.NotifyListenersSelection((IInspectable)m_OldValue);

        }
    }
}
