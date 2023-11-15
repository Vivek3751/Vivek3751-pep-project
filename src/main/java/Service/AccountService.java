package Service;
import Model.Account;
import DAO.AccountDAO;

public class AccountService {
    private AccountDAO accountDAO;
/*
     * no-args constructor for creating a 
     * new AccountService with a new AccountDAO. */
    public AccountService(){
        accountDAO = new AccountDAO();
    }

    public Account newUserRegister(Account account){
        return accountDAO.newUserRegister(account);
    }

    public String getUserNameString(Account account) {
        return accountDAO.getUserNameString(account);
    }

    public Account getAccountByUsername(String username) {
        return accountDAO.getAccountByUsername(username);
    }

    public Account getAccountById(int posted_by) {
        return accountDAO.getAccountById(posted_by);
    }

}
