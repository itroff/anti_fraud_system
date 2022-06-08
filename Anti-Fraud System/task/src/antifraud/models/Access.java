package antifraud.models;


public class Access {

    public enum AccessEnum {
        LOCK, UNLOCK
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public AccessEnum getOperation() {
        return operation;
    }

    public void setOperation(AccessEnum operation) {
        this.operation = operation;
    }

    private AccessEnum operation;
}
