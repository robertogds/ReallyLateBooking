class CreateDeals < ActiveRecord::Migration
  def self.up
    create_table :deals do |t|
      t.string :city
      t.decimal :normal_price
      t.decimal :sale_price
      t.decimal :total_price
      t.integer :quantity
      t.string :hotel_name
      t.integer :hotel_cat
      t.text   :hotel_description
      t.string :room_type
      t.string :address
      t.string :photo1
      t.string :photo2
      t.string :photo3
      t.boolean :active, :default => 0
      t.timestamps
    end
  end

  change_table :deals do |t|
      t.index :created_at
    end
  end

  def self.down
    drop_table :deals
  end
end