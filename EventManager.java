package service;

import model.Event;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class EventManager {
    private Map<Integer, Event> events = new HashMap<>();
    private int nextId = 1;
    private final String DATA_FILE = "events.data";

    public EventManager() {
        loadFromFile();
    }

    public synchronized boolean addEvent(Event e) {
        events.put(e.getId(), e);
        nextId = Math.max(nextId, e.getId() + 1);
        saveToFile();
        return true;
    }

    public synchronized int getNextId() { return nextId++; }

    public List<Event> getAllSortedByTime() {
        return events.values().stream()
                .sorted(Comparator.comparing(Event::getHorario))
                .collect(Collectors.toList());
    }

    public Event getEventById(int id) { return events.get(id); }

    public boolean joinEvent(int eventId, String email) {
        Event e = events.get(eventId);
        if (e == null) return false;
        e.addParticipant(email);
        saveToFile();
        return true;
    }

    public boolean leaveEvent(int eventId, String email) {
        Event e = events.get(eventId);
        if (e == null) return false;
        e.removeParticipant(email);
        saveToFile();
        return true;
    }

    public void loadFromFile() {
        events.clear();
        try {
            if (!Files.exists(Paths.get(DATA_FILE))) return;
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                Event e = Event.fromDataLine(line);
                if (e != null) {
                    events.put(e.getId(), e);
                    nextId = Math.max(nextId, e.getId() + 1);
                }
            }
        } catch (IOException ex) {
            System.err.println("Erro ao carregar events.data: " + ex.getMessage());
        }
    }

    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Event e : getAllSortedByTime()) {
                pw.println(e.toDataLine());
            }
        } catch (IOException ex) {
            System.err.println("Erro ao salvar events.data: " + ex.getMessage());
        }
    }
}
