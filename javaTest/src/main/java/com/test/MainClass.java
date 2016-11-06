package com.test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.IOException;
import java.net.UnknownHostException;

public class MainClass {

    private static AppInfo getAppInfoFromMongoDB() {
        try {

            String mongoUrl = "mongodb://wangsc:351489@ds053126.mlab.com:53126/app-manager";
            String collctionName = "app-info";
            //
            String whereKey = "PackageName";
            Object whereValue = "com.wang17.religiouscalendar";
            String orderKey = "decade";
            Object orderValue = 1;
            //
            MongoClientURI uri = new MongoClientURI(mongoUrl);
            MongoClient client = new MongoClient(uri);
            DB db = client.getDB(uri.getDatabase());
            DBCollection songs = db.getCollection(collctionName);
            //
            BasicDBObject findQuery = new BasicDBObject(whereKey, new BasicDBObject("$gte", whereValue));
            BasicDBObject orderBy = new BasicDBObject(orderKey, orderValue);

            DBCursor docs = songs.find(findQuery).sort(orderBy);

            while (docs.hasNext()) {
                DBObject doc = docs.next();
                return new AppInfo((String)doc.get("PackageName"), (int)doc.get("VersionCode"), (String)doc.get("VersionName"), (String)doc.get("LoadUrl"), (String)doc.get("AccessToken"));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) throws UnknownHostException {
        try {
            AppInfo info =  getAppInfoFromMongoDB();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

