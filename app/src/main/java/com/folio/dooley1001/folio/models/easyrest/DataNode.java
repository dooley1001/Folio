package com.folio.dooley1001.folio.models.easyrest;
import com.fasterxml.jackson.annotation.JsonProperty;

//coin
public class DataNode {
    @JsonProperty("Id")
    private String Id;

    @JsonProperty("Name")
    private String Name;

    public String getId() {
        return Id;
    }

    public DataNode setId(String id) {
        Id = id;
        return this;
    }
    public String getName() {
        return Name;
    }

    public DataNode setName(String name) {
        Name = name;
        return this;
    }
}
