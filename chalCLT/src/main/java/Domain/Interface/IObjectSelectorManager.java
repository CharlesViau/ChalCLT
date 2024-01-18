package Domain.Interface;

public interface IObjectSelectorManager {
    void subscribeToSelectionEvents(IObjectSelectorListener listener);

    void notifySelection(IInspectable obj);

    void notifyUnselection();
}
