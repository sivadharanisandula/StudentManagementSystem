import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.util.*;

class Student {
    int id;
    String name;
    String dept;

    Student(int id, String name, String dept) {
        this.id = id;
        this.name = name;
        this.dept = dept;
    }

    @Override
    public String toString() {
        return id + "|" + name + "|" + dept;
    }
}

public class Server {

    static List<Student> students = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);

        // GET all students
        server.createContext("/students", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            StringBuilder response = new StringBuilder();
            for (Student s : students) {
                response.append(s.toString()).append(",");
            }

            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.toString().getBytes());
            exchange.close();
        });

        // ADD student
        server.createContext("/add", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String[] data = body.split(",");

                if (data.length == 3) {
                    int id = Integer.parseInt(data[0].trim());
                    String name = data[1].trim();
                    String dept = data[2].trim();
                    students.add(new Student(id, name, dept));
                }
            }

            String response = "Added";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        // DELETE student by index
        server.createContext("/delete", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("index=")) {
                int index = Integer.parseInt(query.split("=")[1]);
                if (index >= 0 && index < students.size()) {
                    students.remove(index);
                }
            }

            String response = "Deleted";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        // UPDATE student by index
        server.createContext("/update", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("index=")) {
                int index = Integer.parseInt(query.split("=")[1]);

                if ("POST".equals(exchange.getRequestMethod())) {
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    String[] data = body.split(",");
                    if (data.length == 3) {
                        int id = Integer.parseInt(data[0].trim());
                        String name = data[1].trim();
                        String dept = data[2].trim();

                        if (index >= 0 && index < students.size()) {
                            students.set(index, new Student(id, name, dept));
                        }
                    }
                }
            }

            String response = "Updated";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        server.setExecutor(null); // default executor
        server.start();
        System.out.println("Server running at http://localhost:9090");
    }
}