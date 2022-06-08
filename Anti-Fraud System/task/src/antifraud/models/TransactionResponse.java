package antifraud.models;

import java.util.Map;

public class TransactionResponse {
    private TransactionResult result;
    private String info = "none";

    public TransactionResult getResult() {
        return result;
    }

    public void setResult(TransactionResult result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void addInfo(String val) {
        if (this.info.equals("none")) {
            this.info = val;
        } else {
            this.info += ", " + val;
        }
    }

    public void addInfo(Map<String, TransactionResult> checks) {
        if (checks.containsValue(TransactionResult.PROHIBITED)) {
            this.result = TransactionResult.PROHIBITED;
        } else if (checks.containsValue(TransactionResult.MANUAL_PROCESSING)) {
            this.result = TransactionResult.MANUAL_PROCESSING;
        } else {
            this.result = TransactionResult.ALLOWED;
            return;
        }
        for (Map.Entry<String, TransactionResult> entry : checks.entrySet()) {
            if (entry.getValue() == this.result) {
                if (this.info.equals("none")) {
                    this.info = entry.getKey();
                } else {
                    this.info += ", " + entry.getKey();
                }
            }
        }

    }

}
