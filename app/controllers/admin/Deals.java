package controllers.admin;

import helper.hotusa.HotUsaApiHelper;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.Booking;
import models.City;
import models.Company;
import models.Deal;
import models.User;
import play.Logger;
import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import controllers.Security;
import controllers.CRUD.ObjectType;

@Check(Security.EDITOR_ROLE)
@With(Secure.class)
@CRUD.For(Deal.class)
public class Deals extends controllers.CRUD {
	
	public static void refreshHotUsaPrices(){
		List<City> cities = City.findActiveCities();
		HotUsaApiHelper.getHotelPricesByCityList(cities);
		redirect("/admin/deals");
	}
	
	private static void reparePrices(){
		List<Booking> bookings = Booking.all().fetch();
		for (Booking booking: bookings){
			booking.totalSalePrice =  booking.totalSalePrice != null ? booking.totalSalePrice: booking.salePriceCents;
			if (booking.netTotalSalePrice == null){
				booking.deal = Deal.findById(booking.deal.id);
				int price = booking.totalSalePrice != null ? booking.totalSalePrice: booking.salePriceCents;
				int fee = 12;
				if (booking.deal != null && booking.deal.isHotUsa){
					booking.netTotalSalePrice = calculateNetPriceByFee(price, fee);
				}
				else{
					if (booking.company != null){
						Company company = Company.findById(booking.company.id);
						if (company != null && company.fee != null){
							fee = company.fee;
						}
					}
					booking.netTotalSalePrice = calculateNetPriceByFee(price, fee);
				}
			}
			booking.update();
		}
	}
	
	private static Float calculateNetPriceByFee(Integer price, Integer fee){
		try {
			if (price != null) {
				return new Float(price - (price * fee / 100.0));
			}
		} catch (Exception e) {
			Logger.error("Error calculating net price: %s", e);
		}
		return null;
	}
	
	public static void exportAll() {
		ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        List<Deal> deals = Deal.all().fetch();
		for (Deal deal: deals){
			if (deal.company != null){
				deal.company = Company.findById(deal.company.id);
			}
			if (deal.owner != null){
				deal.owner = User.findById(deal.owner.id);
			}
			if (deal.city != null){
				deal.city = City.findById(deal.city.id);
			}
		}
        List objects = deals;

        render("admin/export.csv", objects, type);
    }
	
	public static void createHotusaHotel(String hotelCode){
		Deal deal = HotUsaApiHelper.getHotelInfo(hotelCode);
		redirect("/admin/deals/%s", deal.id);
	}
	
	
}


