package Domain.History;

import Domain.Interface.IInspectable;
import Domain.MainController;

public class ListenersAccessor
{
    private MainController _controlleur;

    public ListenersAccessor(MainController controlleur){
        _controlleur = controlleur;
    }

    public void NotifyListenersSelection(IInspectable object){
        _controlleur.notifySelection(object);
    }
    public void NotifyListenersUnselection(){
        _controlleur.notifyUnselection();
    }

}
