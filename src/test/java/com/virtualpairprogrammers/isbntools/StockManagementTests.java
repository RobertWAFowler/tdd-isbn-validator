package com.virtualpairprogrammers.isbntools;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class StockManagementTests {

    private final String BOOK_ISBN_1 = "0140177396";
    ExternalISBNDataService webService;
    ExternalISBNDataService databaseService;
    StockManager stockManager;

    @Before
    public void setup() {

        System.out.println("In setup!!!");
        webService = mock(ExternalISBNDataService.class);
        databaseService = mock(ExternalISBNDataService.class);
        stockManager = new StockManager();
        stockManager.setWebService(webService);
        stockManager.setDatabaseService(databaseService);
    }

    @Test
    public void testCanGetACorrectLocatorCode() {

        //Given
        when(webService.lookup(BOOK_ISBN_1))
                .thenReturn(new Book(BOOK_ISBN_1, "Of Mice And Men", "J. Steinbeck"));
        when(databaseService.lookup(BOOK_ISBN_1)).thenReturn((null));

        //When
        String locatorCode = stockManager.getLocatorCode(BOOK_ISBN_1);

        //Then
        assertEquals("7396J4", locatorCode);
    }

    @Test
    public void databaseIsUsedIfDataIsPresent() {

        when(databaseService.lookup(BOOK_ISBN_1))
                .thenReturn(new Book(BOOK_ISBN_1, "abc", "abc"));

        String locatorCode = stockManager.getLocatorCode(BOOK_ISBN_1);

        verify(databaseService).lookup(BOOK_ISBN_1);
        verify(webService, never()).lookup(anyString());

    }

    @Test
    public void webserviceIsUsedIfDataIsNotPresentInDatabase() {

        when(databaseService.lookup(BOOK_ISBN_1)).thenReturn(null);
        when(webService.lookup(BOOK_ISBN_1))
                .thenReturn(new Book(BOOK_ISBN_1, "abc", "abc"));

        String locatorCode = stockManager.getLocatorCode(BOOK_ISBN_1);

        verify(databaseService).lookup(BOOK_ISBN_1);
        verify(webService).lookup(BOOK_ISBN_1);
    }

}
