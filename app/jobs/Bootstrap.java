package jobs;

import models.City;
import models.Country;
import models.Deal;
import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/*This is not working on gae*/
@OnApplicationStart
public class Bootstrap extends Job {
    
    public void doJob() {
    	if(User.all().count() == 0) {
            this.initializeUsers();
            Logger.info("The are no user. A root user has been created.");
        }
        if(Deal.all().count() == 0) {
        	this.initializeDeals();
        	Logger.info("The are no deals. Some examples have been created.");
        }
    }

	private void initializeUsers() {
		 User pablo = new User("pablo@iipir.com", "iipir11", "Pablo", "Pazos",true, true);
		 pablo.insert();
		 pablo.validated = true;
		 pablo.update();
		 User roberto = new User("roberto@iipir.com", "iipir11", "Roberto", "Gil", false, false);
		 roberto.insert();
		 roberto.validated = true;
		 roberto.update();
	}
	

	private void initializeDeals() {
		Country spain = new Country("España", "spain");
		spain.insert();
		City coruña = new City("coruña", "coruña");
		coruña.active = Boolean.TRUE;
		coruña.country = spain;
		coruña.insert();
		City berlin = new City("berlin", "berlin");
		berlin.active = Boolean.TRUE;
		berlin.country = spain;
		berlin.insert();
		City bilbao = new City("bilbao", "bilbao");
		bilbao.active = Boolean.TRUE;
		bilbao.country = spain;
		bilbao.insert();
		City londres = new City("londres", "londres");
		londres.active = Boolean.TRUE;
		londres.country = spain;
		londres.insert();
		City milan = new City("milan", "milan");
		milan.active = Boolean.TRUE;
		milan.country = spain;
		milan.insert();
		City malaga = new City("malaga", "malaga");
		malaga.active = Boolean.TRUE;
		malaga.country = spain;
		malaga.insert();
		City paris = new City("paris", "paris");
		paris.active = Boolean.TRUE;
		paris.country = spain;
		paris.insert();
		City santiago = new City("santiago", "santiago");
		santiago.active = Boolean.TRUE;
		santiago.country = spain;
		santiago.insert();
		City sevilla = new City("sevilla", "sevilla");
		sevilla.active = Boolean.TRUE;
		sevilla.country = spain;
		sevilla.insert();
		City valencia = new City("valencia", "valencia");
		valencia.active = Boolean.TRUE;
		valencia.country = spain;
		valencia.insert();
		City valladolid = new City("valladolid", "valladolid");
		valladolid.active = Boolean.TRUE;
		valladolid.country = spain;
		valladolid.insert();
		City madrid = new City("Madrid", "madrid");
		madrid.active = Boolean.TRUE;
		madrid.country = spain;
		madrid.insert();
		City barcelona = new City("Barcelona", "barcelona");
		barcelona.active = Boolean.TRUE;
		barcelona.country = spain;
		barcelona.insert();
		City moldavia = new City("Moldavia", "moldavia");
		moldavia.active = Boolean.TRUE;
		moldavia.insert();
		Deal prueba = new Deal("Hotel prueba", moldavia, Boolean.TRUE);
		prueba.address = "moldavia 43";
		prueba.mainImageBig = "mainImage.jpg";
		prueba.mainImageSmall = "mainImageSmall.jpg";
		prueba.hotelCode = "601022";
		prueba.isHotUsa = Boolean.TRUE;
		prueba.contactEmail = "pablo@iipir.com";
		prueba.priceCents = 100;
		prueba.owner = User.findByEmail("pablo@iipir.com");
		prueba.quantity = 10;
		prueba.insert();
		Deal ritz = new Deal("Hotel DORMIRDCINE", madrid, Boolean.TRUE);
		ritz.address = "castellana 43";
		ritz.mainImageBig = "mainImage.jpg";
		ritz.mainImageSmall = "mainImageSmall.jpg";
		ritz.image1 = "image1.jpg";
		ritz.image2 = "image2.jpg";
		ritz.image3 = "image3.jpg";
		ritz.image4 = "image4.jpg";
		ritz.hotelCode = "691990";
		ritz.isHotUsa = Boolean.TRUE;
		ritz.contactEmail = "pablo@iipir.com";
		ritz.priceCents = 100;
		ritz.owner = User.findByEmail("pablo@iipir.com");
		ritz.quantity = 10;
		ritz.insert();
	    Deal cibeles = new Deal("Vincci Soho", madrid, Boolean.TRUE);
	    cibeles.address = "castellana 43";
		cibeles.mainImageBig = "mainImage.jpg";
		cibeles.mainImageSmall = "mainImageSmall.jpg";
		cibeles.image1 = "image1.jpg";
		cibeles.image2 = "image2.jpg";
		cibeles.image3 = "image3.jpg";
		cibeles.image4 = "image4.jpg";
		cibeles.hotelCode = "614546";
		cibeles.isHotUsa = Boolean.TRUE;
		cibeles.contactEmail = "pablo@iipir.com";
		cibeles.priceCents = 100;
		cibeles.owner = User.findByEmail("pablo@iipir.com");
		cibeles.quantity = 10;
	    cibeles.insert();
	    Deal sol = new Deal("Hotel Conde Duque", madrid, Boolean.TRUE);
	    sol.address = "castellana 43";
	    sol.mainImageBig = "mainImage.jpg";
		sol.mainImageSmall = "mainImageSmall.jpg";
		sol.image1 = "image1.jpg";
		sol.image2 = "image2.jpg";
		sol.image3 = "image3.jpg";
		sol.image4 = "image4.jpg";
		sol.hotelCode = "000850";
		sol.isHotUsa = Boolean.TRUE;
		sol.contactEmail = "pablo@iipir.com";
		sol.priceCents = 100;
		sol.owner = User.findByEmail("pablo@iipir.com");
		sol.quantity = 10;
	    sol.insert();
	    Deal catalunya = new Deal("Hotel Catalunya", barcelona, Boolean.TRUE);
	    catalunya.address = "castellana 43";
		catalunya.mainImageBig = "mainImage.jpg";
		catalunya.mainImageSmall = "mainImageSmall.jpg";
		catalunya.image1 = "image1.jpg";
		catalunya.image2 = "image2.jpg";
		catalunya.image3 = "image3.jpg";
		catalunya.image4 = "image4.jpg";
		catalunya.isHotUsa = Boolean.FALSE;
		catalunya.contactEmail = "pablo@iipir.com";
		catalunya.priceCents = 200;
		catalunya.salePriceCents = 60;
		catalunya.owner = User.findByEmail("pablo@iipir.com");
		catalunya.quantity = 10;
	    catalunya.insert();		
	}
    
}