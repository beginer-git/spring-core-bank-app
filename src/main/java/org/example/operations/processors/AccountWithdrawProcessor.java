package org.example.operations.processors;

import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountWithdrawProcessor implements OperationCommandProcessor {

    private final Scanner scanner;

    private final AccountService accountService;

    public AccountWithdrawProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id: ");
        int accountId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter amount to withdraw: ");
        int amountToWithdraw = Integer.parseInt(scanner.nextLine());

        try {
            accountService.withdrawFromAccount(accountId, amountToWithdraw);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("failed withdraw to accountId %d amount %d",
                    accountId,
                    amountToWithdraw));
        }

        System.out.printf(
                "Successfully withdraw amount %d to accountId %d",
                amountToWithdraw,
                accountId
        );
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}
