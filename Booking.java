public class Booking {
    private Room room;
    private Customer customer;

    public Booking(Room room, Customer customer) {
        this.room = room;
        this.customer = customer;
    }

    public boolean processBooking() {
        return room.bookRoom();
    }

    public String getStatus() {
        return room.isBooked() ? "Booked" : "Available";
    }

    public Room getRoom() {
        return room;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return String.format("|        %s        |        %s        |        %s        |        %d        |        %s        |        %.2f        |",
                getStatus(), customer.getName(), customer.getContact(), room.getRoomNumber(),
                room.getRoomType(), room.getPrice());
    }
    public static String getHeader() {
        return String.format("|        Status       |       Customer Name       |       Contact Number       |       Room Number       |       Room Type       |       Price       |");
    }
}