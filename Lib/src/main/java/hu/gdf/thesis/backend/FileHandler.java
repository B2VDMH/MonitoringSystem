package hu.gdf.thesis.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
            String filePath = pathConfiguration.getPath() + File.separator + fileName;
            if (Files.exists(Path.of(filePath)) && fileName.endsWith(".json")) {
                return new String(Files.readAllBytes(Paths.get(filePath)));
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
            if (Files.exists(Path.of(filePath))) {
                Files.delete(Paths.get(filePath));
                log.info("Deleted Config: " + filePath);
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
                log.info("Saved Config: " + filePath);
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
        try {
            return new ArrayList<>(config.getServer().getCategories());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public List<Address> getAllAddresses(Config config) {
        try {
            return new ArrayList<>(config.getServer().getAddresses());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public List<Entry> getAllEntries(Category category) {
        try {
            return new ArrayList<>(category.getEntries());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public List<RestField> getAllRestFields(Entry entry) {
        try {
            return new ArrayList<>(entry.getRestFields());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public List<Operation> getAllOperations(RestField restField) {
        try {
            return new ArrayList<>(restField.getOperations());
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    //Deletion of specific elements in deserialized json object

    public void deleteOrEditCategory(String fileName, Config config, Category category, boolean edit) {
        try {
            if (edit) {
                int categoryIndex = config.getServer().getCategories().indexOf(category);
                config.getServer().getCategories().set(categoryIndex, category);
            } else {
                config.getServer().getCategories().remove(category);
            }

            writeConfigToFile(fileName, serializeJsonConfig(config));

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete category in config", ex);
        }
    }


    public void deleteOrEditEntry(String fileName, Config config, Category category, Entry entry, boolean edit) {
        try {
            if (edit) {
                int entryIndex = category.getEntries().indexOf(entry);
                category.getEntries().set(entryIndex, entry);

            } else {
                category.getEntries().remove(entry);
            }

            int categoryIndex = config.getServer().getCategories().indexOf(category);
            config.getServer().getCategories().set(categoryIndex, category);

            writeConfigToFile(fileName, serializeJsonConfig(config));

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete category in config", ex);
        }

    }

    public void deleteOrEditRestField(String fileName, Config config, Category category, Entry entry, RestField restField, boolean edit) {
        try {
            if (edit) {
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

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete category in config", ex);
        }
    }


    public void deleteOrEditOperation(String fileName, Config config, Category category, Entry entry, RestField
            restField, Operation operation, boolean edit) {
        try {
            if (edit) {
                int operationIndex = restField.getOperations().indexOf(operation);
                restField.getOperations().set(operationIndex, operation);
            } else {
                restField.getOperations().remove(operation);
            }
            int restFieldIndex = entry.getRestFields().indexOf(restField);
            entry.getRestFields().set(restFieldIndex, restField);

            int entryIndex = category.getEntries().indexOf(entry);
            category.getEntries().set(entryIndex, entry);

            int categoryIndex = config.getServer().getCategories().indexOf(category);
            config.getServer().getCategories().set(categoryIndex, category);

            writeConfigToFile(fileName, serializeJsonConfig(config));

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete category in config", ex);
        }
    }

    public void deleteAddress(String fileName, Config config, Address address) {
        try {
            config.getServer().getAddresses().remove(address);
            writeConfigToFile(fileName, serializeJsonConfig(config));

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete category in config", ex);
        }

    }

}
