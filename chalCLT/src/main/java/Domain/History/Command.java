package Domain.History;

public abstract class Command{

    protected Object m_Value;
    protected Object m_OldValue;


    Command(Object value, Object oldValue){
        m_Value = value;

        m_OldValue = oldValue;
    }

    public abstract void Execute();
    public abstract void undo();
}
