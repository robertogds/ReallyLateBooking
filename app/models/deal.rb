class Deal < ActiveRecord::Base
attr_accessible :city, :normal_price, :sale_price, :total_price, :hotel_name, :hotel_cat, :room_type, :description, :address, :photo1, :photo2, :photo3
  
  def to_json(options = {})
    super(options.merge(:only => [ :id, :city, :created_at, :normal_price, :sale_price, :total_price, :hotel_name, :hotel_cat, :room_type, :description, :address, :photo1, :photo2, :photo3 ]))
  end
end