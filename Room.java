public class Room {
    private int roomNumber;
    private String roomType;
    private double price;
    private boolean booked;

    public Room(int roomNumber, String roomType, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.booked = false;
    }

    public boolean bookRoom() {
        if (!booked) {
            booked = true;
            return true;
        }
        return false;
    }

    public boolean isBooked() {
        return booked;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("Room %d (%s) - $%.2f", roomNumber, roomType, price);
    }
}