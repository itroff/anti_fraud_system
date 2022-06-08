package antifraud.models;

public class Rule {

    public enum Action {
        EXCEPTION, INCREASE_ALLOWED, DECREASE_ALLOWED, INCREASE_MANUAL, DECREASE_MANUAL
    }

    public Rule() {

    }

    public Rule(TransactionResult result, TransactionResult feedback) {
        this.result = result;
        this.feedback = feedback;
    }

    private TransactionResult result;

    public TransactionResult getResult() {
        return result;
    }

    public void setResult(TransactionResult result) {
        this.result = result;
    }

    public TransactionResult getFeedback() {
        return feedback;
    }

    public void setFeedback(TransactionResult feedback) {
        this.feedback = feedback;
    }

    private TransactionResult feedback;

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (!(other instanceof Rule)) {
            return false;
        }

        Rule another = (Rule) other;
        if (this.result == another.result && this.feedback == another.feedback) {
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        return 17 + (result == null ? 0 : result.hashCode()) + (feedback == null ? 0 : feedback.hashCode());
    }

}
