package Domain.Interface;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface IManager {
    void save(ObjectOutputStream objectOutputStream);
    void load(ObjectInputStream objectInputStream);
    void newProject();
}
