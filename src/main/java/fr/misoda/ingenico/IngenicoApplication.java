package fr.misoda.ingenico;

import fr.misoda.ingenico.model.BankAccount;
import fr.misoda.ingenico.model.Currency;
import fr.misoda.ingenico.repository.BankAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class IngenicoApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(IngenicoApplication.class);

	@Autowired
	private BankAccountRepository repository;

	private BankAccount currentAccount;

	public static void main(String[] args) {
		logger.info("Hello from Ingenico");
		SpringApplication.run(IngenicoApplication.class, args);
		logger.info("See you again soon !!! ");
	}

	private void displayMainMenu() {
		System.out.println("Please select an operation: ");
		System.out.println("1. Login ");
		System.out.println("2. New account ");
		System.out.print("Your choice: ");

	}

	private void displayMenuAccount() {
		System.out.println("Please select an operation: ");
		System.out.println("1. Depot ");
		System.out.println("2. Withdraw ");
		System.out.println("3. Check account info ");
		System.out.println("4. Exit");
		System.out.println("Your choice: ");
	}

	@Override
	public void run(String... args) {
		repository.deleteAll();
		Scanner scanner = new Scanner(System.in);

		while (null == currentAccount) {
			displayMainMenu();
			int cmd = scanner.nextInt();
			switch (cmd) {
				case 1:
					login(scanner);
					break;
				case 2:
					createNewAccount(scanner);
					break;
				default:
					displayMainMenu();
					break;
			}
		}

		System.out.println("Current account: " + currentAccount);
		int cmd = 0 ;
		while (cmd != 4) {
			displayMenuAccount();
			cmd = scanner.nextInt();
			switch (cmd) {
				case 1:
					depot(scanner);
					break;
				case 2:
					withdraw(scanner);
					break;
				case 3:
					logger.info(currentAccount.toString());
					break;
				default:
					displayMenuAccount();
					break;
			}
		}
	}

	private void withdraw(Scanner scanner) {
		if (badCurrency(scanner))  {
			logger.warn("Bad currency");
			return;
		}

		long amount = getAmount(scanner);
		if (amount <= 0) {
			logger.warn("Bad amount");
			return;
		}

		if (currentAccount.getBalance() < amount) {
			logger.warn("You don't have enough $$$ my friend! ");
			return;
		}

		currentAccount.add(Math.negateExact(amount));
		repository.save(currentAccount);
		logger.info("Operation {} {} {}succeeded, new balance is: {} ", "withdraw", amount, currentAccount.getCurrency(),  currentAccount.getBalance());
	}

	private void depot(Scanner scanner) {
		if (badCurrency(scanner))  {
			logger.warn("Bad currency");
			return;
		}

		long amount = getAmount(scanner);
		if (amount <= 0) {
			logger.warn("Bad amount");
			return;
		}

		currentAccount.add(amount);
		repository.save(currentAccount);
		logger.info("Operation {} {} {} succeeded, new balance is: {} ", "depot", amount, currentAccount.getCurrency(), currentAccount.getBalance());
	}

	private void login(Scanner scanner) {
		System.out.println("Enter account ID:");
		long id = scanner.nextLong();
		currentAccount = repository.findById(id).orElse(null);
	}

	private void createNewAccount(Scanner scanner) {
		int c = 0;
		while (c != 1 && c != 2) {
			System.out.println("Enter your account currency (USD/EUR) or exit:");
			System.out.println("1. EUR");
			System.out.println("2. USD");
			c = scanner.nextInt();
		}
		Currency currency = c == 1 ? Currency.EUR : Currency.USD;
		BankAccount newAccount = new BankAccount(currency);
		currentAccount = repository.save(newAccount);
		System.out.println("New account created: "  + currentAccount);
	}

	private boolean badCurrency(Scanner scanner) throws UnsupportedOperationException {
		System.out.println("Enter Currency");
		System.out.println("1. EUR");
		System.out.println("2. USD");
		int choice = scanner.nextInt();
		Currency c = choice == 1 ? Currency.EUR : choice == 2 ? Currency.USD : null;
		return c == null || c != currentAccount.getCurrency();
	}

	private long getAmount(Scanner scanner) throws UnsupportedOperationException {
		System.out.println("Enter the amount:");
		long amount = scanner.nextLong();
		if (amount < 0  ) {
			logger.warn("Amount to must be positive number");
			return 0;
		}
		return amount;
	}

}
