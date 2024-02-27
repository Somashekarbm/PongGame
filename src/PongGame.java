public class PongGame {

    public static void main(String[] args) {

        DatabaseManager manager = new DatabaseManager("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
        // Pass DatabaseManager to PongMainPage constructor
        new PongMainPage(manager);

    }
}
