import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VotingSystem {
    private List<Candidate> candidates;
    private List<Voter> voters;
    private static final String VOTE_FILE = "votes.txt";

    public VotingSystem() {
        this.candidates = new ArrayList<>();
        this.voters = new ArrayList<>();
    }

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
    }

    public void registerVoter(Voter voter) {
        voters.add(voter);
    }

    public boolean castVote(String voterId, String candidateId) {
        Voter voter = findVoter(voterId);
        Candidate candidate = findCandidate(candidateId);

        if (voter == null) {
            System.out.println("Error: Voter ID " + voterId + " not found.");
            return false;
        }

        if (voter.hasVoted()) {
            System.out.println("Error: Voter " + voter.getName() + " has already voted.");
            return false;
        }

        if (candidate == null) {
            System.out.println("Error: Candidate ID " + candidateId + " not found.");
            return false;
        }

        candidate.incrementVote();
        voter.markAsVoted();
        saveVote(voterId, candidateId);
        System.out.println("Vote successfully cast for " + candidate.getName() + "!");
        return true;
    }

    private void saveVote(String voterId, String candidateId) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(VOTE_FILE, true)))) {
            out.println(voterId + "," + candidateId);
        } catch (IOException e) {
            System.out.println("Error saving vote to file: " + e.getMessage());
        }
    }

    public void loadVotes() {
        File file = new File(VOTE_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String voterId = parts[0];
                    String candidateId = parts[1];

                    Voter voter = findVoter(voterId);
                    Candidate candidate = findCandidate(candidateId);

                    if (voter != null && candidate != null && !voter.hasVoted()) {
                        candidate.incrementVote();
                        voter.markAsVoted();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading votes: " + e.getMessage());
        }
    }

    private Voter findVoter(String voterId) {
        for (Voter v : voters) {
            if (v.getVoterId().equals(voterId)) {
                return v;
            }
        }
        return null;
    }

    private Candidate findCandidate(String candidateId) {
        for (Candidate c : candidates) {
            if (c.getId().equals(candidateId)) {
                return c;
            }
        }
        return null;
    }

    public List<Candidate> getSortedCandidates() {
        List<Candidate> sortedCandidates = new ArrayList<>(candidates);
        Collections.sort(sortedCandidates, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate c1, Candidate c2) {
                return Integer.compare(c2.getVoteCount(), c1.getVoteCount());
            }
        });
        return sortedCandidates;
    }

    public List<Candidate> getCandidates() {
        return new ArrayList<>(candidates);
    }

    public void displayResults() {
        System.out.println("\n--- Voting Results (Sorted by Votes) ---");
        
        // Create a copy to sort without affecting the original list order if needed
        List<Candidate> sortedCandidates = new ArrayList<>(candidates);
        
        // Sort candidates based on vote count in descending order
        Collections.sort(sortedCandidates, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate c1, Candidate c2) {
                return Integer.compare(c2.getVoteCount(), c1.getVoteCount());
            }
        });

        for (Candidate c : sortedCandidates) {
            System.out.println(c.toString());
        }
    }
}
