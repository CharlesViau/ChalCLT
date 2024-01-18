package Domain.History;

import Domain.Drawing.Grid;

public class CommandGrille<T>  extends Command{

    private final Grid m_grid;
    public CommandGrille(T value, T oldValue, Grid grid) {
        super(value, oldValue);
        m_grid = grid;
    }

    @Override
    public void Execute() {
        m_grid.setVisible((boolean)m_Value);
    }

    @Override
    public void undo() {
        m_grid.setVisible((boolean)m_OldValue);
    }
}
