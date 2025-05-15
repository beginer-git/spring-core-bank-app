package org.example.operations.processors;

import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class DepositAccountProcessor implements OperationCommandProcessor {

    private final Scanner scanner;

    private final AccountService accountService;

    public DepositAccountProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id: ");
        int accountId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter amount to deposit: ");
        int amountToDeposit = Integer.parseInt(scanner.nextLine());

        try {
            accountService.depositAcccount(accountId, amountToDeposit);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("failed deposited amount %d to account id %d",
                    accountId,
                    amountToDeposit));
        }

        System.out.printf(
                "Successfully deposited amount %d to accountId %d",
                amountToDeposit,
                accountId
        );
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
