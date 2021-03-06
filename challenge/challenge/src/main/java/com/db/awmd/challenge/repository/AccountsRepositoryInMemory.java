package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }
  
  @Override
  public void transitionAmount(String fromAccount, String toAccount, BigDecimal amount) {

	  public synchronized void transitionAmount(String fromAccount, String toAccount, BigDecimal amount) {
	  if (getAccount(fromAccount) != null && getAccount(toAccount) != null) {
			Account acc_from = getAccount(fromAccount);
			boolean deb_flag = debitedFromAccount(acc_from, amount);
			if (deb_flag) {
				Account acc_to = getAccount(toAccount);
				boolean cre_Flag = creditedToAccount(acc_to, amount);
				if (cre_Flag) {
					sendNotification(acc_from, acc_to, amount);
				} else {
					throw new InsufficientBalanceException(" Insufficient balance ....!!!");
				}
			}
		}
	}
	public boolean debitedFromAccount(Account account, BigDecimal amount) {
		if (amount.doubleValue() < account.getBalance().doubleValue()) {
			account.setBalance(account.getBalance().subtract(amount));
			return true;
		} else {
			return false;
		}
	}
	public boolean creditedToAccount(Account account, BigDecimal amount) {
		account.setBalance(account.getBalance().add(amount));
		return true;
	}
	public void sendNotification(Account accountFrom, Account accountTo, BigDecimal amount) {
		try {
			String acc_from = amount + " has been transfered to " + accountTo.getAccountId()
					+ " & your Current Balance is : " + accountFrom.getBalance();
			String acc_to = amount + " has been transfered from " + accountFrom.getAccountId()
					+ " & your Current Balance is : " + accountTo.getBalance();
			notificationService.notifyAboutTransfer(accountFrom, acc_from);
			notificationService.notifyAboutTransfer(accountTo, acc_to);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  

}
