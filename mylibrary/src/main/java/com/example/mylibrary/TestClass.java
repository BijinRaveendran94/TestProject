package com.example.mylibrary;

import android.os.Build;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TestClass  {

    private JSONArray nodes;

    public  static  ArrayList<JSONObject> nodesHash;
    public  static  List<JSONObject> waypoints;
    public  static  LatLng sourceNode;
    public  static  LatLng destinationNode;
    public  static  ArrayList<LatLng> visitedNodes;
    public  static  JSONObject shortestNodesHash;
    public  static  ArrayList<ArrayList<LatLng>> shortestWaypoints = new ArrayList<ArrayList<LatLng>>();


    public static List<JSONObject> getWaypointsForRoom(String json, JSONObject room) {
        List<JSONObject> arrayList = new ArrayList<JSONObject>();
        try {

            JSONObject metaData = new JSONObject(json);
            JSONArray buildings = metaData.getJSONArray("Countries").getJSONObject(0).getJSONArray("States").getJSONObject(0).getJSONArray("Cities").getJSONObject(0).getJSONArray("Buildings");
            JSONArray getCampusWaypoints = null;
            try {
                getCampusWaypoints = metaData.getJSONArray("Countries").getJSONObject(0).getJSONArray("States").getJSONObject(0).getJSONArray("Cities").getJSONObject(0).getJSONArray("CampusWaypoints");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray Floors = null;
            for (int i = 0; i < buildings.length(); i++) {

                try {
                    Floors = buildings.getJSONObject(i).getJSONArray("Floors");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (ClassCastException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < Floors.length(); j++) {

                    JSONArray Waypoints = Floors.getJSONObject(j).getJSONArray("Waypoints");
                    JSONArray Rooms = Floors.getJSONObject(j).getJSONArray("Rooms");

                    for (int k = 0; k < Rooms.length(); k++) {
                        String RoomName = Rooms.getJSONObject(k).getString("RoomName");
                        String RoomNumber = Rooms.getJSONObject(k).getString("RoomNumber");
                        if (RoomName.equalsIgnoreCase(room.getString("RoomName")) && RoomNumber.equalsIgnoreCase(room.getString("RoomNumber"))) {

                            List<JSONObject> jsonObject = new ArrayList<JSONObject>();

                            for (int m = 0; m < Waypoints.length(); m++) {
                                jsonObject.add(Waypoints.getJSONObject(m));
                            }
                            try {
                                for (int n = 0; n < getCampusWaypoints.length(); n++) {
                                    jsonObject.add(getCampusWaypoints.getJSONObject(n));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            arrayList = jsonObject;
                            break;
                        }
                    }
                }
            }

            return arrayList;
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (ClassCastException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return arrayList;
    }

    public static LatLng getNearestNodeFor(LatLng location, ArrayList<JSONObject> nodesHash) {
        try {
            LatLng selectedNode = null;

            double distance = 0;

            for (int v = 0; v < nodesHash.size(); v++) {
                JSONObject node = nodesHash.get(v);
                Iterator<String> keys = node.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String Pointb = key.replace("{", " ");
                    String PointBb = Pointb.replace("}", " ");
                    String[] latlongb = PointBb.split(",");
                    double latitude = Double.parseDouble(latlongb[0]);
                    double longitude = Double.parseDouble(latlongb[1]);
                    LatLng LatLng = new LatLng(latitude, longitude);

                    double dist = distance(location.latitude, location.longitude, LatLng.latitude, LatLng.longitude);

                    if (dist < distance || distance == 0) {
                        distance = dist;
                        selectedNode = LatLng;
                    }
                }
            }
            return selectedNode;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static double distance(double lat_a, double lng_a, double lat_b, double lng_b) {

        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).doubleValue();
    }

    public static ArrayList<JSONObject> getNodesForWaypoints(List<JSONObject> Waypoints) {
        ArrayList<JSONObject> node = new ArrayList<JSONObject>();
        try {
            for (int i = 0; i < Waypoints.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonObject = Waypoints.get(i);

                String PointA = jsonObject.getString("PointA");
                String PointB = jsonObject.getString("PointB");

                String ah = null;
                String bh = null;
                if (node.size() != 0) {
                    Boolean b = false;
                    Boolean a = false;

                    for (int j = 0; j < node.size(); j++) {
                        if (node.get(j).has(PointA)) {
                            a = true;
                            ah = Integer.toString(j);

                            break;
                        }
                    }

                    for (int j = 0; j < node.size(); j++) {

                        if (node.get(j).has(PointB)) {
                            b = true;
                            bh = Integer.toString(j);
                            break;
                        }
                    }
                    if (a) {
                        for (int j = 0; j < node.size(); j++) {

                            if (ah == Integer.toString(j)) {
                                JSONArray jsonArray = node.get(j).getJSONArray(PointA);

                                ArrayList<Object> jsonData = new ArrayList<Object>();
                                if (jsonArray.length() > 0) {

                                    for (int k = 0; k < jsonArray.length(); k++) {
                                        jsonData.add(jsonArray.get(k));
                                    }
                                    if (!jsonData.contains(jsonObject)) {
                                        jsonArray.put(jsonObject);
                                    }
                                    break;
                                } else {
                                    jsonArray.put(jsonObject);
                                }

                            }
                        }
                    } else {
                        for (int j = 0; j < node.size(); j++) {
                            if (node.get(j).has(PointA)) {
                                a = true;
                                ah = Integer.toString(j);

                                break;


                            }

                        }

                        if (a) {
                            for (int j = 0; j < node.size(); j++) {

                                if (ah == Integer.toString(j)) {
                                    JSONArray jsonArray = node.get(j).getJSONArray(PointA);

                                    ArrayList<Object> jsonData = new ArrayList<Object>();
                                    if (jsonArray.length() > 0) {

                                        for (int k = 0; k < jsonArray.length(); k++) {
                                            jsonData.add(jsonArray.get(k));
                                        }
                                        if (!jsonData.contains(jsonObject)) {
                                            jsonArray.put(jsonObject);
                                        }
                                        break;
                                    } else {
                                        jsonArray.put(jsonObject);
                                    }

                                }
                            }
                        } else {
                            JSONObject json = new JSONObject();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                json.put(PointA, new JSONArray(new Object[]{jsonObject}));
                            }
                            node.add(json);
                        }

                    }


                    if (b) {
                        for (int j = 0; j < node.size(); j++) {

                            if (bh == Integer.toString(j)) {
                                JSONArray jsonArray = node.get(j).getJSONArray(PointB);

                                ArrayList<Object> jsonData = new ArrayList<Object>();
                                if (jsonArray.length() > 0) {

                                    for (int k = 0; k < jsonArray.length(); k++) {
                                        jsonData.add(jsonArray.get(k));
                                    }
                                    if (!jsonData.contains(jsonObject)) {
                                        jsonArray.put(jsonObject);
                                    }
                                    break;
                                } else {
                                    jsonArray.put(jsonObject);
                                }


                            }

                        }
                    } else {

                        for (int j = 0; j < node.size(); j++) {
                            if (node.get(j).has(PointA)) {
                                a = true;
                                ah = Integer.toString(j);

                                break;


                            }

                        }

                        if (b) {
                            for (int j = 0; j < node.size(); j++) {

                                if (bh == Integer.toString(j)) {
                                    JSONArray jsonArray = node.get(j).getJSONArray(PointB);

                                    ArrayList<Object> jsonData = new ArrayList<Object>();
                                    if (jsonArray.length() > 0) {

                                        for (int k = 0; k < jsonArray.length(); k++) {
                                            jsonData.add(jsonArray.get(k));
                                        }
                                        if (!jsonData.contains(jsonObject)) {
                                            jsonArray.put(jsonObject);
                                        }
                                        break;
                                    } else {
                                        jsonArray.put(jsonObject);
                                    }

                                }
                            }
                        } else {
                            JSONObject json = new JSONObject();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                json.put(PointB, new JSONArray(new Object[]{jsonObject}));
                            }
                            node.add(json);
                        }

                    }


                } else {
                    JSONObject json = new JSONObject();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        json.put(PointA, new JSONArray(new Object[]{jsonObject}));
                    }
                    node.add(json);


                    JSONObject js = new JSONObject();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        js.put(PointB, new JSONArray(new Object[]{jsonObject}));
                    }
                    node.add(js);

                }

            }
            return node;
        } catch (JSONException e) {
            e.printStackTrace();


        } catch (ClassCastException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return node;
    }

    public static JSONObject getPathFromSource(LatLng location, LatLng destination, List<JSONObject> Waypoints) {
        try {


            nodesHash = getNodesForWaypoints(Waypoints);
            visitedNodes = new ArrayList<LatLng>();
            shortestNodesHash = new JSONObject();
            waypoints = Waypoints;
            sourceNode = getNearestNodeFor(location);
            destinationNode = getNearestNodeFor(destination);
            double distance = 0;

            JSONObject nodes = getPathFromSource(sourceNode, distance);

            return nodes;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static LatLng getNearestNodeFor(LatLng location) {
        try {

            LatLng selectedNode = null;

            double distance = 0;

            for (int v = 0; v < nodesHash.size(); v++) {
                JSONObject node = nodesHash.get(v);
                Iterator<String> keys = node.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String Pointb = key.replace("{", " ");
                    String PointBb = Pointb.replace("}", " ");
                    String[] latlongb = PointBb.split(",");
                    double latitude = Double.parseDouble(latlongb[0]);
                    double longitude = Double.parseDouble(latlongb[1]);
                    LatLng LatLng = new LatLng(latitude, longitude);

                    double dist = distance(location.latitude, location.longitude, LatLng.latitude, LatLng.longitude);

                    if (dist < distance || distance == 0) {
                        distance = dist;
                        selectedNode = LatLng;
                    }
                }
            }

            return selectedNode;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static JSONObject getPathFromSource(LatLng source, double distance) {

        try {

            if (source.toString().equals(destinationNode.toString())) {
                JSONArray jsonArray = new JSONArray();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Distance", distance);
                jsonArray.put(source.toString());
                jsonObject.put("Nodes", (Object) jsonArray);
                return jsonObject;
            }

            if (visitedNodes.contains(source)) {

                JSONObject jsonObject = new JSONObject();
                String sourceName = source.toString();
                if (shortestNodesHash.length() > 0) {
                    Iterator<String> keys = shortestNodesHash.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (key.equalsIgnoreCase(sourceName)) {
                            jsonObject = shortestNodesHash.getJSONObject(sourceName);
                            return jsonObject;
                        }
                    }
                } else {
                    return null;
                }
                return null;
            }

            visitedNodes.add(source);
            ArrayList<JSONObject> connectingNodes = new ArrayList<JSONObject>();

            List<Double> integerList = new ArrayList<Double>();
            List<JSONObject> items = new ArrayList<JSONObject>();
            JSONArray jsonArray = new JSONArray();

            for (int v = 0; v < nodesHash.size(); v++) {
                JSONObject node = nodesHash.get(v);
                Iterator<String> keys = node.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String Pointb = key.replace("{", " ");
                    String PointBb = Pointb.replace("}", " ");
                    String[] latlongb = PointBb.split(",");
                    double latitude = Double.parseDouble(latlongb[0]);
                    double longitude = Double.parseDouble(latlongb[1]);

                    LatLng LatLng = new LatLng(latitude, longitude);

                    if (LatLng.equals(source)) {

                        jsonArray = nodesHash.get(v).getJSONArray(key);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            double Distance = (double) jsonObject.get("Distance");
                            integerList.add(Distance);
                            String PointB = jsonObject.getString("PointB");
                            String PointA = jsonObject.getString("PointA");
                            JSONObject jsondata = new JSONObject();
                            jsondata.put("Distance", Distance);
                            jsondata.put("PointA", PointA);
                            jsondata.put("PointB", PointB);
                            items.add(jsondata);
                        }
                        Collections.sort(integerList);

                        for (int j = 0; j < integerList.size(); j++) {
                            double dist = integerList.get(j);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                double Distance = (double) jsonObject.get("Distance");
                                if (dist == Distance) {
                                    for (int k = 0; k < items.size(); k++) {
                                        double distanceA = (double) items.get(k).get("Distance");
                                        String PointA = jsonArray.getJSONObject(i).getString("PointA");
                                        String PointB = jsonArray.getJSONObject(i).getString("PointB");

                                        String pointa = items.get(k).getString("PointA");
                                        String pointb = items.get(k).getString("PointB");
                                        if (distanceA == dist && pointa.equalsIgnoreCase(PointA) && pointb.equalsIgnoreCase(PointB)) {
                                            connectingNodes.add(jsonObject);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            JSONArray nodes = null;
            double minDistance = 0;

            for (JSONObject edge : connectingNodes) {
                JSONObject connectingNode = edge;
                String PointA = connectingNode.getString("PointA");
                String Pointa = PointA.replace("{", " ");
                String PointAa = Pointa.replace("}", " ");
                String[] latlonga = PointAa.split(",");
                double latitudea = Double.parseDouble(latlonga[0]);
                double longitudea = Double.parseDouble(latlonga[1]);

                String PointB = connectingNode.getString("PointB");
                String Pointb = PointB.replace("{", " ");
                String PointBb = Pointb.replace("}", " ");
                String[] latlongb = PointBb.split(",");
                double latitudeb = Double.parseDouble(latlongb[0]);
                double longitudeb = Double.parseDouble(latlongb[1]);

                LatLng PointaLatLng = new LatLng(latitudea, longitudea);
                LatLng PointbLatLng = new LatLng(latitudeb, longitudeb);

                String Distance = connectingNode.getString("Distance");
                double distanceNde = Double.parseDouble(Distance);
                JSONObject nodesDict = null;

                if (PointaLatLng.equals(source)) {
                    nodesDict = getPathFromSource(PointbLatLng, distanceNde);

                } else {
                    nodesDict = getPathFromSource(PointaLatLng, distanceNde);
                }

                if (nodesDict != null) {

                    double newDistance = (double) nodesDict.get("Distance") + distance;

                    if (newDistance < minDistance || minDistance == 0) {
                        minDistance = newDistance;
                        nodes = new JSONArray();
                        JSONArray jsonArray1 = null;
                        jsonArray1 = (JSONArray) nodesDict.get("Nodes");
                        nodes = jsonArray1;
                    }
                }
            }

            if (nodes != null) {
                if (nodes.length() > 0) {
                    ArrayList<LatLng> newNodes = new ArrayList<LatLng>();
                    newNodes.add(source);

                    for (int j = 0; j < nodes.length(); j++) {

                        Object latLng = nodes.get(j);
                        String Point = latLng.toString();


                        String Pointa = Point.replace("lat/lng:", " ");
                        String PointAa = Pointa.replace("(", " ");
                        String Pointb = PointAa.replace(")", " ");
                        String[] latlonga = Pointb.split(",");
                        double latitudea = Double.parseDouble(latlonga[0]);
                        double longitudea = Double.parseDouble(latlonga[1]);
                        LatLng latlg = new LatLng(latitudea, longitudea);
                        newNodes.add(latlg);

                    }

                    shortestWaypoints.add(newNodes);
                    JSONObject nodesDictionary = new JSONObject();

                    nodesDictionary.put("Distance", minDistance);
                    JSONArray arrays = new JSONArray();
                    for (LatLng node : newNodes) {
                        arrays.put(node);
                    }
                    nodesDictionary.put("Nodes", (Object) arrays);
                    shortestNodesHash.put(source.toString(), nodesDictionary);
                    return nodesDictionary;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (ClassCastException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
