package cn.com.test.event;

import org.json.JSONObject;

public class AddressEvent {

    private JSONObject address;

    public AddressEvent(JSONObject address) {
        this.address = address;
    }

    public JSONObject getAddress() {
        return address;
    }
}
