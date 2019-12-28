package com.sepankasuite.timer;

public class DataHistory {

    String name;
    String type;
    String version_number;
    String feature;
    String id;

    public DataHistory (String id, String name, String type, String version_number, String feature ) {
        this.id=id;
        this.name=name;
        this.type=type;
        this.version_number=version_number;
        this.feature=feature;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVersion_number() {
        return version_number;
    }

    public String getFeature() {
        return feature;
    }
}
