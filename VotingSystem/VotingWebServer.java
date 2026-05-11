import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class VotingWebServer {
    private static VotingSystem system;

    public static void main(String[] args) throws IOException {
        system = new VotingSystem();

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

        // Load existing votes
        system.loadVotes();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/api/candidates", new CandidatesHandler());
        server.createContext("/api/vote", new VoteHandler());
        server.createContext("/api/results", new ResultsHandler());
        server.setExecutor(null);

        System.out.println("Server started at http://localhost:8080");
        server.start();
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File file = new File("VotingSystem/index.html");
            if (!file.exists()) {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes);
            fis.close();

            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class CandidatesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "[" + system.getCandidates().stream()
                    .map(c -> String.format("{\"id\":\"%s\",\"name\":\"%s\",\"party\":\"%s\"}", c.getId(), c.getName(), c.getParty()))
                    .collect(Collectors.joining(",")) + "]";
            
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        }
    }

    static class ResultsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "[" + system.getSortedCandidates().stream()
                    .map(c -> String.format("{\"name\":\"%s\",\"party\":\"%s\",\"voteCount\":%d}", c.getName(), c.getParty(), c.getVoteCount()))
                    .collect(Collectors.joining(",")) + "]";

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        }
    }

    static class VoteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            
            String voterId = "";
            String candidateId = "";
            
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    if (kv[0].equals("voterId")) voterId = kv[1];
                    if (kv[0].equals("candidateId")) candidateId = kv[1];
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream oldOut = System.out;
            System.setOut(ps);
            
            boolean success = system.castVote(voterId, candidateId);
            
            System.setOut(oldOut);
            String output = baos.toString().trim();
            
            exchange.sendResponseHeaders(200, output.length());
            OutputStream os = exchange.getResponseBody();
            os.write(output.getBytes());
            os.close();
        }
    }
}
