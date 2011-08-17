package jobs;

import models.City;
import models.Deal;
import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/*This is not working on gae*/
@OnApplicationStart
public class Bootstrap extends Job {
    
    public void doJob() {
        if(Deal.all().count() == 0) {
        	this.initializeDeals();
        	Logger.info("The are no deals. Some examples have been created.");
        }
        if(User.all().count() == 0) {
            this.initializeUsers();
            Logger.info("The are no user. A root user has been created.");
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
		City madrid = new City("Madrid", "madrid");
		madrid.active = Boolean.TRUE;
		madrid.insert();
		City barcelona = new City("Barcelona", "barcelona");
		barcelona.active = Boolean.TRUE;
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
		prueba.insert();
		Deal ritz = new Deal("Hotel Ritz", madrid, Boolean.TRUE);
		ritz.address = "castellana 43";
		ritz.mainImageBig = "mainImage.jpg";
		ritz.mainImageSmall = "mainImageSmall.jpg";
		ritz.image1 = "image1.jpg";
		ritz.image2 = "image2.jpg";
		ritz.image3 = "image3.jpg";
		ritz.image4 = "image4.jpg";
		ritz.insert();
	    Deal cibeles = new Deal("Hotel Cibeles", madrid, Boolean.TRUE);
	    cibeles.address = "castellana 43";
		cibeles.mainImageBig = "mainImage.jpg";
		cibeles.mainImageSmall = "mainImageSmall.jpg";
		cibeles.image1 = "image1.jpg";
		cibeles.image2 = "image2.jpg";
		cibeles.image3 = "image3.jpg";
		cibeles.image4 = "image4.jpg";
	    cibeles.insert();
	    Deal sol = new Deal("Hotel Sol", madrid, Boolean.TRUE);
	    sol.address = "castellana 43";
	    sol.mainImageBig = "mainImage.jpg";
		sol.mainImageSmall = "mainImageSmall.jpg";
		sol.image1 = "image1.jpg";
		sol.image2 = "image2.jpg";
		sol.image3 = "image3.jpg";
		sol.image4 = "image4.jpg";
	    sol.insert();
	    Deal catalunya = new Deal("Hotel Catalunya", barcelona, Boolean.TRUE);
	    catalunya.address = "castellana 43";
		catalunya.mainImageBig = "mainImage.jpg";
		catalunya.mainImageSmall = "mainImageSmall.jpg";
		catalunya.image1 = "image1.jpg";
		catalunya.image2 = "image2.jpg";
		catalunya.image3 = "image3.jpg";
		catalunya.image4 = "image4.jpg";
	    catalunya.insert();		
	}
    
}