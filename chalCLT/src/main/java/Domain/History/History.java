package Domain.History;

import java.util.Stack;

public class History {
    private final Stack<Command> m_RedoCommands;
    private final Stack<Command> m_UndoCommands;
    public History(){

        m_RedoCommands = new Stack<>();
        m_UndoCommands = new Stack<>();
    }

    public void Push(Command command){
        command.Execute();

        m_UndoCommands.push(command);

    }

    public void Pop(){

        if(!m_UndoCommands.empty()){
            Command c = m_UndoCommands.pop();

            c.undo();
            m_RedoCommands.push(c);
        }

    }
    public void Redo(){
        if(m_RedoCommands.empty())
            return;
        Command c = m_RedoCommands.pop();
        c.Execute();
        m_UndoCommands.push(c);
    }

    public void clearHistory() {
        m_RedoCommands.clear();
        m_UndoCommands.clear();
    }
}

