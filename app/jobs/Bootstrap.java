package jobs;

import java.util.Calendar;

import controllers.InfoTexts;
import models.City;
import models.Country;
import models.Deal;
import models.InfoText;
import models.Setting;
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
        if(InfoText.all().count() == 0){
        	this.initializeInfoTexts();
        	Logger.info("The are no infotexts. Some examples have been created.");
        }
        if(Setting.all().count() == 0){
        	this.initializeSettings();
        	Logger.info("The are no settings. Some examples have been created.");
        }
    }

	private void initializeSettings() {
	}

	private void initializeInfoTexts() {
		InfoText mcSubject = new InfoText();
		mcSubject.key = InfoTexts.MAILCHIMP_SUBJECT;
		mcSubject.content = "Hotel en ## esta noche";
		mcSubject.locale = "es";
		mcSubject.insert();
		InfoText mcSubjectEN = new InfoText();
		mcSubjectEN.key = InfoTexts.MAILCHIMP_SUBJECT;
		mcSubjectEN.content = "Hotel in ## tonight";
		mcSubjectEN.locale = "en";
		mcSubjectEN.insert();
		InfoText mcSubjectFR = new InfoText();
		mcSubjectFR.key = InfoTexts.MAILCHIMP_SUBJECT;
		mcSubjectFR.content = "Hotel á ## ce soir";
		mcSubjectFR.locale = "fr";
		mcSubjectFR.insert();
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
		spain.active = true;
		spain.hotusaCode = "MV";
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
		madrid.mailchimpCode ="MAD";
		madrid.hotusaCode = "MOLDAVIA";
		madrid.hotusaProvCode = "MVMOL";
		madrid.nameEN ="Madrid";
		madrid.nameFR ="Madrid";
		madrid.latitude = "40.4711601";
		madrid.longitude = "-3.871398";
		madrid.lat = 40.4711601;
		madrid.lng = -3.871398;
		madrid.updated = Calendar.getInstance().getTime();
		madrid.insert();
		City madridNorth = new City("Madrid Norte", "madrid_north");
		madridNorth.active = Boolean.TRUE;
		madridNorth.country = spain;
		madridNorth.root = "madrid";
		madridNorth.insert();
		City madridSouth = new City("Madrid Sur", "madrid_south");
		madridSouth.active = Boolean.TRUE;
		madridSouth.country = spain;
		madridSouth.root = "madrid";
		madridSouth.insert();
		City barcelona = new City("Barcelona", "barcelona");
		barcelona.active = Boolean.TRUE;
		barcelona.country = spain;
		barcelona.insert();
		City moldavia = new City("Moldavia", "moldavia");
		moldavia.active = Boolean.TRUE;
		moldavia.insert();
		Deal prueba = new Deal("Hotel prueba", madridNorth, Boolean.TRUE);
		prueba.address = "moldavia 43";
		prueba.mainImageBig = "mainImage.jpg";
		prueba.mainImageSmall = "mainImageSmall.jpg";
		prueba.hotelCode = "752552";
		prueba.isHotUsa = Boolean.TRUE;
		prueba.contactEmail = "pablo@iipir.com";
		prueba.priceCents = 100;
		prueba.owner = User.findByEmail("pablo@iipir.com");
		prueba.quantity = 10;
		prueba.salePriceCents = 55;
		prueba.priceCents = 128;
		prueba.priceDay2 = 80;
		prueba.position = 3;
		prueba.limitHour = 3;
		prueba.insert();
		Deal ritz = new Deal("Hotel DORMIRDCINE", madridNorth, Boolean.TRUE);
		ritz.address = "castellana 43";
		ritz.mainImageBig ="madrid/embassy/detalle.jpg";
		ritz.mainImageSmall = "madrid/embassy/listado.jpg";
		ritz.listImage = "madrid/embassy/completo10.jpg";
		ritz.image1 = "madrid/embassy/completo1.jpg";
		ritz.image2 = "madrid/embassy/completo2.jpg";
		ritz.image3 = "madrid/embassy/completo3.jpg";
		ritz.image4 = "madrid/embassy/completo4.jpg";
		ritz.image5 = "madrid/embassy/completo5.jpg";
		ritz.image10 = "madrid/embassy/completo10.jpg";
		ritz.hotelCode = "691990";
		ritz.isHotUsa = Boolean.TRUE;
		ritz.contactEmail = "pablo@iipir.com";
		ritz.priceCents = 100;
		ritz.owner = User.findByEmail("pablo@iipir.com");
		ritz.quantity = 10;
		ritz.salePriceCents = 105;
		ritz.priceCents = 188;
		ritz.priceDay2 = 80;
		ritz.priceDay3 = 110;
		ritz.priceDay4 = 110;
		ritz.position = 2;
		ritz.limitHour = 3;
		ritz.insert();
	    Deal cibeles = new Deal("Vincci Soho", madridSouth, Boolean.TRUE);
	    cibeles.address = "calle castellana, 43, 28224";
		cibeles.mainImageBig ="madrid/embassy/detalle.jpg";
		cibeles.mainImageSmall = "madrid/embassy/listado.jpg";
		cibeles.listImage = "madrid/embassy/completo10.jpg";
		cibeles.image1 = "madrid/embassy/completo1.jpg";
		cibeles.image2 = "madrid/embassy/completo2.jpg";
		cibeles.image3 = "madrid/embassy/completo3.jpg";
		cibeles.image4 = "madrid/embassy/completo4.jpg";
		cibeles.image5 = "madrid/embassy/completo5.jpg";
		cibeles.image10 = "madrid/embassy/completo10.jpg";
		cibeles.hotelCode = "601022";
		cibeles.isHotUsa = Boolean.TRUE;
		cibeles.contactEmail = "pablo@iipir.com";
		cibeles.salePriceCents = 145;
		cibeles.netSalePriceCents = (float) 125.0;
		cibeles.priceCents = 188;
		cibeles.priceDay2 = 80;
		cibeles.priceDay3 = 110;
		cibeles.position = 1;
		cibeles.limitHour = 3;
		cibeles.roomType = "lujo";
		cibeles.owner = User.findByEmail("pablo@iipir.com");
		cibeles.quantity = 10;
		cibeles.latitude = "42.8814063452954";
		cibeles.longitude = "-8.54341917990539";
		cibeles.detailText = "* Desayuno NO incluído.* El precio es final e INCLUYE impuestos." +
				"* El precio es por habitación DOBLE con una ocupación máxima de 2 personas." +
				"* Para conseguir los mejores precios no podemos asegurar que podrás elegir entre cama de matrimonio y dos camas individuales. " +
				"Depende de la disponibilidad  del hotel a tu llegada aunque SIEMPRE será una HABITACION DOBLE." +
				"* Puedes llegar al hotel hasta las 6am. * La salida será antes de las 12 del mediodía." +
				"	* Necesitarás tu carnet de identidad y una tarjeta de crédito al llegar al hotel.";
		cibeles.hotelText = "Enclavado en un emblemático edificio del siglo XIX, totalmente reformado." +
				"* En la recepción podrá alquilar gratis bicicletas y cochecitos para bebés. " +
				"* Se admiten animales domésticos sin cargo adicional a petición." +
				"* Se ofrece un servicio de traslado al aeropuerto gratuito (disponible bajo petición)." +
				"* Puntuación de 7.7 en Booking." +
				"* Barrio tranquilo y sin ruidos por la noche.";
		cibeles.roomText = "Las habitaciones del Petit Palace Embassy son elegantes y espaciosas. " +
				"Todas disponen de: * un ordenador con conexión a internet" +
				"* un baño con ducha de hidromasaje" +
				"* cama de matrimonio* minibar* caja fuerte gratuita* televisión de pantalla plana. ";
		cibeles.foodDrinkText = "Cuenta con restaurante situado en la planta noble del edificio. " +
				"Tiene una capacidad para 40 comensales y dispone de un espacio reservado dentro del propio local" +
				" ideal para almuerzos de negocios hasta 10 comensales.* Restaurante recomendado por Guía Roja Michelín" +
				"* Todas las mañanas se sirve un variado desayuno bufé." +
				"* El restaurante del hotel, abierto para el almuerzo y la cena, sirve cocina española creativa.";
	    cibeles.aroundText ="Está situado en la exclusiva zona comercial de la Milla De Oro de Madrid, y a tan solo un paseo del " +
	    		"Parque del Retiro y de los principales Museos de la capital -Prado, Reina Sofía y Thyssen Bornemiza." +
	    		"* Este exclusivo barrio del centro de Madrid es tranquilo y silencioso por las noches. Perfecto si no " +
	    		"quieres que nadie moleste tus horas de sueño.";
	    cibeles.insert();
	    Deal sol = new Deal("Hotel Conde Duque", madridSouth, Boolean.TRUE);
	    sol.address = "castellana 43";
		sol.mainImageBig ="madrid/embassy/detalle.jpg";
		sol.mainImageSmall = "madrid/embassy/listado.jpg";
		sol.listImage = "madrid/embassy/completo10.jpg";
		sol.image1 = "madrid/embassy/completo1.jpg";
		sol.image2 = "madrid/embassy/completo2.jpg";
		sol.image3 = "madrid/embassy/completo3.jpg";
		sol.image4 = "madrid/embassy/completo4.jpg";
		sol.image5 = "madrid/embassy/completo5.jpg";
		sol.image10 = "madrid/embassy/completo10.jpg";
		sol.hotelCode = "000850";
		sol.isHotUsa = Boolean.TRUE;
		sol.contactEmail = "pablo@iipir.com";
		sol.priceCents = 100;
		sol.owner = User.findByEmail("pablo@iipir.com");
		sol.quantity = 10;
	    sol.insert();
	    Deal catalunya = new Deal("Hotel Barcelo Norte", madridNorth, Boolean.TRUE);
	    catalunya.address = "calle castellana 43, al norte";
		catalunya.mainImageBig ="madrid/embassy/detalle.jpg";
		catalunya.mainImageSmall = "madrid/embassy/listado.jpg";
		catalunya.listImage = "madrid/embassy/completo10.jpg";
		catalunya.image1 = "madrid/embassy/completo1.jpg";
		catalunya.image2 = "madrid/embassy/completo2.jpg";
		catalunya.image3 = "madrid/embassy/completo3.jpg";
		catalunya.image4 = "madrid/embassy/completo4.jpg";
		catalunya.image5 = "madrid/embassy/completo5.jpg";
		catalunya.image10 = "madrid/embassy/completo10.jpg";
		catalunya.isHotUsa = Boolean.FALSE;
		catalunya.contactEmail = "pablo@iipir.com";
		catalunya.priceCents = 200;
		catalunya.salePriceCents = 60;
		catalunya.owner = User.findByEmail("pablo@iipir.com");
		catalunya.quantity = 10;
	    catalunya.insert();		
	}
    
}