package Domain.History;

import Domain.Drawing.*;
import Domain.Shack.Shack;
import Domain.Utility.ViewHolder;
import Domain.Utility.ZoomUtility;
import View.CurrentView;

import java.awt.*;

public class ViewCommand extends Command{

    private ShackDrawer m_shackDrawer;
    private Shack m_shack;
    private Grid m_grid;
    private WallDrawer m_walldrawer;
    private TopDrawer m_topDrawer;
    private GridDrawer m_gridDrawer;
    private Graphics m_g;
    private ViewHolder m_viewHolder;

    public ViewCommand(Object value, Object oldValue, ShackDrawer shackDrawer, Shack shack, Grid grid, WallDrawer wallDrawer, TopDrawer topDrawer, GridDrawer gridDrawer, Graphics g,ViewHolder viewHodler) {
        super(value,oldValue);
        m_shackDrawer = shackDrawer;
        m_shack = shack;
        m_grid = grid;
        m_walldrawer = wallDrawer;
        m_topDrawer = topDrawer;
        m_gridDrawer= gridDrawer;
        m_g = g;
        m_viewHolder = viewHodler;
    }

    @Override
    public void Execute() {
        m_shackDrawer.changeSelectedView((CurrentView) m_Value,m_shack, m_grid, m_walldrawer, m_topDrawer, m_gridDrawer, m_g);
        m_viewHolder.setCurrentView((CurrentView) m_Value);
    }

    @Override
    public void undo() {

        m_shackDrawer.changeSelectedView((CurrentView) m_OldValue,m_shack, m_grid, m_walldrawer, m_topDrawer, m_gridDrawer, m_g);
        m_viewHolder.setCurrentView((CurrentView) m_OldValue);
    }
}
