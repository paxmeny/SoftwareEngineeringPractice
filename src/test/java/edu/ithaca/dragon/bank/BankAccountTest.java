package edu.ithaca.dragon.bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void constructorTestUpdated() {
        BankAccount bankAccount = new BankAccount("a@b.com", 200);

        assertEquals("a@b.com", bankAccount.getEmail());
        assertEquals(200, bankAccount.getBalance());
        //check for exception thrown correctly
        assertThrows(IllegalArgumentException.class, ()-> new BankAccount("", 100));
        assertThrows(IllegalArgumentException.class, ()-> new BankAccount("a@b.com", -0.01)); // invalid border case (amount)
        assertThrows(IllegalArgumentException.class, ()-> new BankAccount("a@b.com", 0.001)); // invalid border case (decimal place limit)
    }

    @Test
    void isEmailValidTestUpdated(){
        assertFalse(BankAccount.isEmailValid(""));
        // Empty String
        assertFalse(BankAccount.isEmailValid("a-@b.com"));
        // Ends in special char
        assertFalse(BankAccount.isEmailValid("a..@b.com"));
        // Ends in two dots
        assertFalse(BankAccount.isEmailValid(".a@b.com"));
        // Starts with dot
        assertFalse(BankAccount.isEmailValid("a#@b.com"));
        // Ends in special char
        assertFalse(BankAccount.isEmailValid("a@b.c"));
        // Too short a domain
        assertFalse(BankAccount.isEmailValid("a@b#c.com"));
        // Special char in domain
        assertFalse(BankAccount.isEmailValid("a@b"));
        // Too short a domain
        assertFalse(BankAccount.isEmailValid("a@b..com"));
        // Two dots in domain
        assertTrue(BankAccount.isEmailValid("a@b.com"));
        assertTrue(BankAccount.isEmailValid("a-b@c.com"));
        // Border case two periods not consecutive (valid)
        assertTrue(BankAccount.isEmailValid("a.b@c.com"));
        assertTrue(BankAccount.isEmailValid("a_b@c.com"));
        // Border case special character "-" before @ (valid)
        assertTrue(BankAccount.isEmailValid("a@b-c.com"));
        // Border case special character "-" after @ (valid)
        assertTrue(BankAccount.isEmailValid("a-b@c.com"));
        // Border case length of email domain (valid)
        assertTrue(BankAccount.isEmailValid("a@b.cc"));
    }

    @Test
    void withdrawTestUpdated() throws InsufficientFundsException{
        BankAccount bankAccount = new BankAccount("a@b.com", 200);
        assertThrows(IllegalArgumentException.class, ()-> bankAccount.withdraw(-100));
        assertThrows(InsufficientFundsException.class, ()-> bankAccount.withdraw(300));
        bankAccount.withdraw(100);
        assertEquals(100, bankAccount.getBalance());

        BankAccount bankAccount2 = new BankAccount("a@b.com", 200);
        // Border case (3+ decimal places not allowed)
        assertThrows(IllegalArgumentException.class, ()-> bankAccount2.withdraw(199.999));
        // Valid border case (<=2 decimal places allowed)
        bankAccount2.withdraw(199.99);
        assertEquals(0.01, bankAccount2.getBalance());
        // Invalid border case (greater amount with decimals)
        assertThrows(IllegalArgumentException.class, ()-> bankAccount2.withdraw(200.001));
    }

    @Test
    void isAmountValidTest(){
        // equivalence class amount >= 0
        assertTrue(BankAccount.isAmountValid(100)); // valid middle case (amount value)
        assertTrue(BankAccount.isAmountValid(0.01)); // valid border case (decimal place limit)
        assertFalse(BankAccount.isAmountValid(0.009)); // invalid border case (decimal place limit)
        assertFalse(BankAccount.isAmountValid(0.0000093739)); // invalid middle case (decimal place limit)
        assertFalse(BankAccount.isAmountValid(0)); // invalid border case (amount value)
        // equivalence class amount < 0
        assertFalse(BankAccount.isAmountValid(-100)); // invalid middle case (amount value)
        assertFalse(BankAccount.isAmountValid(-0.01)); // invalid border case (amount value invalid, decimal place limit technically valid)
        assertFalse(BankAccount.isAmountValid(-0.009)); // invalid border case (amount value invalid, decimal place limit invalid)
        assertFalse(BankAccount.isAmountValid(-0.0000093739)); // invalid middle case (decimal place limit)
        // equivalence class integers converted to doubles
        assertTrue(BankAccount.isAmountValid(42.0)); // valid middle case (decimal place limit)
        assertTrue(BankAccount.isAmountValid(42.00)); // valid border case (decimal place limit)
        // these cases are based on Java's double mechanism (X.0000 becomes X.0 for an integer X in Java)
        assertTrue(BankAccount.isAmountValid(42.000)); // valid border case (decimal place limit is invalid, amount is chopped off to 42.0 by Java, so it becomes valid)
        assertTrue(BankAccount.isAmountValid(42.000000)); // valid middle case (decimal place limit, amount is chopped off to 42.0 by Java, so it becomes valid)
    }

    @Test
    void depositTest() throws InsufficientFundsException{
        BankAccount account = new BankAccount("a@b.com", 200);
        assertThrows(IllegalArgumentException.class, ()-> account.deposit(-100)); // invalid middle case (value)
        assertThrows(IllegalArgumentException.class, ()-> account.deposit(-1)); // invalid border case (value)
        assertThrows(IllegalArgumentException.class, ()-> account.deposit(100.001)); // invalid border case (decimal place limit)
        assertThrows(IllegalArgumentException.class, ()-> account.deposit(100.00001)); // invalid middle case (decimal place limit)
        assertThrows(IllegalArgumentException.class, ()-> account.deposit(-100.001)); // invalid case (middle value, border decimal place limit)
        assertThrows(IllegalArgumentException.class, ()-> account.deposit(-100.00001)); // invalid middle case (value, decimal place limit)

        account.deposit(0.01); // valid border case (value, decimal place limit)
        assertEquals(200.01, account.getBalance());
        account.withdraw(0.01);

        account.deposit(0.25); // valid case (border value, middle decimal place limit)
        assertEquals(200.25, account.getBalance());
        account.withdraw(0.25);

        account.deposit(100); // valid middle case (value)
        assertEquals(300, account.getBalance());
        account.withdraw(100);

        account.deposit(100.25); // valid middle case (value, decimal place limit)
        assertEquals(300.25, account.getBalance());
    }

    @Test
    void transferTest() throws InsufficientFundsException{
        BankAccount account1 = new BankAccount("a@b.com", 200);
        BankAccount account2 = new BankAccount("c@d.com", 200);
        assertThrows(IllegalArgumentException.class, ()-> account1.transfer(account2, -100)); // invalid middle case (value)
        assertThrows(IllegalArgumentException.class, ()-> account1.transfer(account2, -0.01)); // invalid border case (value)
        assertThrows(IllegalArgumentException.class, ()-> account1.transfer(account2, -0.009)); // invalid border case (decimal place limit)
        assertThrows(IllegalArgumentException.class, ()-> account1.transfer(account2, 0.001)); // invalid border case (decimal place limit)
        assertThrows(IllegalArgumentException.class, ()-> account1.transfer(account1, 100)); // invalid case (same account)

        account1.transfer(account2, 100); // valid middle case (value)
        assertEquals(300, account2.getBalance());
        assertEquals(100, account1.getBalance());

        account2.transfer(account1, 100.25); // valid middle case (decimal limit)
        assertEquals(199.75, account2.getBalance());
        assertEquals(200.25, account1.getBalance());
    }

}