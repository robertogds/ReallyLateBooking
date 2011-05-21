import org.junit.*;

import controllers.Deals;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset("utf-8", response);
    }
    
    @Test
	public void getDealListForIphone(){
    	City madrid = new City("Madrid");
    	madrid.insert();
		Deal ritz = new Deal("Hotel Ritz", madrid, Boolean.TRUE);
		ritz.address = "castellana 43";
		ritz.mainImageBig = "mainImage.jpg";
		ritz.mainImageSmall = "mainImageSmall.jpg";
		ritz.image1 = "image1.jpg";
		ritz.image2 = "image2.jpg";
		ritz.image3 = "image3.jpg";
		ritz.image4 = "image4.jpg";
		ritz.insert();
		
		Response response = GET("/deals/madrid");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
	}
    
}