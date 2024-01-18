package Domain.Drawing;

import Domain.Enum.Direction;
import Domain.Shack.Accessory.Accessory;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Shack;
import View.CurrentView;

import java.awt.*;

public class ShackDrawer {

    public ShackDrawer() {
    }
    public void changeSelectedView(CurrentView view, Shack shack, Grid grid, WallDrawer wallDrawer,
                                   TopDrawer topDrawer, GridDrawer gridDrawer, Graphics g) {

        for(Accessory a : shack.getExteriorWall(Direction.FRONT).getAccessories()) {
            a.setVisible(false);
        }
        for(Accessory a : shack.getExteriorWall(Direction.LEFT).getAccessories()) {
            a.setVisible(false);
        }
        for(Accessory a : shack.getExteriorWall(Direction.BACK).getAccessories()) {
            a.setVisible(false);
        }
        for(Accessory a : shack.getExteriorWall(Direction.RIGHT).getAccessories()) {
            a.setVisible(false);
        }
        shack.getRoof().setVisible(true);

        switch(view) {
            case LEFT:
                if(shack.getRoofFacingDirection() == Direction.FRONT || shack.getRoofFacingDirection() == Direction.BACK) {
                    shack.getExteriorWall(Direction.FRONT).setVisible(true);
                    shack.getExteriorWall(Direction.BACK).setVisible(true);
                    if(shack.getRoofFacingDirection() == Direction.FRONT) {
                        shack.getGable(Direction.LEFT).setVisible(true);
                    } else {
                        shack.getGable(Direction.RIGHT).setVisible(true);
                    }
                }
                shack.getRoof().setVisible(true);
                Wall leftWall = shack.getExteriorWall(Direction.LEFT);
                leftWall.setVisible(true);
                for(Accessory acc : leftWall.getAccessories()) {
                    acc.setVisible(true);
                }

                if(shack.getRoofFacingDirection() != Direction.LEFT)
                    shack.getExtension().setVisible(true);
                break;
            case RIGHT:
                if(shack.getRoofFacingDirection() == Direction.FRONT || shack.getRoofFacingDirection() == Direction.BACK) {
                    shack.getExteriorWall(Direction.FRONT).setVisible(true);
                    shack.getExteriorWall(Direction.BACK).setVisible(true);
                    if(shack.getRoofFacingDirection() == Direction.FRONT) {
                        shack.getGable(Direction.RIGHT).setVisible(true);
                    } else {
                        shack.getGable(Direction.LEFT).setVisible(true);
                    }
                }
                shack.getRoof().setVisible(true);
                Wall rightWall = shack.getExteriorWall(Direction.RIGHT);
                rightWall.setVisible(true);
                for(Accessory acc : rightWall.getAccessories()) {
                    acc.setVisible(true);
                }

                if(shack.getRoofFacingDirection() != Direction.RIGHT)
                    shack.getExtension().setVisible(true);
                break;
            case FRONT:
                if(shack.getRoofFacingDirection() == Direction.RIGHT || shack.getRoofFacingDirection() == Direction.LEFT) {
                    shack.getExteriorWall(Direction.RIGHT).setVisible(true);
                    shack.getExteriorWall(Direction.LEFT).setVisible(true);
                    if(shack.getRoofFacingDirection() == Direction.RIGHT) {
                        shack.getGable(Direction.LEFT).setVisible(true);
                    } else {
                        shack.getGable(Direction.RIGHT).setVisible(true);
                    }
                }
                shack.getRoof().setVisible(true);
                Wall frontWall = shack.getExteriorWall(Direction.FRONT);
                frontWall.setVisible(true);
                for(Accessory acc : frontWall.getAccessories()) {
                    acc.setVisible(true);
                }

                if(shack.getRoofFacingDirection() != Direction.FRONT)
                    shack.getExtension().setVisible(true);
                break;
            case BACK:
                if(shack.getRoofFacingDirection() == Direction.RIGHT || shack.getRoofFacingDirection() == Direction.LEFT) {
                    shack.getExteriorWall(Direction.RIGHT).setVisible(true);
                    shack.getExteriorWall(Direction.LEFT).setVisible(true);
                    if(shack.getRoofFacingDirection() == Direction.RIGHT) {
                        shack.getGable(Direction.RIGHT).setVisible(true);
                    } else {
                        shack.getGable(Direction.LEFT).setVisible(true);
                    }
                }
                shack.getRoof().setVisible(true);
                Wall backWall = shack.getExteriorWall(Direction.BACK);
                backWall.setVisible(true);
                for(Accessory acc : backWall.getAccessories()) {
                    acc.setVisible(true);
                }

                if(shack.getRoofFacingDirection() != Direction.BACK)
                    shack.getExtension().setVisible(true);
                break;
            case TOP:
                shack.getExteriorWall(Direction.FRONT).setVisible(true);
                shack.getExteriorWall(Direction.BACK).setVisible(true);
                shack.getExteriorWall(Direction.LEFT).setVisible(true);
                shack.getExteriorWall(Direction.RIGHT).setVisible(true);
                break;
        }

        if(view == CurrentView.TOP) {
            topDrawer.draw(g);
        } else {
            wallDrawer.setCurrentView(view);
            wallDrawer.draw(g);
        }

        if(view == CurrentView.TOP) {
            topDrawer.draw(g);
        } else {
            wallDrawer.setCurrentView(view);
            wallDrawer.draw(g);
        }

        if(grid.isVisible()) {
            gridDrawer.draw(g);
        }
    }
}
