import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HotelManagementSystem {
    private List<Room> rooms;
    private List<String> bookingHistory;

    public HotelManagementSystem() {
        rooms = new ArrayList<>();
        bookingHistory = new ArrayList<>();

        // Create rooms dynamically
        for (int i = 101; i <= 200; i++) {
            String roomType = (i % 2 == 0) ? "Double" : "Single"; // Example logic for room type
            double price = (roomType.equals("Single")) ? 100 : 150; // Example pricing
            rooms.add(new Room(i, roomType, price));
        }
        loadBookingHistory();
    }

    public void bookRoom(String customerName, String contact, int roomNumber) {
        Customer customer = new Customer(customerName, contact);
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);

        if (room != null) {
            Booking booking = new Booking(room, customer);
            if (booking.processBooking()) {
                String bookingDetails = booking.toString();
                bookingHistory.add(bookingDetails);
                saveBookingHistory();
                JOptionPane.showMessageDialog(null, "Booking successful:\n" + bookingDetails);
            } else {
                JOptionPane.showMessageDialog(null, "Room " + room + " is already booked.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Room " + roomNumber + " does not exist.");
        }
    }

    private void saveBookingHistory() {
        new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("booking_history.txt"))) {
                for (String record : bookingHistory) {
                    writer.write(record);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadBookingHistory() {
        File file = new File("booking_history.txt");
        if (file.exists()) {
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        bookingHistory.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Hotel Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new GridLayout(8, 2)); // Increased rows to 8 for the clear history button

        JLabel nameLabel = new JLabel("Customer Name:");
        JTextField nameField = createTextField("Enter customer name");
        JLabel contactLabel = new JLabel("Contact Number:");
        JTextField contactField = createTextField("Enter contact number");
        JLabel roomLabel = new JLabel("Room Number:");
        JTextField roomField = createTextField("Enter room number(101-200)");
        JButton bookButton = new JButton("Book Room");
        JButton historyButton = new JButton("View Booking History");
        JButton clearButton = new JButton("Clear Fields"); // Clear fields button
        JButton clearHistoryButton = new JButton("Clear History"); // Clear history button
        JButton exitButton = new JButton("Exit"); // Exit button

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String contact = contactField.getText();
                int roomNumber;

                try {
                    roomNumber = Integer.parseInt(roomField.getText());
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            bookRoom(name, contact, roomNumber);
                            return null;
                        }
                    }.execute();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid room number.");
                }
            }
        });

        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayBookingHistory(); // Call the new method to display booking history
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                contactField.setText("");
                roomField.setText("");
            }
        });

        clearHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookingHistory.clear(); // Clear the booking history
                saveBookingHistory(); // Save the empty history to the file
                JOptionPane.showMessageDialog(null, "Booking history cleared.");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application
            }
        });

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(contactLabel);
        frame.add(contactField);
        frame.add(roomLabel);
        frame.add(roomField);
        frame.add(bookButton);
        frame.add(historyButton);
        frame.add(clearButton); // Add the clear fields button to the frame
        frame.add(clearHistoryButton); // Add the clear history button to the frame
        frame.add(exitButton); // Add the exit button to the frame

        frame.setVisible(true);
    }

    private void displayBookingHistory() {
        JFrame historyFrame = new JFrame("Booking History");
        historyFrame.setSize(600, 400);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        StringBuilder history = new StringBuilder("Booking History:\n\n");
        for (String record : bookingHistory) {
            history.append(record).append("\n");
        }

        historyArea.setText(history.toString());

        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyFrame.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                historyFrame.dispose();
            }
        });
        historyFrame.add(closeButton, BorderLayout.SOUTH);

        historyFrame.setVisible(true);
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setBorder(new LineBorder(Color.GRAY, 1));
        textField.setBorder(BorderFactory.createCompoundBorder(
            textField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        textField.setText(placeholder);
        textField.setForeground(Color.LIGHT_GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.LIGHT_GRAY);
                    textField.setText(placeholder);
                }
            }
        });

        return textField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelManagementSystem hms = new HotelManagementSystem();
            hms.createAndShowGUI();
        });
    }
}