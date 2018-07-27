package repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import models.init.Locations;
import models.init.Users;
import models.init.Visits;
import net.lingala.zip4j.model.FileHeader;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.core.ZipFile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Alikin E.A. on 14.06.18.
 */
public class Repository {

    private static volatile long currentTimeStamp = 0l;
    private static final String dataPath = "/tmp/data/";
    //private static final String dataPath = "/mnt/highloaddata/";

    public static final ConcurrentHashMap<String, byte[]> mapUser = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, byte[]> mapLocation = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, byte[]> mapVisit = new ConcurrentHashMap<>();

    public static void initData() {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath + "options.txt")))){
                String timestamp = reader.lines().findFirst().get();
                currentTimeStamp = new Long(timestamp + "000");
                System.out.println("external timestamp = " + currentTimeStamp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ZipFile zipFile = new ZipFile(dataPath + "data.zip");
            zipFile.getFileHeaders().stream().forEach(item -> {
                if (item != null) {
                    try {
                        FileHeader fileHeader = (FileHeader)item;
                        if (fileHeader.getFileName().toString().contains("options")) {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(fileHeader)))){
                                String timestamp = reader.lines().findFirst().get();
                                currentTimeStamp = new Long(timestamp + "000");
                                System.out.println("timestamp = " + currentTimeStamp);
                            }
                        }
                        if (fileHeader.getFileName().toString().contains("users")) {
                            ObjectMapper mapper = new ObjectMapper();
                            new ObjectMapper()
                                    .readValue(zipFile.getInputStream(fileHeader), Users.class)
                                    .getUsers().stream()
                                    .forEach(element -> {
                                        try {
                                            Repository.mapUser.put(element.getId().toString(), mapper.writeValueAsBytes(element));
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                    });
                        }
                        if (fileHeader.getFileName().toString().contains("visits")) {
                            ObjectMapper mapper = new ObjectMapper();
                            new ObjectMapper()
                                    .readValue(zipFile.getInputStream(fileHeader), Visits.class)
                                    .getVisits().stream()
                                    .forEach(element -> {
                                        try {
                                            Repository.mapVisit.put(element.getId().toString(), mapper.writeValueAsBytes(element));
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                    });
                        }
                        if (fileHeader.getFileName().toString().contains("locations")) {
                            ObjectMapper mapper = new ObjectMapper();
                            new ObjectMapper()
                                    .readValue(zipFile.getInputStream(fileHeader), Locations.class)
                                    .getLocations().stream()
                                    .forEach(element -> {
                                        try {
                                            Repository.mapLocation.put(element.getId().toString(), mapper.writeValueAsBytes(element));
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                    });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("mapUser size = " + Repository.mapUser.size());
            System.out.println("End unzip");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
