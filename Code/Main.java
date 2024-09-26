import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Accommodation> accList = new ArrayList<>();
        boolean option = true;
        String accAddr;
        double price;
        int numOfRooms;
        boolean availStatus;

        while (option) {
            System.out.println("Enter Accommodation Address: ");
            StringBuilder addressBuilder = new StringBuilder();
            String line;
            while (!(line = sc.nextLine()).isEmpty()) {
                addressBuilder.append(line).append("\n");
            }
            accAddr = addressBuilder.toString().trim();

            System.out.print("Enter the Rent: ");
            price = sc.nextDouble();

            System.out.print("Enter the number of rooms: ");
            numOfRooms = sc.nextInt();

            System.out.print("Is the property vacant? (y/n) ");
            sc.nextLine();
            String temp = sc.nextLine();
            availStatus = temp.equalsIgnoreCase("y");

            Accommodation acc = new Accommodation();
            acc.updateDetails(accList.size() + 1, accAddr, price, numOfRooms);
            acc.updateAvailability(availStatus);
            accList.add(acc);

            System.out.println("Do you want to add another property(y/n): ");
            temp = sc.nextLine();
            if (!temp.equalsIgnoreCase("y")) {
                option = false;
            }
        }

        sc.close();

        for (Accommodation accommodation : accList) {
            accommodation.displayDetails();
        }
    }
}
