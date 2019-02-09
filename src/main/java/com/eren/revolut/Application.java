package com.eren.revolut;

import com.eren.revolut.repository.AccountRepository;
import com.eren.revolut.repository.impl.AccountMemoryRepository;
import com.eren.revolut.service.AccountService;
import com.eren.revolut.service.impl.AccountServiceImpl;
import com.eren.revolut.web.AccountApi;
import com.eren.revolut.web.TransactionApi;
import org.jooby.Jooby;
import org.jooby.json.Jackson;

public class Application extends Jooby {

    {
        use(new Jackson());

        use((env, conf, binder) -> {
            binder.bind(AccountService.class).to(AccountServiceImpl.class);
            binder.bind(AccountRepository.class).to(AccountMemoryRepository.class);
        });

        use(AccountApi.class);
        use(TransactionApi.class);
    }

    public static void main(String[] args) {
        run(Application::new, args);
    }
}
