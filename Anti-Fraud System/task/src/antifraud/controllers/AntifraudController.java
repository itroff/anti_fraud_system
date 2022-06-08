package antifraud.controllers;

import antifraud.models.*;
import antifraud.repositories.CardRepository;
import antifraud.repositories.IPRepository;
import antifraud.repositories.LimitRepository;
import antifraud.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;


@RestController
public class AntifraudController {

    @Autowired
    IPRepository ipRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    Limit limit;

    @Autowired
    LimitRepository limitRepository;

    private static ResponseEntity<CrudInterface> validateAndSave(CrudInterface obj, CrudRepository repository) {
        if (obj.validate()) {
            List<CrudInterface> list = (List<CrudInterface>) repository.findAll();
            for (CrudInterface ipObj : list) {
                if (obj.value().equals(ipObj.value())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            repository.save(obj);
            return new ResponseEntity<>(obj, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<Map<String, String>> delete(CrudInterface obj, CrudRepository repository) {
        if (obj.validate()) {
            List<CrudInterface> list = (List<CrudInterface>) repository.findAll();
            for (CrudInterface ipObj : list) {
                if (obj.value().equals(ipObj.value())) {
                    repository.delete(ipObj);
                    return new ResponseEntity<>(obj.removeMsg(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<TransactionResponse> transaction(@RequestBody TransactionRequest request) {

        TransactionResponse response = new TransactionResponse();

        try {
            if (!IP.validateIP(request.getIp()) || !Card.validateCard(request.getNumber())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (request.getAmount() <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // TODO validate date, region?
            Map<String, TransactionResult> checks = new TreeMap<>();
            System.out.println("Amount: " + request.getAmount());
            System.out.println("limitAllowed: " + limit.getLimitAllowed());
            System.out.println("limitManual: " + limit.getLimitManual());
            if (request.getAmount() <= limit.getLimitAllowed()) {
                checks.put("amount", TransactionResult.ALLOWED);
            } else if (request.getAmount() <= limit.getLimitManual()) {
                checks.put("amount", TransactionResult.MANUAL_PROCESSING);
            } else {
                checks.put("amount", TransactionResult.PROHIBITED);
            }

            if (cardRepository.existsByNumber(request.getNumber())) {
                checks.put("card-number", TransactionResult.PROHIBITED);
            }

            if (ipRepository.existsByIp(request.getIp())) {
                checks.put("ip", TransactionResult.PROHIBITED);
            }
            List<TransactionRequest> list = transactionRepository.findByNumber(request.getNumber());
            list.add(request);
            TransactionRequest.findAllTransactionByNumberAndDateBetween(list, request, checks);

            response.addInfo(checks);
            request.setResult(response.getResult());
            transactionRepository.save(request);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<CrudInterface> suscpIp(@RequestBody Map<String, String> map) {
        return validateAndSave(new IP(map.get("ip")), ipRepository);

    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<CrudInterface> stolenCard(@RequestBody Map<String, String> map) {
        return validateAndSave(new Card(map.get("number")), cardRepository);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<Map<String, String>> deleteIP(@PathVariable String ip) {
        return delete(new IP(ip), ipRepository);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<Map<String, String>> deleteCard(@PathVariable String number) {
        return delete(new Card(number), cardRepository);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<List<IP>> ips() {
        return new ResponseEntity<>((List<IP>) ipRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<List<Card>> cards() {
        return new ResponseEntity<>((List<Card>) cardRepository.findAll(), HttpStatus.OK);
    }

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity<TransactionRequest> feedback(@RequestBody TransactionRequest request) {
        Optional<TransactionRequest> transaction = transactionRepository.findById(request.getTransactionId());
        if (transaction.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TransactionRequest trans = transaction.get();
        // TODO result wrong format -> Bad request
        if (trans.getFeedback() != TransactionResult.UNDEFINED) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Rule.Action action = limit.processFeedback(transaction.get(), request);
        limitRepository.save(limit);
        if (action == Rule.Action.EXCEPTION) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        trans.setFeedback(request.getFeedback());
        transactionRepository.save(trans);
        return new ResponseEntity<>(trans, HttpStatus.OK);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity<List<TransactionRequest>> history() {
        return new ResponseEntity<>((List<TransactionRequest>) transactionRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity<List<TransactionRequest>> transaction(@PathVariable String number) {
        if (!Card.validateCard(number)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<TransactionRequest> results = transactionRepository.findByNumber(number);
        if (results.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}
