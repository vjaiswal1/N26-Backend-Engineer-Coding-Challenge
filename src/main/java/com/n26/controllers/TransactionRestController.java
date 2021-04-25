package com.n26.controllers;

import com.n26.models.Transaction;
import com.n26.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Rest controller that handles POST and DELETE transaction calls
 */
@RestController("/transactions")
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;



    @PostMapping
    public ResponseEntity<?> add(@RequestBody final Transaction transaction) {
        transactionService.addWithSyncronization(transaction);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?> delete() {
        transactionService.delete();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
