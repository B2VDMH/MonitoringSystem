package hu.gdf.thesis.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.gdf.thesis.model.PathConfiguration;
import hu.gdf.thesis.model.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileHandler {
    @Autowired
    PathConfiguration pathConfiguration;

    public String readFromFile(String fileName) {
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                if(Files.exists(Path.of(pathConfiguration.getPath() + "\\" + fileName))) {
                    return new String(Files.readAllBytes(Paths.get(pathConfiguration.getPath() + "\\" + fileName)));
                }
            } else {
                if(Files.exists(Path.of(pathConfiguration.getPath() + "/" + fileName))) {
                    return new String(Files.readAllBytes(Paths.get(pathConfiguration.getPath() + "/" + fileName)));
                }
            }
        } catch (IOException ex) {

        }
        return null;
    }

    public Set<String> listFilesInDirectory() {
        return Stream.of(new File(pathConfiguration.getPath()).listFiles((d, name) -> name.endsWith(".json")))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public String serializeJsonConfig(Config config) {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        return gsonBuilder.toJson(config);
    }

    public <T> T deserializeJsonConfig(String configJson, Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(configJson, classOfT);
    }

    public void createFile(String fileName) {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            try {
                if (Files.notExists(Path.of(pathConfiguration.getPath() + "\\" + fileName + ".json"))) {
                    Files.createFile(Paths.get(pathConfiguration.getPath() + "\\" + fileName + ".json"));
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            try {
                if (Files.notExists(Path.of(pathConfiguration.getPath() + "/" + fileName + ".json"))) {
                    Files.createFile(Paths.get(pathConfiguration.getPath() + "/" + fileName + ".json"));
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void deleteFile(String fileName) {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            try {
                Files.delete(Paths.get(pathConfiguration.getPath() + "\\" + fileName));
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            try {
                Files.delete(Paths.get(pathConfiguration.getPath() + "/" + fileName));
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void writeConfigToFile(String fileName, String fileContent) {
        try {
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                if(Files.exists(Path.of(pathConfiguration.getPath() + "\\" + fileName))) {
                    Files.write(Paths.get(pathConfiguration.getPath() + "\\" + fileName), fileContent.getBytes());
                }
            } else {
                if(Files.exists(Path.of(pathConfiguration.getPath() + "/" + fileName))) {
                    Files.write(Paths.get(pathConfiguration.getPath() + "/" + fileName), fileContent.getBytes());
                }
            }

        } catch (IOException ex) {
            System.out.println("Error when trying to save configuration data to the selected file" + "\n" + ex);
        }
    }

    public List<Category> getAllCategories(Config config) {
        return new ArrayList<>(config.getServer().getCategories());
    }

    public List<Entry> getAllEntries(Category category) {
        return new ArrayList<>(category.getEntries());
    }

    public List<RestField> getAllRestFields(Entry entry) {
        return new ArrayList<>(entry.getRestFields());
    }

    public List<Operation> getAllOperations(RestField restField) {
        return new ArrayList<>(restField.getOperation());
    }

    public List<Address> getAllAddresses(Operation operation) {
        return new ArrayList<>(operation.getAddresses());
    }

    public void deleteCategory(String fileName, Config config, Category category) {
        config.getServer().getCategories().remove(category);

        writeConfigToFile(fileName, serializeJsonConfig(config));
    }

    public void deleteEntry(String fileName, Config config, Category category, Entry entry) {
        category.getEntries().remove(entry);

        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, serializeJsonConfig(config));
    }

    public void deleteRestField(String fileName, Config config, Category category, Entry entry, RestField restField) {
        entry.getRestFields().remove(restField);

        int entryIndex = category.getEntries().indexOf(entry);
        category.getEntries().set(entryIndex, entry);

        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, serializeJsonConfig(config));
    }

    public void deleteOperation(String fileName, Config config, Category category, Entry entry, RestField restField, Operation operation) {
        restField.getOperation().remove(operation);

        int restFieldIndex = entry.getRestFields().indexOf(restField);
        entry.getRestFields().set(restFieldIndex, restField);

        int entryIndex = category.getEntries().indexOf(entry);
        category.getEntries().set(entryIndex, entry);

        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, serializeJsonConfig(config));
    }

    public void deleteAddress(String fileName, Config config, Category category, Entry entry, RestField restField, Operation operation, Address address) {
        operation.getAddresses().remove(address);

        int operationIndex = restField.getOperation().indexOf(operation);
        restField.getOperation().set(operationIndex, operation);

        int restFieldIndex = entry.getRestFields().indexOf(restField);
        entry.getRestFields().set(restFieldIndex, restField);

        int entryIndex = category.getEntries().indexOf(entry);
        category.getEntries().set(entryIndex, entry);

        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, serializeJsonConfig(config));
    }

}
