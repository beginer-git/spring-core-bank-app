package org.example.account;

import org.example.user.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final Map<Integer, Account> accountMap;

    private int idCounter;

    private final AccountProperties accountProperties;

    public AccountService(AccountProperties accountProperties) {
        this.accountProperties = accountProperties;
        this.accountMap = new HashMap<>();
        this.idCounter = 0;
    }

    public Account createAccount(User user) {
        idCounter++;
        Account account = new Account(idCounter, user.getId(), accountProperties.getDefaultAccountAmount());
        accountMap.put(account.getId(), account);
        return account;
    }

    public Optional<Account> findAccountById(int id) {
        return Optional.ofNullable(accountMap.get(id));
    }

    public List<Account> getAllUserAccounts(int userId) {
        return accountMap.values()
                .stream()
                .filter(account -> account.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public void depositAcccount(int accountId, int moneyToDeposit) {
        var account = findAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException
                        (String.format("No such account: id=%d", accountId)));

        if (moneyToDeposit <= 0) {
            throw new IllegalArgumentException
                    (String.format("Cannot deposit not positive amount: amount=%d", moneyToDeposit));
        }
        account.setMoneyAmount(account.getMoneyAmount() + moneyToDeposit);
    }

    public void withdrawFromAccount(int accountId, int amountToWithdraw) {
        var account = findAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException
                        (String.format("No such account: id=%d", accountId)));

        if (amountToWithdraw <= 0) {
            throw new IllegalArgumentException
                    (String.format("Cannot withdraw not positive amount: amount=%d", amountToWithdraw));
        }

        if (account.getMoneyAmount() < amountToWithdraw) {
            throw new IllegalArgumentException
                    (String.format("Cannot withdraw from account:" +
                                    " id=%d, moneyAmount=%d, attemptedWithdraw=%s",
                            accountId, account.getMoneyAmount(), amountToWithdraw));
        }
        account.setMoneyAmount(account.getMoneyAmount() - amountToWithdraw);
    }

    public Account closeAccount(int accountId) {
        var accountToRemove = findAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException
                        (String.format("No such account: id=%d", accountId)));

        List<Account> accountList = getAllUserAccounts(accountToRemove.getUserId());

        if (accountList.size() == 1) {
            throw new IllegalArgumentException("Cannot close the only one account");
        }

        Account accountToDeposit = accountList.stream()
                .filter(it -> it.getId() != accountId)
                .findFirst()
                .orElseThrow();

        accountToDeposit.setMoneyAmount(accountToDeposit.getMoneyAmount() + accountToRemove.getMoneyAmount());

        accountMap.remove(accountId);

        return accountToRemove;
    }

    public void transfer(int fromAccountId, int toAccountId, int amountToTransfer) {
        var accountFrom = findAccountById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException
                        (String.format("No such account: id=%d", fromAccountId)));
        var accountTo = findAccountById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException
                        (String.format("No such account: id=%d", toAccountId)));

        if (amountToTransfer <= 0) {
            throw new IllegalArgumentException
                    (String.format("Cannot transfer not positive amount: amount=%d",
                            amountToTransfer));
        }

        if (accountFrom.getMoneyAmount() < amountToTransfer) {
            throw new IllegalArgumentException
                    (String.format("Cannot transfer from account:" +
                                    " id=%d, moneyAmount=%d, attemptedTransfer=%d",
                            accountFrom, accountFrom.getMoneyAmount(), amountToTransfer));
        }

        int totalAmountToDeposit = (int) (accountTo.getUserId() != accountFrom.getUserId()
                ? (int) (amountToTransfer * (1 - accountProperties.getTransferCommission()))
                : amountToTransfer);

        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amountToTransfer);
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + totalAmountToDeposit);
    }
}
