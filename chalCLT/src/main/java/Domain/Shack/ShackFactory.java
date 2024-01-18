package Domain.Shack;

import Domain.General.EntityManager;
import Domain.General.Entity;
import Domain.MainController;

public class ShackFactory {
    public Shack createShack(EntityManager entityManager, MainController mainController) {
        Shack shack;
        //Create Shack
        Entity e = entityManager.instantiate();
        e.name = "Chalet";
        shack = e.addComponent(Shack.class);
        assert shack != null;
        shack.init(mainController);
        //Hide transform in inspector for the shack.
        e.transform.showInInspector = false;
        return shack;
    }

}
