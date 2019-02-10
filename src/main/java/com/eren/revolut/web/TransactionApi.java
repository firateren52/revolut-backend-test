package com.eren.revolut.web;


import com.eren.revolut.model.web.TransferRequest;
import com.eren.revolut.model.web.TransactionResponse;
import com.eren.revolut.service.TransactionService;
import org.jooby.mvc.Body;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;

import javax.inject.Inject;

@Path("/transactions")
public class TransactionApi {
    private final TransactionService transactionService;

    @Inject
    public TransactionApi(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    public TransactionResponse transfer(@Body TransferRequest request) {
        return new TransactionResponse(transactionService.transfer(request));
    }

}
