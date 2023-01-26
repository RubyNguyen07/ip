package duke;

import duke.exceptions.InvalidDateFormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/** A class Storage that deals with loading tasks from the file and saving tasks in the file */
public class Storage {
    /** The file path */
    private String path;

    /**
     * Initializes an Storage object with the given path.
     *
     * @param relativePath The relative path
     * @return A Storage instance
     */
    public Storage(String relativePath) {
        path = relativePath;
    }

    /**
     * Initializes and loads tasks from file indicated when Duke is started.
     *
     * @throws FileNotFoundException If file is not found
     * @return A list of tasks saved in file
     */
    public ArrayList<Task> initialize() throws FileNotFoundException {
        File f = new File(path);
        ArrayList<Task> og = new ArrayList<>();
        if (f.exists()) {
            Scanner sc = new Scanner(f);
            while (sc.hasNext()) {
                String str = sc.nextLine();
                String[] keywords = splitString(str);
                String type = keywords[0];
                String doneStatus = keywords[1];
                boolean isDone = doneStatus == "X" ? true : false;
                Task toInsert = null;
                switch (type) {
                case "T":
                    toInsert = new ToDo(keywords[2], isDone);
                    break;
                case "D":
                    try {
                        toInsert = new Deadline(keywords[2], Parser.getDateMMM(keywords[3]), isDone);
                    } catch (InvalidDateFormatException err) {
                        System.out.println(err.getMessage());
                    }
                    break;
                case "E":
                    String[] duration = keywords[3].split(" - ");
                    toInsert = new Event(keywords[2], duration[0], duration[1], isDone);
                    break;
                }
                og.add(toInsert);
            }
            sc.close();
        }
        return og;
    }

    private String[] splitString(String str) {
        return str.split(" \\| ");
    }

    /**
     * Load tasks from another place into the file
     *
     * @throws IOException If there is a problem with the writer class
     */
    public void load() throws IOException {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }

        f.getParentFile().mkdirs();
        f.createNewFile();


        FileWriter fw = new FileWriter(path);
        ArrayList<Task> changed = TaskList.getList();
        for (int i = 0; i < changed.size(); i++) {
            fw.write(changed.get(i).toString());
            fw.write("\n");
        }
        fw.close();
    }
}
