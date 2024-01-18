package Domain.Interface;

import Domain.General.Entity;

import java.util.ArrayList;
import java.util.UUID;

public interface IInspectable {

    UUID getUuid();

    String getName();

    IInspectable getParent();

    Entity getEntity();

    ArrayList<IInspectable> getIChildren();

}
