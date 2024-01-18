package Domain.History;

import Domain.Drawing.Grid;

public class CommandMesureGrille <T> extends Command
{
    private Grid m_grid;
    public CommandMesureGrille(T value, T oldValue,Grid grid) {
        super(value, oldValue);
        m_grid = grid;
    }

    @Override
    public void Execute() {m_grid.setDistance((float)m_Value);}

    @Override
    public void undo() {
        //swap les values
        T temp = (T) m_Value;
        m_Value = m_OldValue;
        m_OldValue = temp;
        //executer
        m_grid.setDistance((float)m_Value);
        //swap les values encore pour eviter bugs eventuels
         temp = (T) m_OldValue;
        m_OldValue = m_Value;
        m_Value = temp;
    }
}
