public class Candidate {
    private String id;
    private String name;
    private String party;
    private int voteCount;

    public Candidate(String id, String name, String party) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.voteCount = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void incrementVote() {
        this.voteCount++;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Party: %s | Votes: %d", id, name, party, voteCount);
    }
}
