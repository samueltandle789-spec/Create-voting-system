public class Voter {
    private String voterId;
    private String name;
    private boolean hasVoted;

    public Voter(String voterId, String name) {
        this.voterId = voterId;
        this.name = name;
        this.hasVoted = false;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getName() {
        return name;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void markAsVoted() {
        this.hasVoted = true;
    }
}
