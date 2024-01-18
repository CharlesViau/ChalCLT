package Domain.ChalFile;

import Domain.Interface.IManager;

import java.io.*;
import java.util.ArrayList;
import java.awt.Component;

public class FileManager {
    private static FileManager _instance = null;
    private final ArrayList<IManager> savables;
    private File currentFilePath = null;
    private String currentProjectName = null;
    private String fileExtension = ".clt";

    public FileManager() {
        if (_instance != null)
            throw new RuntimeException("Trying to initialize FileManager when it already exist.");
        _instance = this;

        savables = new ArrayList<IManager>();
    }

    public void addSavable(IManager savable) {
        savables.add(savable);
    }

    //Adds the .clt extension to the file if it doesn't have it
    private File checkExtension(File file) {
        if (!file.getName().endsWith(fileExtension)) {
            file = new File(file.getAbsolutePath() + fileExtension);
        }
        return file;
    }

    public String getCurrentProjectName() {
        return currentProjectName;
    }

    public void saveFile(File file) {
        File filePath = checkExtension(file);
        try {
            currentFilePath = filePath;
            currentProjectName = filePath.getName().replace(fileExtension, "");
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            for (IManager savable : savables) {
                savable.save(objectOutputStream);
            }
            objectOutputStream.close();
            fileOutputStream.close();
            System.out.println("Objects saved to " + filePath.getName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFile(File filePath) {
        currentFilePath = filePath;
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            for (IManager savable : savables) {
                savable.load(objectInputStream);
            }
            objectInputStream.close();
            fileInputStream.close();
            System.out.println("Objects loaded from " + filePath.getName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newProject() {
        for (IManager savable : savables) {
            savable.newProject();
        }
    }

    public File getCurrentFilePath() {
        if (currentFilePath == null || currentFilePath.getParentFile() == null) {
            return new File("");
        }
        return currentFilePath.getParentFile();
    }

}