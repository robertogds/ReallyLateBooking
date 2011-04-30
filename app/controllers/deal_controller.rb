class DealController < ApplicationController
  def index
     render :json => Deal.all
   end

   def show
     render :json => Deal.find(params[:id])
   end

   def create
     deal = Deal.create! params
     render :json => deal
   end

   def update
     deal = Deal.find(params[:id])
     deal.update_attributes! params
     render :json => deal
   end
  
   def destroy
    deal = Deal.find(params[:id])
    deal.destroy
    render :json => deal
  end

end
