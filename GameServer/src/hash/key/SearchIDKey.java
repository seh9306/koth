package hash.key;

public class SearchIDKey extends SignalKey {
    private String id;

    public SearchIDKey(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}