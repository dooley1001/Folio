package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/*
.
.MAIN SELIALIZER FOR OUR COIN LIST
.
.
 */
public class CoinList {
    /*
    *@JsonAnyGetter is annotated at non-static, no-argument method to serialize a Java Map into JSON.
     The return type of this method must be of Map type.
     @JsonAnySetter is annotated at non-static two-argument method or at object property of Map type.
     In the two-argument method, first one is for key and second one is for value.
     @JsonAnySetter can work as fallback during deserialization of JSON.
     It means if logical properties are available for some JSON fields and not for all then
     @JsonAnySetter will deserialize the rest of the JSON fields in Map.                                         https://www.concretepage.com/jackson-api/jackson-jsonanygetter-and-jsonanysetter-example

     *
     * The class is used for the serializing of the data keys in the json. Since the objects
     * have mutable keys we can use the @jsonanysetter anotation to tell the mapper to send all matching JSON through here
     */
    public static class Data {

        private List<DataNode> dataList;
        @JsonAnySetter
        public void setDataList(String key, DataNode dataNode) {
            if(dataList == null) dataList = new ArrayList<>();
            dataList.add(dataNode);
        }
    }
    /**
     * Regular JSON members.
     */
    @JsonProperty("Message")
    private String Message;
    @JsonProperty("Data")
    private Data data;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}




