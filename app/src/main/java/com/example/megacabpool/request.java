package com.example.megacabpool;

public class request{
   private String contact,name,from,to,time,seats,date,email;

   public request(){
       //empty const
   }

    public request(String contact, String name, String from, String to, String time, String seats, String date,String email) {
        this.contact = contact;
        this.name = name;
        this.from = from;
        this.to = to;
        this.time = time;
        this.seats = seats;
        this.date = date;
        this.email=email;
    }

    public request(String name,String contact){
        this.contact = contact;
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public String getName() {
        return name;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTime() {
        return time;
    }

    public String getSeats() {
        return seats;
    }

    public String getDate() {
        return date;
    }
    public String getEmail(){return email;}
    public void setSeats(String seats) {
        this.seats = seats;
    }

}
