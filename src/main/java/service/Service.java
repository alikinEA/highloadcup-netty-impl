package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import models.Location;
import models.Result;
import models.User;
import models.Visit;
import repository.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Alikin E.A. on 29.05.18.
 */
public class Service {

    private static final byte[] EMPTY = "{}".getBytes();
    private static final Result NOT_FOUND_RESULT = new Result(EMPTY,HttpResponseStatus.NOT_FOUND);
    private static final Result BAD_REQUEST_RESULT = new Result(EMPTY,HttpResponseStatus.BAD_REQUEST);
    private static final Result OK_EMTY = new Result(EMPTY,HttpResponseStatus.OK);

    private static final ObjectMapper mapper = new ObjectMapper();


    private static final String URI_USERS = "/users";
    private static final String URI_LOCATIONS = "/locations";
    private static final String URI_VISITS = "/visits";

    private static final String URI_VISITS_NEW = "/visits/new";
    private static final String URI_LOCATIONS_NEW = "/locations/new";
    private static final String URI_USERS_NEW = "/users/new";



    public static String getIdFromUri(String uri) {
        String[] elements = uri.split("/");
        return elements[2];
    }


    public static Result handle(FullHttpRequest req) throws IOException {
        if (req.uri().startsWith(URI_USERS)) {
            return handleUser(req);
        } else if (req.uri().startsWith(URI_VISITS)) {
            return handleVisits(req);
        } else if (req.uri().startsWith(URI_LOCATIONS)) {
            return handleLocations(req);
        } else {
            throw new RuntimeException();
        }
    }


    private static Result handleVisits(FullHttpRequest req) throws IOException {
        HttpMethod method = req.method();
        String uri = req.uri();
        if (method.equals(HttpMethod.GET)) {
            byte[] visit = Repository.mapVisit.get(getIdFromUri(uri));
            if (visit != null) {
                return new Result(visit, HttpResponseStatus.OK);
            } else {
                return NOT_FOUND_RESULT;
            }
        } else if (method.equals(HttpMethod.POST)) {
            if (uri.contains(URI_VISITS_NEW)) {
                String obj = req.content().toString(StandardCharsets.UTF_8);
                Visit visit = mapper.readValue(obj, Visit.class);
                if (visit.getId() == null
                        || visit.getUser() == null
                        || visit.getLocation() == null
                        || visit.getMark() == null
                        || visit.getVisited_at() == null) {
                    return BAD_REQUEST_RESULT;
                } else {
                    Repository.mapVisit.put(visit.getId().toString(), obj.getBytes());
                    return OK_EMTY;
                }

            }
        }

        return NOT_FOUND_RESULT;
    }

    private static Result handleUser(FullHttpRequest req) throws IOException {
        HttpMethod method = req.method();
        String uri = req.uri();
        if (method.equals(HttpMethod.GET)) {
            byte[] user = Repository.mapUser.get(getIdFromUri(uri));
            if (user != null) {
                return new Result(user, HttpResponseStatus.OK);
            } else {
                return NOT_FOUND_RESULT;
            }
        } else if (method.equals(HttpMethod.POST)) {
            if (uri.contains(URI_USERS_NEW)) {
                String obj = req.content().toString(StandardCharsets.UTF_8);
                User user = mapper.readValue(obj, User.class);
                if (user.getId() == null
                        || user.getBirth_date() == null
                        || user.getEmail() == null
                        || user.getFirst_name() == null
                        || user.getGender() == null
                        || user.getLast_name() == null) {
                    return BAD_REQUEST_RESULT;
                } else {
                    Repository.mapUser.put(user.getId().toString(), obj.getBytes());
                    return OK_EMTY;
                }

            }
        }

        return NOT_FOUND_RESULT;
    }

    private static Result handleLocations(FullHttpRequest req) throws IOException {
        HttpMethod method = req.method();
        String uri = req.uri();
        if (method.equals(HttpMethod.GET)) {
            byte[] location = Repository.mapLocation.get(getIdFromUri(uri));
            if (location != null) {
                return new Result(location, HttpResponseStatus.OK);
            } else {
                return NOT_FOUND_RESULT;
            }
        } else if (method.equals(HttpMethod.POST)) {
            String obj = req.content().toString(StandardCharsets.UTF_8);
            if (uri.contains(URI_LOCATIONS_NEW)) {
                Location location = mapper.readValue(obj, Location.class);
                if (location.getId() == null
                        || location.getCity() == null
                        || location.getCountry() == null
                        || location.getDistance() == null
                        || location.getPlace() == null) {
                    return BAD_REQUEST_RESULT;
                } else {
                    Repository.mapLocation.put(location.getId().toString(), obj.getBytes());
                    return OK_EMTY;
                }
            } else {
                String urlParam = getIdFromUri(uri);
                byte[] locationOldB = Repository.mapLocation.get(urlParam);
                if (locationOldB != null) {
                    Location locationOld = mapper.readValue(locationOldB,Location.class);
                    Location location = mapper.readValue(obj, Location.class);
                    if (location.getCity() != null) {
                        locationOld.setCity(location.getCity());
                    }
                    if (location.getDistance() != null) {
                        locationOld.setDistance(location.getDistance());
                    }
                    if (location.getPlace() != null) {
                        locationOld.setPlace(location.getPlace());
                    }
                    if (location.getCountry() != null) {
                        locationOld.setCountry(location.getCountry());
                    }
                    Repository.mapLocation.put(urlParam, mapper.writeValueAsBytes(locationOld));
                    return OK_EMTY;
                } else {
                    return NOT_FOUND_RESULT;
                }
            }
        }

        return NOT_FOUND_RESULT;
    }
}
