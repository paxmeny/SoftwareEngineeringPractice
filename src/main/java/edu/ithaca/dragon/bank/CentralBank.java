package edu.ithaca.dragon.bank;

import java.security.PublicKey;
import java.util.Collection;

public class CentralBank implements BasicAPI, AdvancedAPI, AdminAPI {

    private BankAccount[] accounts;
    private int time;
    private String adminPassoword;
    private String[] allHistory;
    private String[] atmHistory;
    private String[] tellerHistory;
    private String[] adminHistory;
    private String[] emails;
    private String[] passwords;
    private int numEmails;
    private int numAccounts;

    public CentralBank() {

        int defaultArraySize = 10; //this may change

        accounts = new BankAccount[defaultArraySize];
        emails = new String[defaultArraySize];
        passwords = new String[defaultArraySize];
        adminPassoword = "NotRobert";
        allHistory = new String[defaultArraySize];
        atmHistory = new String[defaultArraySize];
        tellerHistory = new String[defaultArraySize];
        adminHistory = new String[defaultArraySize];
        numAccounts = 0;
        numEmails = 0;
        time = 0;
    }

    //----------------- edu.ithaca.dragon.bank.BasicAPI methods -------------------------//

    public boolean confirmCredentials(String email, String password) {
        return false;
    }

    /**
     * Checks an accounts balance
     * @param acctId Account Identifier
     * @return current account balance
     */
    public double checkBalance(String acctId) {
        return findAcct(acctId).getBalance();
    }

    public double checkBalance(String email, String type) {
        return checkBalance(getAccountId(email, type));
    }

    public void withdraw(String acctId, double amount) throws InsufficientFundsException {

    }

    public void withdraw(String email, String type, double amount) throws InsufficientFundsException {

    }

    /**
     * Deposits money to an account
     * @param acctId Account Identifier
     * @param amount Amount to be deposited
     */
    public void deposit(String acctId, double amount) {
        findAcct(acctId).deposit(amount);
    }
    public void deposit(String email, String type, double amount) {
        deposit(getAccountId(email,type),amount);
    }

    public void transfer(String acctIdToWithdrawFrom, String acctIdToDepositTo, double amount) throws InsufficientFundsException {

    }

    public void transfer(String emailToWithdrawFrom, String type1, String emailToDepositTo, String type2, double amount) throws InsufficientFundsException{
        transfer(getAccountId(emailToWithdrawFrom,type1),getAccountId(emailToDepositTo,type2),amount);
    }

    public String transactionHistory(String acctId) {
        return null;
    }

    public String transactionHistory(String email, String type){
        return transactionHistory(getAccountId(email,type));
    }

    public String getAccountId(String email, String accountType){
        for (int i = 0; i < numAccounts; i++){
            if(accounts[i].getEmail().equals(email) && accounts[i].getType().equals(accountType)){
                return accounts[i].getAcctId();
            }
        }
        throw new IllegalArgumentException("Account not found");
    }

    public BankAccount findAcct(String acctId){
        for (int i = 0; i < numAccounts; i++){
            if(accounts[i].getAcctId().equals(acctId)){
                return accounts[i];
            }
        }
        throw new IllegalArgumentException("Account not found");
    }


    //----------------- edu.ithaca.dragon.bank.AdvancedAPI methods -------------------------//

    /**
     * Creates an acct of type defined, adds acct to acct list, updates num accts
     * @param email email associated with acct
     * @param startingBalance starting balance
     * @param acctType type of account
     */
    public void createAccount(String email, double startingBalance, String acctType) {
        String acctId = (this.numAccounts + 1) + acctType.substring(0,1);

        if (acctType.equals("Checking")) {
            CheckingAccount account = new CheckingAccount(email, startingBalance, acctId);
            accounts[numAccounts] = account;
            numAccounts++;
            addNewEmail(email);
        }
        else if(acctType.equals("Savings")){
            SavingsAccount account = new SavingsAccount(email, startingBalance, acctId);
            accounts[numAccounts] = account;
            numAccounts++;
            addNewEmail(email);
        }
        else{
            throw new IllegalArgumentException("AcctType must be either Savings or Checking");
        }

    }

    public void addNewEmail(String email){
        boolean newEmail = true;
        for (int i = 0; i < numEmails; i++){
            if(emails[i].equals(email)){
                newEmail = false;
                break;
            }
        }
        if (newEmail){
            emails[numEmails] = email;
            numEmails++;
        }
    }

    public void closeAccount(String acctId) {

    }


    //------------------ edu.ithaca.dragon.bank.AdminAPI methods -------------------------//

    /**
     * Calculates the total assets stored in the bank and updates attribute netWorth
     * @return calculated value
     */
    public double calcTotalAssets() {
        double sum = 0;
        for(int i=0; i<numAccounts;i++){
            sum += accounts[i].getBalance();
        }
        return sum;
    }

    public Collection<String> findAcctIdsWithSuspiciousActivity() {
        return null;
    }

    public void freezeAccount(String acctId) {

    }

    public void unfreezeAcct(String acctId) {

    }

    //------------------ CentralBank Getters -------------------------//


    public BankAccount[] getAccounts() {
        return accounts;
    }

    public int getTime() {
        return time;
    }

    public String[] getAllHistory() {
        return allHistory;
    }

    public String[] getAtmHistory() {
        return atmHistory;
    }

    public String[] getTellerHistory() {
        return tellerHistory;
    }

    public String[] getAdminHistory() {
        return adminHistory;
    }

    public int getNumAccounts() {
        return numAccounts;
    }
}
