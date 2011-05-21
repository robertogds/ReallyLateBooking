package controllers;

import java.util.*;
import models.*;
import models.crudsiena.*;
import play.*;
import play.mvc.*;
import siena.*;

@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	
	public static void iPhoneList(String city) {
	        Collection<Deal> deals = fetchDeals(city);
	        
			for (Deal deal: deals){
				Logger.info("deal" + deal.hotelName , deal);
				prepareImagesRoutes(deal);
			}
	        renderJSON(deals);
	}
	
	private static Collection<Deal> fetchDeals(String cityName) {
			City city = City.findByName(cityName);
	        Collection<Deal> deals = Deal.findActiveDealsByCity(city);
	        return deals;
	}

	
	private static void prepareImagesRoutes(Deal deal){
		//Size of the images for the required system, iphone, ipad , etc.
		String listSize = Play.configuration.getProperty("img.list_size");
		String detailSize = Play.configuration.getProperty("img.detail_size");
		String completeSize = Play.configuration.getProperty("img.complete_size");
		
		//Url of the image directory. Now is Amazon s3 http://d2f5bmx5jz1oq8.cloudfront.net/
		String imgDirUrl = Play.configuration.getProperty("img.dir.url");
		
		//Concatenate the url to the image with the appropiate size
		deal.mainImageBig = deal.mainImageBig != null ? imgDirUrl + concatImageSize(deal.mainImageBig, detailSize) : null;
		deal.mainImageSmall = deal.mainImageSmall != null ? imgDirUrl + concatImageSize(deal.mainImageSmall, listSize) : null;
		deal.image1 = deal.image1 != null ? imgDirUrl + concatImageSize(deal.image1, completeSize) : null;
		deal.image2 = deal.image2 != null ? imgDirUrl + concatImageSize(deal.image2, completeSize) : null;
		deal.image3 = deal.image3 != null ? imgDirUrl + concatImageSize(deal.image3, completeSize) : null;
		deal.image4 = deal.image4 != null ? imgDirUrl + concatImageSize(deal.image4, completeSize) : null;
		deal.image5 = deal.image5 != null ? imgDirUrl + concatImageSize(deal.image5, completeSize) : null;
	}
    
	/*
	 * Modify the image name to fit the required size
	 * */
	private static String concatImageSize(String image, String size){
			if (!image.isEmpty()){
				String[] temp;
			  	String delimiter = "\\.";
			  	temp = image.split(delimiter);
			  	System.out.println("Imagen:" + image);
			  	try {
					image = temp[0] + size + "." + temp[1];
				} catch (Exception e) {
					Logger.error("Incorrect image format, be sure it has the image format extension", e);
				}
			}
			
			return image;
	}
	
}

