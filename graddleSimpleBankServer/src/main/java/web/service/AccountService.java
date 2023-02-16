package web.service;

import data.dao.PersonDAO;
import data.entities.Person;

public class AccountService {

    private static AccountService accountManager;

    private AccountService() {
    }

    public static AccountService instance(){
        if (accountManager == null) {
            accountManager = new AccountService();
        }

        return accountManager;
    }

/*    public boolean checkUserData(Person person){
        Person personData = new PersonDAO().read(person.get);

        if (personData != null) {
            return personData.getPassword().equals(person.getPassword());
        }

        return false;
    }

    public boolean isRegistred(User userProfile){
        return new UserDao().read(userProfile.getLogin()) != null;
    }

    public boolean addNewUser(User userProfile){
        new UserDao().save(userProfile);
        return true;
    }

    public boolean deleteUser(User userProfile){
        return false;
    }*/

}
