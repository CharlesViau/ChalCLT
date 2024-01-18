package Domain.Utility;

import View.CurrentView;

public class ViewHolder {
    private CurrentView m_view;

    public ViewHolder(CurrentView view){
        m_view = view;
    }

    public CurrentView getCurrentView(){
        return m_view;
    }
    public void setCurrentView(CurrentView view){
        m_view = view;
    }

}
