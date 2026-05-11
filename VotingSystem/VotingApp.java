import java.util.Scanner;

public class VotingApp {
    public static void main(String[] args) {
        VotingSystem system = new VotingSystem();

        // Sample Candidates
        system.addCandidate(new Candidate("1", "Alice Johnson", "Tech Party"));
        system.addCandidate(new Candidate("2", "Bob Smith", "Green Party"));
        system.addCandidate(new Candidate("3", "Charlie Brown", "Innovate Party"));

        // Sample Voters
        system.registerVoter(new Voter("V101", "David"));
        system.registerVoter(new Voter("V102", "Emma"));
        system.registerVoter(new Voter("V103", "Frank"));
        system.registerVoter(new Voter("V104", "Grace"));
        system.registerVoter(new Voter("V105", "Hank"));

        // Load existing votes from file
        system.loadVotes();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Java Voting System!");

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) Cast a vote");
            System.out.println("2) View results");
            System.out.println("3) Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter your Voter ID: ");
                    String vId = scanner.nextLine();
                    System.out.print("Enter Candidate ID: ");
                    String cId = scanner.nextLine();
                    system.castVote(vId, cId);
                    break;
                case "2":
                    system.displayResults();
                    break;
                case "3":
                    System.out.println("Exiting... Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }
}
