package pgxp.pto.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum NameFinder {

    DATE("en-ner-date.bin"),
    LOCATION("en-ner-location.bin"),
    MONEY("en-ner-money.bin"),
    ORGANIZATION("en-ner-organization.bin"),
    PERCENTAGE("en-ner-percentage.bin"),
    PERSON("en-ner-person.bin"),
    TIME("en-ner-time.bin");

    private final String value;

    private NameFinder(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static Map getMap() {
        Map<NameFinder, String> map = new ConcurrentHashMap<>();
        for (NameFinder userType : NameFinder.values()) {
            map.put(userType, userType.toString());
        }
        return map;
    }

}
