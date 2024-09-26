
public class Accommodation {

    public Accommodation() {
    }

    protected int accommodationID;
    private String address;
    private double price;
    private int numofRooms;
    private boolean availStatus;

    public Accommodation fetchDetails() {
        return this;
    }

    protected boolean updateDetails(int newID, String newAddress,
            double newPrice, int newNumofRooms) {
        this.accommodationID = newID;
        this.address = newAddress;
        this.price = newPrice;
        this.numofRooms = newNumofRooms;
        return true;
    }

    protected boolean updateAvailability(boolean status) {
        this.availStatus = status;
        return true;
    }

    public boolean removeAccommodation() {
        this.accommodationID = -1;
        this.address = "";
        this.price = 0;
        this.numofRooms = 0;
        this.availStatus = false;
        return true;
    }

    public void displayDetails() {
        System.out.println("Accommodation ID: " + accommodationID);
        System.out.println("Address: " + address);
        System.out.println("Price: " + price);
        System.out.println("Number of Rooms: " + numofRooms);
        System.out.println("Availability Status: " + (availStatus ? "Vacant" : "Not Vacant"));
        System.out.println();
    }

}
