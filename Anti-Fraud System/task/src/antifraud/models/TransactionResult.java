package antifraud.models;


import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionResult {
    UNDEFINED(""), ALLOWED("ALLOWED"), MANUAL_PROCESSING("MANUAL_PROCESSING"), PROHIBITED("PROHIBITED");
    // UNDEFINED, ALLOWED, MANUAL_PROCESSING, PROHIBITED;


    private final String code;

    TransactionResult(String code) {
        System.out.println("constr");
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.code;
    }


}
