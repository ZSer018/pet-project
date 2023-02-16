package data.dao;

import data.dao.LoanDAO;
import data.entities.Loan;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class LoanDAOTest {

    @Test
    public void testCreate() {
        LoanDAO loanDAO = Mockito.mock(LoanDAO.class);

        Loan loan = new Loan();
        loan.setId(0);
        loan.setAmount(1000);
        Mockito.when(loanDAO.read("fake")).thenReturn(loan);

        loan = null;

        loan = loanDAO.read("fake");
        Assert.assertEquals(loan.getId(), 0);
        Assert.assertEquals(loan.getAmount(), 10000);
    }


}