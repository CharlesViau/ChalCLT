package Domain.MenuActions;

import Domain.ChalFile.FileManager;
import Domain.Interface.IManager;

import java.io.File;

public class ProjectManager {
    private boolean isSaved;
    private File saveFile;
    private final FileManager fileManager;
    private static ProjectManager _instance = null;

    public ProjectManager() {
        if (_instance != null)
            throw new RuntimeException("Trying to initialize SaveManager when it already exist.");
        _instance = this;

        fileManager = new FileManager();

        isSaved = true;
    }

    public void subISaveable(IManager savable) {
        fileManager.addSavable(savable);
    }

    public String getCurrentProjectName() {
        return fileManager.getCurrentProjectName();
    }

    public boolean save() {
        if (saveFile == null) {
            return false;
        }
        isSaved = true;
        fileManager.saveFile(saveFile);
        return true;
    }

    public boolean saveAs(File file) {
        saveFile = file;
        fileManager.saveFile(file);
        isSaved = true;
        return true;
    }

    public boolean open(File file) {
        saveFile = file;
        fileManager.loadFile(file);
        isSaved = true;
        return true;
    }

    public void newProject() {
        fileManager.newProject();
        saveFile = null;
        isSaved = false;
    }

    public File getSaveLocation() {
        return fileManager.getCurrentFilePath();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setDirty() {
        isSaved = false;
    }

}
