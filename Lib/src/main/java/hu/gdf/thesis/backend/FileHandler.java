package hu.gdf.thesis.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import hu.gdf.thesis.model.PathConfiguration;
import hu.gdf.thesis.model.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    public String readFromFile(String fileName) {
        try {
            if (Files.exists(Path.of(pathConfiguration.getPath() + File.separator + fileName))) {
                return new String(Files.readAllBytes(Paths.get(pathConfiguration.getPath() + File.separator + fileName)));
            } else {
                LOGGER.warn("File " + fileName + " does not exist");
            }
        } catch (IOException ex) {
            LOGGER.error("Unable to read from file", ex);
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
        try {
            Gson gson = new Gson();
            return gson.fromJson(configJson, classOfT);
        } catch (JsonSyntaxException ex) {
            LOGGER.error("Exception occured when trying to deserialize Json file. ", ex);
            return null;
        }
    }

    public void createFile(String fileName) {
        try {
            if (Files.notExists(Path.of(pathConfiguration.getPath() + File.separator + fileName + ".json"))) {
                Files.createFile(Paths.get(pathConfiguration.getPath() + File.separator + fileName + ".json"));
                LOGGER.info("Created Config: " + pathConfiguration.getPath() + File.separator + fileName + ".json");
            }
        } catch (Exception ex) {
            LOGGER.error("Error when creating file: " + fileName + ".json", ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(pathConfiguration.getPath() + File.separator + fileName));
            LOGGER.info("Deleted Config: " + pathConfiguration.getPath() + File.separator + fileName);
        } catch (IOException ex) {
            LOGGER.error("Error when deleting file: " + fileName + ".json", ex);
        }
    }

    public void writeConfigToFile(String fileName, String fileContent) {
        try {
            if (Files.exists(Path.of(pathConfiguration.getPath() + File.separator + fileName + ".json"))) {
                Files.write(Paths.get(pathConfiguration.getPath() + File.separator + fileName + ".json"), fileContent.getBytes());
                LOGGER.info("Saved Config: " + pathConfiguration.getPath() + File.separator + fileName + ".json");
            }
        } catch (IOException ex) {
            LOGGER.error("Error when trying to save configuration data to the selected file", ex);
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

    public void deleteOperation(String fileName, Config config, Category category, Entry entry, RestField
            restField, Operation operation) {
        restField.getOperation().remove(operation);

        int restFieldIndex = entry.getRestFields().indexOf(restField);
        entry.getRestFields().set(restFieldIndex, restField);

        int entryIndex = category.getEntries().indexOf(entry);
        category.getEntries().set(entryIndex, entry);

        int categoryIndex = config.getServer().getCategories().indexOf(category);
        config.getServer().getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, serializeJsonConfig(config));

    }

    public void deleteAddress(String fileName, Config config, Category category, Entry entry, RestField
            restField, Operation operation, Address address) {
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
