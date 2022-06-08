package antifraud.models;

import java.util.Map;

public interface CrudInterface {

    public boolean validate();

    public String value();

    public Map<String, String> removeMsg();

}
