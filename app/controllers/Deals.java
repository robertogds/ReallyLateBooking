package controllers;

import java.util.*;
import models.*;
import models.crudsiena.*;
import play.*;
import play.mvc.*;
import siena.*;

@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	
	public static void iPhoneList() {
	        Collection<Deal> deals = fetchDeals();
			for (Deal deal: deals){
				prepareImagesRoutes(deal);
			}
	        renderJSON(deals);
	}
	
	private static Collection<Deal> fetchDeals() {
	        Collection<Deal> deals = Deal.all()
				.filter("active", true)
				.fetch(3);
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
		deal.mainImageBig = imgDirUrl + concatImageSize(deal.mainImageBig, detailSize);
		deal.mainImageSmall = imgDirUrl + concatImageSize(deal.mainImageSmall, listSize);
		deal.image1 = imgDirUrl + concatImageSize(deal.image1, completeSize);
		deal.image2 = imgDirUrl + concatImageSize(deal.image2, completeSize);
		deal.image3 = imgDirUrl + concatImageSize(deal.image3, completeSize);
		deal.image4 = imgDirUrl + concatImageSize(deal.image4, completeSize);
		deal.image5 = imgDirUrl + concatImageSize(deal.image5, completeSize);
	}
    
	/*
	 * Modify the image name to fit the required size
	 * */
	private static String concatImageSize(String image, String size){
			String[] temp;
		  	String delimiter = "\\.";
		  	temp = image.split(delimiter);
		  	try {
				image = temp[0] + size + "." + temp[1];
			} catch (ArrayIndexOutOfBoundsException e) {
				Logger.error(e, "Incorrect image format, be sure it has the image format extension");
			}
			return image;
	}
	
}

