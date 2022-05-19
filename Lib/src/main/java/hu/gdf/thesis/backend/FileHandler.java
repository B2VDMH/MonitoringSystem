package hu.gdf.thesis.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import hu.gdf.thesis.model.PathConfiguration;
import hu.gdf.thesis.model.config.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileHandler {

    //Config directory path from application.properties
    @Autowired
    PathConfiguration pathConfiguration;

    //List all files in the specified directory
    public Set<String> listFilesInDirectory() {
        return Stream.of(Objects.requireNonNull(new File(pathConfiguration.getPath()).listFiles((d, name) -> name.endsWith(".json"))))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    //Check if specific file exists in directory
    public boolean fileExists(String fileName) {
        return Files.exists(Path.of(pathConfiguration.getPath() + File.separator + fileName + ".json"));
    }

    //If the file does not exist, create it in directory
    public void createFile(String fileName) {
        try {
            String filePath = pathConfiguration.getPath() + File.separator + fileName + ".json";
            if (Files.notExists(Path.of(filePath))) {
                Files.createFile(Paths.get(filePath));
                log.info("Created Config: " + fileName + ".json " + "In directory: " + pathConfiguration.getPath());
            } else {
                log.warn("Warning, file already existed in directory: " + pathConfiguration.getPath());
            }
        } catch (Exception ex) {
            log.error("Error when creating file: " + fileName + ".json", ex);
        }
    }

    //If the file exists, parse the content as String
    public String readFromFile(String fileName) {
        try {
            if (Files.exists(Path.of(pathConfiguration.getPath() + File.separator + fileName)) && fileName.endsWith(".json")) {
                return new String(Files.readAllBytes(Paths.get(pathConfiguration.getPath() + File.separator + fileName)));
            } else {
                log.warn("File does not exist!");
            }
        } catch (IOException ex) {
            log.error("Unable to read from file", ex);
        }
        return null;
    }

    //If the file exists in directory, delete it
    public void deleteFile(String fileName) {
        try {
            String filePath = pathConfiguration.getPath() + File.separator + fileName;
            if(Files.exists(Path.of(filePath))) {
                Files.delete(Paths.get(pathConfiguration.getPath() + File.separator + fileName));
                log.info("Deleted Config: " + pathConfiguration.getPath() + File.separator + fileName);
            } else {
                log.warn("Warning, file was not found in directory: " + pathConfiguration.getPath());
            }
        } catch (IOException ex) {
            log.error("Error when deleting file: " + fileName + ".json", ex);
        }
    }

    //If the file exists in directory, overwrite it's content
    public void writeConfigToFile(String fileName, String fileContent) {
        try {
            String filePath = pathConfiguration.getPath() + File.separator + fileName;
            if (Files.exists(Path.of(filePath))) {
                Files.write(Paths.get(filePath), fileContent.getBytes());
                log.info("Saved Config: " + pathConfiguration.getPath() + File.separator + fileName );
            } else {
                log.warn("Warning, file was not found in directory: " + pathConfiguration.getPath());
            }
        } catch (IOException ex) {
            log.error("Error when trying to save configuration data to the selected file", ex);
        }
    }

    //Serialization of JSON using Gson
    public String serializeJsonConfig(Config config) {
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        return gsonBuilder.toJson(config);
    }

    //Deserialization of JSON using Gson
    public <T> T deserializeJsonConfig(String configJson, Class<T> classOfT) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(configJson, classOfT);
        } catch (JsonSyntaxException ex) {
            log.error("Error occurred when trying to deserialize Json file. " + ex.getMessage());
            return null;
        }
    }

    //Lists of specific objects in deserialized json object

    public List<Category> getAllCategories(Config config) {
        return new ArrayList<>(config.getServer().getCategories());
    }
    public List<Address> getAllAddresses(Config config) {
        return new ArrayList<>(config.getServer().getAddresses());
    }

    public List<Entry> getAllEntries(Category category) {
        return new ArrayList<>(category.getEntries());
    }

    public List<RestField> getAllRestFields(Entry entry) {
        return new ArrayList<>(entry.getRestFields());
    }

    public List<Operation> getAllOperations(RestField restField) {
        return new ArrayList<>(restField.getOperations());
    }

    //Deletion of specific elements in deserialized json object

    public void deleteOrEditCategory(String fileName, Config config, Category category, String action) {

        if(action.equals("edit")) {
            int categoryIndex = config.getServer().getCategories().indexOf(category);
            config.getServer().getCategories().set(categoryIndex, category);
        } else {
            config.getServer().getCategories().remove(category);
        }
        writeConfigToFile(fileName, serializeJsonConfig(config));
    }


    public void deleteOrEditEntry(String fileName, Config config, Category category, Entry entry, String action) {
        if(action.equals("edit")) {
            int entryIndex = category.getEntries().indexOf(entry);
            category.getEntries().set(entryIndex, entry);
        } else {
            log.info("I GOT HEEEEEERE");
            category.getEntries().remove(entry);
        }
        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);
        log.info("I GOT HEEEEEERE tooo");
        writeConfigToFile(fileName, serializeJsonConfig(config));

    }

    public void deleteOrEditRestField(String fileName, Config config, Category category, Entry entry, RestField restField, String action) {
        if(action.equals("edit")) {
            int restFieldIndex = entry.getRestFields().indexOf(restField);
            entry.getRestFields().set(restFieldIndex, restField);
        } else {
            entry.getRestFields().remove(restField);
        }

        int entryIndex = category.getEntries().indexOf(entry);
        category.getEntries().set(entryIndex, entry);

        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, serializeJsonConfig(config));
    }


    public void deleteOrEditOperation(String fileName, Config config, Category category, Entry entry, RestField
            restField, Operation operation, String action) {
        if(action.equals("edit")) {
            int operationIndex= restField.getOperations().indexOf(operation);
            restField.getOperations().set(operationIndex, operation);
        }else {
            restField.getOperations().remove(operation);
        }
        int restFieldIndex = entry.getRestFields().indexOf(restField);
        entry.getRestFields().set(restFieldIndex, restField);

        int entryIndex = category.getEntries().indexOf(entry);
        category.getEntries().set(entryIndex, entry);

        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, serializeJsonConfig(config));

    }

    public void deleteAddress(String fileName, Config config, Address address) {

        config.getServer().getAddresses().remove(address);
        writeConfigToFile(fileName, serializeJsonConfig(config));

    }

}
