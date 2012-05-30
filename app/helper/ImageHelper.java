package helper;

import models.Deal;
import models.dto.DealDTO;
import play.Logger;
import play.Play;

/** 
 * Prepare images to fit to the required size
 *
 * @author pablopr
 *
 */
public final class ImageHelper {

	public static void prepareImagesRoutes(DealDTO deal, boolean autoImageUrl){
		//Size of the images for the required system, iphone, ipad , etc.
		String listSize = Play.configuration.getProperty("img.list_size");
		String listSizeV2 = Play.configuration.getProperty("img.list_size.v2");
		String detailSize = Play.configuration.getProperty("img.detail_size");
		String completeSize = Play.configuration.getProperty("img.complete_size");
		
		//Url of the image directory. Now is Amazon s3 http://d2f5bmx5jz1oq8.cloudfront.net/
		String imgDirUrl = Play.configuration.getProperty("img.dir.url");
		
		//Concatenate the url to the image with the appropiate size
		deal.mainImageBig = deal.mainImageBig != null ? imgDirUrl + concatImageSize(deal.mainImageBig, detailSize, !autoImageUrl) : null;
		deal.mainImageSmall = deal.mainImageSmall != null ? imgDirUrl + concatImageSize(deal.mainImageSmall, listSize,  !autoImageUrl) : null;
		deal.listImage = deal.listImage != null && !deal.listImage.isEmpty() ? imgDirUrl + concatImageSize(deal.listImage, listSizeV2, !autoImageUrl) : null;
		deal.image1 = deal.image1 != null ? imgDirUrl + concatImageSize(deal.image1, completeSize, !autoImageUrl) : null;
		deal.image2 = deal.image2 != null ? imgDirUrl + concatImageSize(deal.image2, completeSize, !autoImageUrl) : null;
		deal.image3 = deal.image3 != null ? imgDirUrl + concatImageSize(deal.image3, completeSize, !autoImageUrl) : null;
		deal.image4 = deal.image4 != null ? imgDirUrl + concatImageSize(deal.image4, completeSize, !autoImageUrl) : null;
		deal.image5 = deal.image5 != null ? imgDirUrl + concatImageSize(deal.image5, completeSize, !autoImageUrl) : null;
		deal.image6 = deal.image6 != null ? imgDirUrl + concatImageSize(deal.image6, completeSize, !autoImageUrl) : null;
		deal.image7 = deal.image7 != null ? imgDirUrl + concatImageSize(deal.image7, completeSize, !autoImageUrl) : null;
		deal.image8 = deal.image8 != null ? imgDirUrl + concatImageSize(deal.image8, completeSize, !autoImageUrl) : null;
		deal.image9 = deal.image9 != null ? imgDirUrl + concatImageSize(deal.image9, completeSize, !autoImageUrl) : null;
		deal.image10 = deal.image10 != null ? imgDirUrl + concatImageSize(deal.image10, completeSize, !autoImageUrl) : null;
		
		//If we dont have listImage, use image10
		deal.listImage = deal.listImage != null ? deal.listImage : deal.mainImageBig;
		
	}
    
	/*
	 * Modify the image name to fit the required size
	 * */
	private static String concatImageSize(String image, String size, boolean concatSize){
			if (!image.isEmpty()){
				if (concatSize){
					String[] temp;
				  	String delimiter = "\\.";
				  	temp = image.split(delimiter);
				  	try {
						image = temp[0] + size + "." + temp[1];
					} catch (Exception e) {
						Logger.error("Incorrect image format, be sure it has the image format extension", e);
					}
				}
			}
			
			return image;
	}
	
}
