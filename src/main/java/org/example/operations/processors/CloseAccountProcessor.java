package org.example.operations.processors;

import org.example.account.Account;
import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;
import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CloseAccountProcessor implements OperationCommandProcessor {

    private final Scanner scanner;

    private final AccountService accountService;

    private final UserService userService;

    public CloseAccountProcessor(
            Scanner scanner,
            AccountService accountService,
            UserService userService
    ) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id to close: ");
        int accountId = Integer.parseInt(scanner.nextLine());
        Account account = accountService.closeAccount(accountId);
        User user = userService.findUserById(account.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("No such user with id=%d", account.getUserId())));
        user.getAccountList().remove(account);

        System.out.printf("Account successfully closed with id=%d", accountId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
