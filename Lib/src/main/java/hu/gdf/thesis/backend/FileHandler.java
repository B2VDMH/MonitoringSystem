package hu.gdf.thesis.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import hu.gdf.thesis.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileHandler {


    @Autowired
    PathConfiguration pathConfiguration;
    //Config directory path from application.properties
    public String directory() {
        return pathConfiguration.getPath();
    }

    //Serialization of Java objects with Gson
    public String serialize(Config config) {
        try {
            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
            return gsonBuilder.toJson(config);
        } catch (NullPointerException ex) {
            log.error("Error occurred when trying to serialize java object. " + ex.getMessage());
            return null;
        }
    }

    //Deserialization of JSON with Gson
    public Config deserialize(String fileName) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(readFromFile(fileName), Config.class);
        } catch (JsonSyntaxException | IllegalStateException ex ) {
            return null;
        }
    }

    private Path path(String fileName, Boolean withExtension) {
        try {
            if (withExtension) {
                return Path.of(directory() + File.separator + fileName + ".json");
            }
            return Path.of(directory() + File.separator + fileName);
        } catch (NullPointerException ex){
            log.warn("No files found in directory " + directory());
            return null;
        }
    }

    public boolean validateConfig (File file) {
        return (deserialize(file.getName())!=null);
    }

    //List all files in the specified directory
    public Set<String> listFilesInDirectory() {
        return Stream.of(Objects.requireNonNull(new File(directory()).listFiles((directory, fileName) -> fileName.endsWith(".json"))))
                .filter(file -> !file.isDirectory())
                .filter(this::validateConfig)
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    //Check if specific file exists in directory
    public boolean fileExists(String fileName) {
        return Files.exists(path(fileName, true));
    }

    //If the file does not exist, create it in directory
    public void createFile(String fileName) {
        try {
            Path filePath = path(fileName, true);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                log.info("Created Config: " + fileName + ".json " + "In directory: " + directory());
            }
        } catch (Exception ex) {
            log.error("Error when creating file: " + fileName + ".json", ex);
        }
    }

    //If the file exists, parse the content as String
    public String readFromFile(String fileName) {
        try {
            Path filePath = path(fileName, false);
            if (Files.exists(filePath) && fileName.endsWith(".json") && !Files.isDirectory(filePath)) {
                return new String(Files.readAllBytes(filePath));
            }
        } catch (IOException ex) {
            log.error("Unable to read from file", ex);
        }
        return null;
    }

    //If the file exists in directory, delete it
    public void deleteFile(String fileName) {
        try {
            Path filePath = path(fileName, false);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted Config: " + filePath);
            }
        } catch (IOException ex) {
            log.error("Error when deleting file: " + fileName + ".json", ex);
        }
    }

    //If the file exists in directory, overwrite it's content
    public void writeConfigToFile(String fileName, Config config) {
        try {
            Path filePath = path(fileName, false);
            if (Files.exists((filePath))) {
                Files.writeString(filePath, serialize(config));
                log.info("Saved Config: " + filePath);
            }
        } catch (IOException ex) {
            log.error("Error when trying to save configuration data to the selected file", ex);
        }
    }


    //Lists of specific data objects

    public void addCategory(String fileName, Config config, Category category) {

        config.getCategories().add(category);

        writeConfigToFile(fileName, config);

    }

    public void addEndpoint(String fileName, Config config, Category category, Endpoint endpoint) {
        category.getEndpoints().add(endpoint);

        int categoryIndex = config.getCategories().indexOf(category);

        config.getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, config);
    }

    public void addField(String fileName, Config config, Category category, Endpoint endpoint, Field field) {
        endpoint.getFields().add(field);

        int endpointIndex = category.getEndpoints().indexOf(endpoint);
        category.getEndpoints().set(endpointIndex, endpoint);

        int categoryIndex = config.getCategories().indexOf(category);
        config.getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, config);
    }

    public void addOperation(String fileName, Config config,
                             Category category, Endpoint endpoint,
                             Field field, Operation operation) {
        field.getOperations().add(operation);

        int fieldIndex = endpoint.getFields().indexOf(field);
        endpoint.getFields().set(fieldIndex, field);

        int endpointIndex = category.getEndpoints().indexOf(endpoint);
        category.getEndpoints().set(endpointIndex, endpoint);

        int categoryIndex = config.getCategories().indexOf(category);
        config.getCategories().set(categoryIndex, category);

        writeConfigToFile(fileName, config);
    }

    public void addAddress (String fileName, Config config, Address address) {
        config.getAddresses().add(address);

        writeConfigToFile(fileName, config);
    }
    //Delete or edit specific elements in config
    public void modifyCategory(String fileName, Config config,
                               Category category, boolean edit) {
        try {
            if (edit) {
                int categoryIndex = config.
                        getCategories().indexOf(category);
                config.getCategories().set(categoryIndex, category);
            } else {
                config.getCategories().remove(category);
            }

            writeConfigToFile(fileName, config);

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete category in config", ex);
        }
    }


    public void modifyEndpoint(String fileName, Config config, Category category, Endpoint endpoint, boolean edit) {
        try {
            if (edit) {
                int endpointIndex = category.getEndpoints().indexOf(endpoint);
                category.getEndpoints().set(endpointIndex, endpoint);

            } else {
                category.getEndpoints().remove(endpoint);
            }

            int categoryIndex = config.getCategories().indexOf(category);
            config.getCategories().set(categoryIndex, category);

            writeConfigToFile(fileName, config);

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete endpoint in config", ex);
        }

    }

    public void modifyField(String fileName, Config config, Category category, Endpoint endpoint, Field field, boolean edit) {
        try {
            if (edit) {
                int fieldIndex = endpoint.getFields().indexOf(field);
                endpoint.getFields().set(fieldIndex, field);
            } else {
                endpoint.getFields().remove(field);
            }

            int endpointIndex = category.getEndpoints().indexOf(endpoint);
            category.getEndpoints().set(endpointIndex, endpoint);

            int categoryIndex = config.getCategories().indexOf(category);
            config.getCategories().set(categoryIndex, category);

            writeConfigToFile(fileName, config);

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete field in config", ex);
        }
    }


    public void modifyOperation(String fileName, Config config, Category category, Endpoint endpoint, Field
            field, Operation operation, boolean edit) {
        try {
            if (edit) {
                int operationIndex = field.getOperations().indexOf(operation);
                field.getOperations().set(operationIndex, operation);
            } else {
                field.getOperations().remove(operation);
            }

            int fieldIndex = endpoint.getFields().indexOf(field);
            endpoint.getFields().set(fieldIndex, field);

            int endpointIndex = category.getEndpoints().indexOf(endpoint);
            category.getEndpoints().set(endpointIndex, endpoint);

            int categoryIndex = config.getCategories().indexOf(category);
            config.getCategories().set(categoryIndex, category);

            writeConfigToFile(fileName, config);

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete operation in config", ex);
        }
    }

    public void deleteAddress(String fileName, Config config, Address address) {
        try {
            config.getAddresses().remove(address);

            writeConfigToFile(fileName, config);

        } catch (NullPointerException ex) {
            log.error("Error when trying to edit or delete address in config", ex);
        }

    }

}
