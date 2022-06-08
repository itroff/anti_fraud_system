package antifraud.models;

import javax.persistence.*;
import java.util.Map;

@Table(name = "card")
@Entity(name = "card")
public class Card implements CrudInterface {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column
    private String number;

    public Card() {

    }

    public Card(String cardNumber) {
        this.number = cardNumber;
    }

    public boolean validate() {
        return validateCard(this.number);
    }

    public static boolean validateCard(String cardNumber) {
        if (cardNumber.matches("^4[0-9]{12}(?:[0-9]{3})?$")) {
            return isValidLuhn(cardNumber);
        }
        return false;
    }

    private static boolean isValidLuhn(String value) {
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return (sum % 10) == 0;
    }

    public String value() {
        return this.number;
    }

    public Map<String, String> removeMsg() {
        return Map.of("status", "Card " + this.number + " successfully removed!");
    }

}
