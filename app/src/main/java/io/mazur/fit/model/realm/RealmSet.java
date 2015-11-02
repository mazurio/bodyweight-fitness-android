package io.mazur.fit.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class RealmSet extends RealmObject {
    @Ignore
    public static final String BODYWEIGHT = "bodyweight";

    @Ignore
    public static final String TIME = "time";

    private String id;
    private String type;

    private int value;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
